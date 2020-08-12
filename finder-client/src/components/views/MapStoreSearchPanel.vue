<template>
  <section id="searchContainer" class="searchContainer" style="visibility: hidden">
    <v-card class="mx-auto searchCard" elevation="5" max-width="470" width="470">
      <!-- Search area -->
      <MapStoreSearchPanelHeader
        :address="address"
        :storeTypes="storeTypes"
        @setStoreTypes="setStoreTypes"
        @setAddress="setAddress"
        @searchAddress="searchAddress"
        @initAutoComplete="initAutoComplete"
        />
        
      <!-- Expandable panels with the found stores -->
       <MapStoreSearchPanelBody
        id="searchBodyWrapper"
        style="display: none"
        :foundStores="foundStores"
        :selectedStore="selectedStore"
        :loading="loading"
        :nearbyStoresLimit="nearbyStoresLimit"
        :storeFilter="storeFilter"
        @toggleFavorite="toggleFavorite"
        @setSelectedStore="setSelectedStore"
        @setStoresLimit="setStoresLimit"
        />
    </v-card>
  </section>
</template>

<script>
import MapStoreSearchPanelHeader from "./MapStoreSearchPanelHeader";
import MapStoreSearchPanelBody from "./MapStoreSearchPanelBody";

export default {
    props: ["address","foundStores","selectedStore","storeTypes","loading","storeFilter","nearbyStoresLimit"],
    name: "MapStoreSearchPanel",
    components: {
      MapStoreSearchPanelHeader,
      MapStoreSearchPanelBody
    },
    methods: {
        setAddress(address) {
            this.$emit("setAddress", address);
        },
        searchAddress() {
            this.$emit("searchAddress");
        },
        setStoreTypes(item) {
          this.$emit("setStoreTypes", item);
        },
        setSelectedStore(item) {
          this.$emit("setSelectedStore", item);
        },
        setStoresLimit(item) {
          this.$emit("setStoresLimit", item);
        },
        toggleFavorite(storeId) {
          this.$emit("toggleFavorite", storeId);
        },
        initAutoComplete() {
          this.$emit("initAutoComplete");
        }
    }
}

</script>
