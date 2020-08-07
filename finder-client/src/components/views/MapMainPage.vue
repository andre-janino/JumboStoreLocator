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
          :foundStores="foundStores"
          :storeTypes="storeTypes"
          :address="address"
          :selectedStore="selectedStore"
          @setStoreTypes="setStoreTypes"
          @setAddress="setAddress"
          @queryStores="queryStores"
          @setSelectedStore="setSelectedStore"
        />
      </v-card-text>
    </v-card>
  </div>
</template>

<script>
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
    searchParameters: {}, // the last search parameters (to avoid re-submitting the same query)
    selectedStore: -1, // the last selected store (to prevent highlighting the same marker more than once)
    markers: [], // map of markers
    address: "", // selected address
    storeFilter: 0, // search filters [all, 5 nearest and favorite]
    storeTypes: [0, 1, 2], // store types [store, pick-up point and drive through]
    foundStores: [ // list of queried stores, hardcoded for now
      {
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
      },
    ],
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
    queryStores() {
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

      // TODO query the stores
      console.log('Query for: ' + this.address);

      // update the search parameters
      this.searchParameters = newSearchParameters;
    },

    // handle store selection on the search panel
    setSelectedStore(id) {
      if(id != undefined && id != this.selectedStore) {
        const marker = this.markers[id];
        this.markerClickHandler(marker);
      }
    },

    // add basic marker click handling
    markerClickHandler(marker) {
      // remove the highlight from the current marker
      this.unhighlightMarker(this.markers[this.selectedStore]);

      // position the map on the new marker, and highlight it
      this.selectedStore = marker.markerId;
      this.moveToPosition(marker);
      this.highlightMarker(marker);
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

    toggleBounce (marker) {
      if (marker.getAnimation() != null) {
          marker.setAnimation(null);
      } else {
          marker.setAnimation(this.google.maps.Animation.BOUNCE);
      }
    }
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

      //  enables the visibility of the search panel upon loading and add autocomplete to the search input
      const self = this;
      google.maps.event.addDomListener(window, 'load', function(){
          // display the search panel
          document.getElementById('searchContainer').style.visibility = 'show';

          // enable the autocomplete operation
          var input = document.getElementById('searchInput');
          var options = {
              componentRestrictions: {country: 'nl'}
          };
          const autocomplete = new google.maps.places.Autocomplete(input, options);

          // listen for changes on the autocomplete control, capturing the address and fetching a new list of stores
          autocomplete.addListener('place_changed', () => {
            let place = autocomplete.getPlace();
            if(place.formatted_address) {
              self.address = place.formatted_address;
              self.queryStores();
            }

            // coordinates, in case we decide to use them directly
            // let lat = place.geometry.location.lat();
            // let lon = place.geometry.location.lng();
          }, self);
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

      // plot existing markers
      var index = 0;
      this.foundStores.map((store) => {
          store.icon = this.markerIcons[store.locationType];
          store.markerId = index++;
          const marker = new google.maps.Marker({ ...store, map });
          this.markers[marker.markerId] = marker;
          marker.addListener(`click`, () => this.markerClickHandler(marker));
          return marker;
        });

      this.gmap = map;
      this.google = google;
    } catch (error) {
      console.error(error);
    }
  },
};
</script>
