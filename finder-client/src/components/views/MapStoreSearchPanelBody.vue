<template>
    <v-expansion-panels
        class="searchResults"
        :accordion=true
        :multiple=false
        :focusable=false
        :hover=false
        v-model="currentStore">
        <v-expansion-panel v-for="store in foundStores" :key="store.markerId">
          <v-expansion-panel-header>
            <template>
              <div>{{store.city}}</div>
              <div>{{store.street}}</div>
            </template>
          </v-expansion-panel-header>
          <v-expansion-panel-content>
            <template>
              <div>{{store.street}}</div>
            </template>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
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
        }
    },
}

</script>
