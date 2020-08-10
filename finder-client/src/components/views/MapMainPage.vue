<template>
  <div class="mapPage">
     <v-card flat class="mx-auto" color="white" width="1800px" height="800px">
      <v-card-text class="mapCard">
        <v-divider></v-divider>
        <MapStoreFilters
          :storeFilter="storeFilter"
          :location="location"
          @setFilters="setFilters"
        />
        <div class="storeLocator" id="map"/>
        <MapStoreSearchPanel 
          ref="searchPanel"
          :foundStores="foundStores"
          :storeTypes="storeTypes"
          :address="address"
          :selectedStore="selectedStore"
          :loading="loading"
          @setStoreTypes="setStoreTypes"
          @setAddress="setAddress"
          @searchAddress="searchAddress"
          @setSelectedStore="setSelectedStore"
          @initAutoComplete="initAutoComplete"
        />
      </v-card-text>
    </v-card>
  </div>
</template>

<script>

 /* 
  * Main page of the store finder application. It loads up all the other needed components, dealing with the business logic in a centralized manner. 
  * Although creating reusable components that only call $emit (so that the page that uses it can decide how to act on it) is a good way to decouple the front-end, 
  * I believe something should be done about the size of the MapStoreSearchPanel as it has too many objects, which is an indication that it could have been broken up better.
  * Having said that, keeping it as a unit makes it easier to move it around if necessary and I don't see much of a point of breaking it up, 
  * unless we plan to allow different combinations of headers and bodies.
  * 
  * Anyway here's an elephant:
  *   _    _
  *  /=\""/=\
  * (=(0_0 |=)__
  *  \_\ _/_/   )
  *    /_/   _  /\
  *   |/ |\ || |
  *      ~ ~  ~
  */
import * as axios from "axios";
import http from "../../utils/http";
import config from "../../config";
import gmapsInit from '../../utils/map';

import MapStoreSearchPanel from "./MapStoreSearchPanel";
import MapStoreFilters from "./MapStoreFilters"

// default properties for requests
axios.defaults.headers.common = { "X-Requested-With": "XMLHttpRequest" };
axios.defaults.baseURL = `${config.baseUrl}/`;

export default {
  name: "MapMainPage",
  components: {
    MapStoreSearchPanel,
    MapStoreFilters
  },
  data: () => ({
    google: {}, 
    gmap: {}, // the google map object
    geocoder: {},
    searchParameters: {}, // the last search parameters (to avoid re-submitting the same query)
    selectedStore: -1, // the last selected store (to prevent highlighting the same marker more than once)
    markers: [], // map of markers
    address: "", // selected address
    location: {},
    viewport: {},
    storeFilter: 0, // search filters [all, 5 nearest and favorite]
    storeTypes: [0, 1, 2], // store types [store, pick-up point and drive through]
    foundStores: [], // list of queried stores, hardcoded for now
    loading: false, // indicates whether there's an active query
    markerIcons: {
      Supermarkt: require("../../assets/img/store.webp"),
      SupermarktPuP: require("../../assets/img/pup-store.webp"),
      PuP: require("../../assets/img/pup.webp"),
    }, 
    highlightedMarkerIcons: {
      Supermarkt: require("../../assets/img/pin-store.webp"),
      SupermarktPuP: require("../../assets/img/pin-pup-store.webp"),
      PuP: require("../../assets/img/pin-pup.webp"),
    },
    hasFiltered: false, 
    defaultLat: 52.370216, // in case it all goes sour, use the center of the Netherlands as a fallback base location
    defaultLng: 4.895168,
    defaultAddress: "Netherlands",
  }),
  methods: {
    // synch changes on the address text-field, but do not submit a new search
    setAddress(address) {
      this.address = address;
    },

    // handle the MapStoreTypes click events
    setStoreTypes(types) {
      this.storeTypes = types;
      this.queryStores();
    },

    // handle the MapStoreFilters click events
    setFilters(filter) {
      this.storeFilter = filter;
      this.queryStores();
    },

    // handle manual address search (enter key or pressing the button)
    searchAddress() {
      this.geocoder.geocode({ address: this.address }, (results, status) => {
          // if the provided address is valid, get the lat/lng and search
          if (status == `OK` && results[0]) {
            this.location = results[0].geometry.location;
            this.viewport = results[0].geometry.viewport; 
           
          }
          this.queryStores();
          this.gmap.panTo(this.location);
          this.gmap.fitBounds(this.viewport);
      });
    },

    // update the foundStores list
    queryStores() {
      // display the search results after the user filters for the first time
      if(!this.hasFiltered) {
        this.displaySearchResults();
      }

      // wrap the search parameters
      const newSearchParameters = {
        "address": this.address,
        "storeFilter": this.storeFilter,
        "Supermarkt": this.storeTypes.includes(0),
        "PuP": this.storeTypes.includes(1),
        "SupermarktPuP": this.storeTypes.includes(2)
      };

      // if the search parameters didn't change, prevent a request
      if(Object.entries(this.searchParameters).toString() === Object.entries(newSearchParameters).toString()) {
        return;
      }

      // update the search parameters to avoid duplicate requests
      this.loading = true;
      this.searchParameters = newSearchParameters;

      // query all stores (with filters)
      this.executeQuery(newSearchParameters);
    },

    // Query all stores based on the user-provided parameters (address, store types, filter type, etc)
    // Querying data does not require the JWT token to be passed.
    executeQuery(searchParameters) {
      // build the url based on the provided parameters
      let url = "store/stores/";
      url += (this.location.lat) ? "nearest/?lng=" + this.location.lng() + '&lat=' + this.location.lat() : "?";

      // if search parameters are supplied, add them to the url
      if(searchParameters) {
        if(searchParameters.storeFilter == 1) {
          url += "&limit=5";
        }
        if(searchParameters.Supermarkt) {
          url += "&storeTypes=Supermarkt";
        }
        if(searchParameters.PuP) {
          url += "&storeTypes=PuP";
        }
        if(searchParameters.SupermarktPuP) {
          url += "&storeTypes=SupermarktPuP";
        }
      }

      // query data and update the markers
      http.get(url).then(({ data }) => {
        this.foundStores = data;
        this.resetSelection()
        this.setupStores();
        this.loading = false;
      });
    },

    // process the found stores, adding needed properties and creating markers
    setupStores() {
      this.gmap.clearOverlays();
      var index = 0;
      this.foundStores.map((store) => {
          // set the base properties
          store.icon = this.markerIcons[store.locationType];
          store.markerId = index++;
          this.formatDistance(store);

          // build the store directions url. if no location was provided, use the default Netherlands lat/lng as the "from" location
          if(this.location.lat) {
            store.directions = `https://maps.apple.com/?saddr=${this.location.lat()},${this.location.lng()}&daddr=${store.position.lat},${store.position.lng}`;
          } else {
            store.directions = `https://maps.apple.com/?&daddr=${store.position.lat},${store.position.lng}`;
          }

          // create a marker based on the store object
          const map = this.gmap;
          const marker = new this.google.maps.Marker({ ...store, map });
          this.markers[marker.markerId] = marker;
          marker.addListener(`click`, () => this.markerClickHandler(marker));
          return marker;
      });
    },

    // handle store selection on the search panel
    setSelectedStore(id) {
      if(id != undefined && id != this.selectedStore) {
        const marker = this.markers[id];
        this.markerClickHandler(marker, true);
      }
    },

    // add basic marker click handling
    markerClickHandler(marker, panelClick) {
      // display the search results after the user selects a marker for the first time
      if(!this.hasFiltered) {
        this.displaySearchResults();
      }

      // remove the highlight from the current marker
      if(this.selectedStore > 0) {
        this.unhighlightMarker(this.markers[this.selectedStore]);
      }

      // position the map on the new marker, and highlight it
      this.selectedStore = marker.markerId;
      this.moveToPosition(marker);
      this.highlightMarker(marker);

      // scroll the search panel to the selected store if the event came from the map
      if(!panelClick) {
        this.scrollToSearchResult(marker.sapStoreID);
      }
    },

    // navigate to a position
    moveToPosition(marker) {
      this.gmap.panTo(marker.getPosition());
      this.gmap.setZoom(13);
    },

    // change the marker icon and animate it
    highlightMarker(marker) {
      // change the icon of the newly selected marker
      marker.setIcon(this.highlightedMarkerIcons[marker.locationType]);

      // animate it
      this.toggleBounce(marker);
      setTimeout(() => { this.toggleBounce(marker) }, 1500);
    },

    // change the marker icon back to its original
    unhighlightMarker(marker) {
      if(marker) {
        marker.setIcon(this.markerIcons[marker.locationType]);
      }
    },

    // toggles on/off the bounce animation on a marker
    toggleBounce (marker) {
      if (marker.getAnimation() != null) {
          marker.setAnimation(null);
      } else {
          marker.setAnimation(this.google.maps.Animation.BOUNCE);
      }
    },

    // Format the distance (km and meters)
    // For values below 1km and above 100km, format with no decimal cases; for everything in-between, allow 1 decimal case.
    // Example: 10.32 => 10m, 100000.32 => 100km, 35000.23123 => 35.2km. 
    // When things are nearby or too far, decimal cases makes little difference, even more so when we're considering unobstructed distance.
    formatDistance(store) {
      if(store.distance) {
        if(store.distance < 1000) {
          store.distance = Number(store.distance).toFixed(0) + ' m';
        } else {
          const dist = (store.distance / 1000);
          store.distance = (dist > 100 ? (dist).toFixed(0) : (dist).toFixed(1)) + ' km';
        }
      }
    },

    // scroll to the search result (on the search panel)
    // it is not perfect, but it works ok for most cases
    scrollToSearchResult(id) {
      // only scroll if the container is scrollable
      var scrollContainer = document.getElementById("searchResults");
      var store = document.getElementById(id).parentNode;
      scrollContainer.scrollTop = store.offsetTop;
    },

    // reset the selection and search results scroll after a new set of results is queried
    resetSelection() {
      this.selectedStore = -1;
      var scrollContainer = document.getElementById("searchResults");
      scrollContainer.scrollTop = 0;
    },

    // display the search results expandable panel
    displaySearchResults() {
      document.getElementById("searchBodyWrapper").style.display = "";
      this.hasFiltered = true;
    },

    // loading autocomplete only when the user focus on the input to ensure everything is loaded up nicely and to avoid initializing it if left unused
    initAutoComplete() {
      var input = document.getElementById('searchInput');
      var options = {
          componentRestrictions: {country: 'nl'}
      };
      const autocomplete = new this.google.maps.places.Autocomplete(input, options);

      // listen for changes on the autocomplete control, capturing the address and fetching a new list of stores
      autocomplete.addListener('place_changed', () => {
        this.loading = true;
        let place = autocomplete.getPlace();
        if(place.formatted_address) {
          this.address = place.formatted_address;
          this.location = place.geometry.location;
          this.viewport = place.geometry.viewport;

          // query and adjust the map
          this.queryStores();
          this.gmap.panTo(this.location);
          this.gmap.fitBounds(this.viewport);
        }
      });
    },
  },

  // google maps definitions
  async mounted() {
    try {
      // initialize the map
      const google = await gmapsInit();
      const geocoder = new google.maps.Geocoder();
      const map = new google.maps.Map(document.getElementById("map"), {
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false
      });

      // store google map properties
      this.geocoder = geocoder;
      this.gmap = map;
      this.google = google;

      // set the initial zoom to a minum of 8
      google.maps.event.addListener(map, 'zoom_changed', function() {
          var zoomChangeBoundsListener = 
              google.maps.event.addListener(map, 'bounds_changed', function() {
                  if (this.getZoom() < 8 && this.initialZoom == true) {
                      // Change max/min zoom here
                      this.setZoom(8);
                      this.initialZoom = false;
                  }
              google.maps.event.removeListener(zoomChangeBoundsListener);
          });
      });
      map.initialZoom = true;

      // add a method that allows clearing the markers
      google.maps.Map.prototype.clearOverlays = () => {
        for (var i = 0; i < this.markers.length; i++ ) {
          this.markers[i].setMap(null);
        }
        this.markers.length = 0;
      }

      // query the initial stores based on the default Netherlands lat/lng, but do not display them yet
      http.get("store/stores/nearest/?lng=" + this.defaultLng + '&lat=' + this.defaultLat).then(({ data }) => {
        this.foundStores = data;
        this.setupStores();
      });

      //  enables the visibility of the search panel upon loading
      google.maps.event.addDomListener(window, 'load', function(){
          document.getElementById('searchContainer').style.visibility = 'show';
      });  

      // centralize it at the Netherlands
      geocoder.geocode({ address: `Netherlands` }, (results, status) => {
        if (status !== `OK` || !results[0]) {
          throw new Error(status);
        }

        map.setCenter(results[0].geometry.location);
        map.fitBounds(results[0].geometry.viewport);
      });

      // move the search bar within the map
      var searchContainer = document.getElementById('searchContainer');
      map.controls[google.maps.ControlPosition.TOP_LEFT].push(searchContainer);
      
      // adjust the control panel
      var controlDiv = document.getElementById('floating-panel');
      map.controls[google.maps.ControlPosition.TOP_CENTER].push(controlDiv);
    } catch (error) {
      console.error(error);
    }
  },
};
</script>
