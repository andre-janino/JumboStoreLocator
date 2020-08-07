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
    searchParameters: { // the last search parameters (to avoid re-submitting the same query)
      "address": "",
      "storeFilter": "",
      "store": "",
      "pickUp": "",
      "driveThrough": ""
    },
    selectedStore: "", // the last selected store (to prevent highlighting the same marker more than once)
    markers: [], // map of markers
    address: "", // selected address
    storeFilter: 0, // search filters [all, 5 nearest and favorite]
    storeTypes: [0, 1, 2], // store types [store, pick-up point and drive through]
    foundStores: [ // list of queried stores, hardcoded for now
      {
        "city":"'s Gravendeel",
        "postalCode":"3295 BD",
        "street":"Kerkstraat",
        "uuid":"EOgKYx4XFiQAAAFJa_YYZ4At",
        "position": {
          "lat": 51.778461,
          "lng": 4.615551,
        },
      },
      {
        "city":"'s-Heerenberg",
        "postalCode":"7041 JE",
        "street":"Stadsplein",
        "uuid":"7ewKYx4Xqp0AAAFIHigYwKrH",
        "position": {
          "lat": 51.923993,
          "lng": 6.576066,
        },
      },
    ], 
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
    // handle store selection on the search panel
    setSelectedStore(store) {
      if(store != this.selectedStore) {
        this.selectedStore = store;
        //var result = this.foundStores.find(s => s.uuid === this.selectedStore);
        this.moveToPosition(store);
      }
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


      // update the search parameters
      this.searchParameters = newSearchParameters;
    },
    // add basic marker click handling
    markerClickHandler(marker) {
      this.gmap.setZoom(13);
      this.gmap.setCenter(marker.getPosition());
    },
    // navigate to a position
    moveToPosition(uuid) {
      const marker = this.markers[uuid];
      this.gmap.panTo(marker.getPosition());
      this.gmap.setZoom(13);
      this.highlightMarker(marker);
    },
    highlightMarker(marker) {
      this.toggleBounce(marker);
      setTimeout(() => { this.toggleBounce(marker) }, 1500);
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
      google.maps.event.addListenerOnce(map, 'idle', function(){
          document.getElementById('searchContainer').style.visibility = 'show';
      });
      map.initialZoom = true;

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

      var controlDiv = document.getElementById('floating-panel');
      map.controls[google.maps.ControlPosition.TOP_CENTER].push(controlDiv);

      // plot existing markers
      this.foundStores.map((store) => {
          const marker = new google.maps.Marker({ ...store, map });
          this.markers[store.uuid] = marker;
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
