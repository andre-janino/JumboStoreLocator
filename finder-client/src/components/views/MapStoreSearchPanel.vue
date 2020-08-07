<template>
  <section id="searchContainer" class="searchContainer" style="visibility: hidden">
    <v-card class="mx-auto searchCard" max-width="470" width="470">
      <!-- Search area -->
      <MapStoreSearchPanelHeader
        :address="address"
        :storeTypes="storeTypes"
        @setStoreTypes="setStoreTypes"
        @setAddress="setAddress"
        @queryStores="queryStores"
        />
      
      <!-- Title of the store list area -->
      <v-card-text class="text--primary">
        <p class="searchLabelTextBold">Nearby stores</p>
      </v-card-text>
      <v-divider></v-divider>

      <!-- Expandable panels with the found stores -->
       <MapStoreSearchPanelBody
        :foundStores="foundStores"
        :selectedStore="selectedStore"
        @setSelectedStore="setSelectedStore"
        />
    </v-card>
  </section>
</template>

<script>
import MapStoreSearchPanelHeader from "./MapStoreSearchPanelHeader";
import MapStoreSearchPanelBody from "./MapStoreSearchPanelBody";

export default {
    props: ["address","foundStores","selectedStore","storeTypes"],
    name: "MapStoreSearchPanel",
    components: {
      MapStoreSearchPanelHeader,
      MapStoreSearchPanelBody
    },
    methods: {
        setAddress(address) {
            this.$emit("setAddress", address);
        },
        queryStores() {
            this.$emit("queryStores");
        },
        setStoreTypes(item) {
          this.$emit("setStoreTypes", item);
        },
        setSelectedStore(item) {
          this.$emit("setSelectedStore", item);
        },
    }
}

</script>
