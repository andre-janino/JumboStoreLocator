<template>
  <div class="mapPage">
     <v-card flat class="mx-auto" color="white" width="1800px" height="800px">
      <v-card-text class="mapCard">
        <v-divider></v-divider>
        <MapStoreFilters
          :storeFilter="storeFilter"
          @setFilters="setFilters"
        />
        <div class="storeLocator" id="map"/>
        <MapStoreSearchPanel 
          ref="searchPanel"
          :foundStores="foundStores"
          :storeTypes="storeTypes"
          :address="address"
          :selectedStore="selectedStore"
          @setStoreTypes="setStoreTypes"
          @setAddress="setAddress"
          @queryStores="queryStores"
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
    defaultLat: 52.370216, // in case it all goes sour, use the center of the Netherlands as a fallback base location
    defaultLng: 4.895168,
    defaultAddress: "Netherlands",
  }),
  methods: {
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

    // synch changes on the address text-field, but do not submit a new search
    setAddress(address) {
      this.address = address;
    },

    // update the foundStores list
    queryStores(hasLatLng) {
      let newSearchParameters = {
        "address": this.address,
        "storeFilter": this.storeFilter,
        "store": this.storeTypes.includes(0),
        "pickUp": this.storeTypes.includes(1),
        "driveThrough": this.storeTypes.includes(2)
      };

      // if the search parameters didn't change, prevent a request
      if(Object.entries(this.searchParameters).toString() === Object.entries(newSearchParameters).toString()) {
        return;
      }

      // set lat/lng defaults
      newSearchParameters.lat = this.defaultLat;
      newSearchParameters.lat = this.defaultLng;

      // if we do not have the most up to date location, query the lat/lng from the address string
      if(!hasLatLng) {
        this.geocoder.geocode({ address: this.address }, (results, status) => {
          // if the provided address is valid, setup the lat/lng on the search parameters
          if (status == `OK` && results[0]) {
            this.location = results[0].geometry.location;
            newSearchParameters.lat = this.location.lat();
            newSearchParameters.lng = this.location.lng();
            this.viewport = results[0].geometry.viewport;
          } 
          this.updateMap(newSearchParameters);
        });
      } else {
        // in case we already have the lat/lng, add it to the search parameters
        newSearchParameters.lat = this.location.lat();
        newSearchParameters.lng = this.location.lng();
        
        // centralize the map at the new location
        this.updateMap(newSearchParameters); 
      }

      // update the search parameters
      this.searchParameters = newSearchParameters;
    },

    updateMap(searchParameters) {
      // centralize the map at the new location
      this.gmap.panTo(this.location);
      this.gmap.fitBounds(this.viewport);

      // query the stores and setup the markers
      this.foundStores = this.executeQuery(searchParameters);
      this.setupStores();
    },

    // TODO: call the backend for the data
    executeQuery(searchParameters) {
      console.log(searchParameters);
      return [{
        "city":"'s Gravendeel",
        "postalCode":"3295 BD",
        "street":"Kerkstraat",
        "street2":"37",
        "street3":"",
        "addressName":"Jumbo 's Gravendeel Gravendeel Centrum",
        "uuid":"EOgKYx4XFiQAAAFJa_YYZ4At",
        "position": {
          "lat": 51.778461,
          "lng": 4.615551,
        },
        "complexNumber":"33249",
        "showWarningMessage":true,
        "todayOpen":"08:00",
        "locationType":"SupermarktPuP",
        "collectionPoint":true,
        "sapStoreID":"3605",
        "todayClose":"20:00",
        "distance":"100m",
        "favorite":true
      },
      {
        "city":"'s-Heerenberg",
        "postalCode":"7041 JE",
        "street":"Stadsplein",
        "street2":"71",
        "street3":"",
        "addressName":"Jumbo 's-Heerenberg Stadsplein",
        "uuid":"7ewKYx4Xqp0AAAFIHigYwKrH",
        "position": {
          "lat": 51.923993,
          "lng": 6.576066,
        },
        "complexNumber":"30170",
        "showWarningMessage":true,
        "todayOpen":"08:00",
        "locationType":"Supermarkt",
        "sapStoreID":"4670",
        "todayClose":"21:00",
        "distance":"1km",
        "favorite":false
      }];
    },

    // process the found stores, adding needed properties and creating markers
    setupStores() {
      this.gmap.clearOverlays();
      var index = 0;
      this.foundStores.map((store) => {
          // set the base properties
          store.icon = this.markerIcons[store.locationType];
          store.markerId = index++;
           
          // build the store directions url. if no location was provided, use the default Netherlands lat/lng as the "from" location
          if(this.location.lat) {
            store.directions = `https://maps.apple.com/?saddr=${this.location.lat()},${this.location.lng()}&daddr=${store.position.lat},${store.position.lng}`;
          } else {
            store.directions = `https://maps.apple.com/?saddr=${this.defaultLat},${this.defaultLng}&daddr=${store.position.lat},${store.position.lng}`;
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

    // scroll to the search result (on the search panel)
    // it is not perfect, but it works ok for most cases
    scrollToSearchResult(id) {
      // only scroll if the container is scrollable
      var scrollContainer = document.getElementById("searchResults");
      if(scrollContainer.scrollHeight > scrollContainer.clientHeight) {
        return;
      }
      
      // calculate the position
      var target = document.getElementById(id);
      var targetY = 0;
      do { 
          if (target == scrollContainer) break;
          targetY += target.offsetTop;
      } while ((target = target.offsetParent));

      // define a "smooth" scroll function
      const scroll = function(c, a, b, i) {
          i++; if (i > 30) return;
          c.scrollTop = a + (b - a) / 30 * i;
          setTimeout(function(){ scroll(c, a, b, i); }, 20);
      }
      // start scrolling
      scroll(scrollContainer, scrollContainer.scrollTop, targetY, 0);
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
        let place = autocomplete.getPlace();
        if(place.formatted_address) {
          this.address = place.formatted_address;
          this.location = place.geometry.location;
          this.viewport = place.geometry.viewport;
          this.queryStores(true);
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

      this.geocoder = geocoder;
      this.gmap = map;
      this.google = google;

      // query the base location
      geocoder.geocode({ address: this.defaultAddress }, (results, status) => {
        if (status == `OK` && results[0]) {
          // get the location and update the default lat/lng as a baseline
          this.location = results[0].geometry.location;
          this.defaultLat = this.location.lat();
          this.defaultLng = this.location.lng();
          this.viewport = results[0].geometry.viewport;

          // query the data
          this.queryStores();
        } else {
          // if no location is found, query the stores based on the default lat/lng
          this.queryStores();
        }
      });

      // set the default zoom after loading
      google.maps.event.addListenerOnce(map, 'zoom_changed', function() {
          map.setZoom(8);
      });

      // add a method that allows clearing the markers
      google.maps.Map.prototype.clearOverlays = () => {
        for (var i = 0; i < this.markers.length; i++ ) {
          this.markers[i].setMap(null);
        }
        this.markers.length = 0;
      }

      //  enables the visibility of the search panel upon loading
      google.maps.event.addDomListener(window, 'load', function(){
        document.getElementById('searchContainer').style.visibility = 'show';
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
