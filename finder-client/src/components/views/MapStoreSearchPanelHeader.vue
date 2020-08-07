<template>
    <div class="searchBox">
        <div class="searchWrapper">
            <div class="arrange-fit">
                <v-icon class="mr-2">mdi-map-marker-circle</v-icon>
            </div>
            <div class="arrange-fill">
                <p class="searchLabelText" width="300px">Find a nearby Jumbo store.</p>
                <div class="searchWrapper">
                    <div class="arrange-fill">
                    <v-text-field 
                        autocomplete="off"
                        v-model="searchedAddress"
                        id="searchInput" 
                        class="searchInput"
                        hint="Enter an address or location"
                        persistent-hint 
                        clearable 
                        type="text"
                        @keyup.enter="queryStores"
                        />
                    </div>
                    <div class="arrange-fit">
                    <v-btn 
                        @click="queryStores"
                        small 
                        class="searchStoresButton" 
                        fab 
                        color="#fdc513">
                        <v-icon>mdi-near-me</v-icon>
                    </v-btn>
                    </div>
                </div>
                <MapStoreTypes 
                    :storeTypes="storeTypes"
                    @setStoreTypes="setStoreTypes"
                    />
            </div>
        </div>
    </div>
</template>

<script>
import MapStoreTypes from "./MapStoreTypes";

export default {
    props: ["address","storeTypes"],
    name: "MapStoreSearchPanelHeader",
    components: {
      MapStoreTypes
    },
    computed: {
        searchedAddress: {
            get() {
                return this.address;
            },
            set(newVal) {
                this.$emit("setAddress", newVal);
            } 
        }
    },
    methods: {
        setStoreTypes(item) {
          this.$emit("setStoreTypes", item);
        },
        queryStores() {
            this.$emit("queryStores");
        },
    }
}

</script>
