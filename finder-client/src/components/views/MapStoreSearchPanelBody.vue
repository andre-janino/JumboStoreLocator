<template>
    <v-expansion-panels
        class="searchResults"
        :accordion=true
        :multiple=false
        :focusable=false
        :hover=false>
        <v-expansion-panel v-for="store in foundStores" :key="store.uuid">
          <v-expansion-panel-header @click="setSelectedStore(store.uuid)">
            <template>
              <div>{{store.city}}</div>
              <div>{{store.street}}</div>
            </template>
          </v-expansion-panel-header>
          <v-expansion-panel-content onshowhide="log()">
            <template>
              <div>{{store.street}}</div>
            </template>
          </v-expansion-panel-content>
        </v-expansion-panel>
      </v-expansion-panels>
</template>

<script>
export default {
    props: ["foundStores"],
    name: "MapStoreSearchPanelBody",
    methods: {
        log (aaaa) {
            console.log("Section click:", aaaa);
        },
        setSelectedStore(item) {
            this.$emit("setSelectedStore", item);
        },
    },
}

</script>
