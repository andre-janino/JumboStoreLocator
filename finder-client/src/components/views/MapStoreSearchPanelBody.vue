<template>
  <div>
    <!-- Title of the store list area -->
    <v-card-text id="searchBoxTitle" class="text--primary">
        <p class="searchLabelTextBold">Nearby stores</p>
    </v-card-text>
    <v-divider></v-divider>
    <v-expansion-panels
        ref="panels"
        class="searchResults"
        id="searchResults"
        :accordion=true
        :multiple=false
        :focusable=false
        :hover=false
        v-model="currentStore">
      <v-expansion-panel v-for="store in foundStores" :key="store.markerId">
        <v-expansion-panel-header :id="store.sapStoreID">
          <template>
            <div class="searchWrapper">
                <div class="arrange-fill">
                  <span class="jumbo-black-city">{{store.addressName}}</span>
                  <p class="jumbo-details">{{store.street}} {{store.street2}}</p>
                  <p class="jumbo-details">{{store.city}}</p>
                  <span class="jumbo-open">Today open</span>
                  <span class="jumbo-details"> {{store.todayOpen}} - {{store.todayClose}}</span>
                </div>
                <div class="arrange-fit">
                  <div class="jumbo-details-links">
                    <span v-if="isCollectionPoint(store)" class="jumbo-details-pup">
                      <a class="jumbo-details-link" :href="'https://www.jumbo.com/INTERSHOP/web/WFS/Jumbo-Grocery-Site/nl_NL/-/EUR/ViewDeliveryOptions-Start?storeUUID='+ store.uuid" target="_blank" onclick="event.stopPropagation();">
                        <img v-if="isSupermarktPuP" class="jumbo-details-img-store" src="../../assets/img/pup-store.webp">
                        <img v-else class="jumbo-details-img-pup" src="../../assets/img/pup.webp">
                        <span>Pick-up</span>
                      </a>
                    </span>
                    <span v-else class="jumbo-details-store">
                      <img src="../../assets/img/store.webp">
                    </span>
                    <span>
                      <a class="jumbo-details-link" :href="store.directions" target="_blank" onclick="event.stopPropagation();">
                        <v-icon large color="black" class="jumbo-details-img-dist">mdi-call-split</v-icon>
                        <span>{{store.distance}}</span>
                      </a>
                    </span>
                  </div>
                </div>
            </div>
          </template>
        </v-expansion-panel-header>
        <v-expansion-panel-content>
          <template>
            <hr class="mt-3 mb-3"/>
            <div class="searchWrapper">
              <div class="arrange-fit left-panel">
                <v-icon class="jumbo-store-time" large>mdi-clock-outline</v-icon>
                <v-icon v-if="showWarningSign(store)" class="jumbo-store-warning" large color="rgb(38, 38, 38)">mdi-information-outline</v-icon>
              </div>
              <div class="arrange-fill">
                <div class="jumbo-details">
                  <span class="jumbo-details-time">Mon - Sat</span>
                  <span class="ml-5">08:00 - 22:00</span>
                </div>
                <div class="jumbo-details">
                  <div class="searchWrapper">
                    <div class="arrange-fill left-panel">
                      <span class="jumbo-details-time">Suday</span>
                      <span class="ml-10">10:00 - 20:00</span>
                    </div>
                    <div class="arrange-fit favorite-button">
                      <v-chip-group active-class="primary--text" v-model="store.favorite">
                        <v-chip outlined :value="true">
                          <v-icon medium class="chipFavIcon">mdi-star-circle</v-icon>
                          <strong>Favorite</strong>
                        </v-chip>
                      </v-chip-group>
                    </div>
                  </div>
                  <div v-if="showWarningSign(store)">
                    <p class="jumbo-details-attention">Pay attention!</p>
                    If you select a different Pick Up Point, prices may change. 
                  </div>
                </div>
              </div>
            </div>
          </template>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>
</template>

<script>
export default {
    props: ["foundStores", "selectedStore"],
    name: "MapStoreSearchPanelBody",
    computed: {
        currentStore: {
            get() {
                return this.selectedStore;
            },
            set(newVal) {
                this.$emit("setSelectedStore", newVal);
            } 
        },
    },
    methods: {
      isCollectionPoint(item) {
        return item.collectionPoint;
      },
      isSupermarktPuP(item) {
        return item.locationType == "SupermarktPuP";
      },
      showWarningSign(store) {
        return store.showWarningMessage;
      },
      getStoreIcon(store) {
        return this.storeIcon[store.locationType];
      },
    }
}

</script>
