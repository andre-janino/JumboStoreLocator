<template>
    <!-- Differently from MapStoreTypes, I've set-up a hard-coded list of filtering options -->
    <v-toolbar flat class="mapToolbar">
        <template v-slot:extension>
        <v-tabs v-model="stores" color="black" fixed-tabs>
            <v-tabs-slider></v-tabs-slider>
            <v-tab>
                <v-icon class="mr-2">mdi-map-marker-multiple</v-icon>
                <div class="tabText">All stores</div>
            </v-tab>
            <v-tab>
                <v-icon class="mr-2">mdi-walk</v-icon>
                <div class="tabText">Closest stores</div>
            </v-tab>
            <v-tab>
                <v-icon class="mr-2">mdi-star</v-icon>
                <div class="tabText">Favorite stores</div>
            </v-tab>
        </v-tabs>
        </template>
    </v-toolbar>  
</template>

<script>

export default {
    props: ["storeFilter"],
    name: "MapStoreFilters",
    computed: {
        stores: {
            get() {
                return this.storeFilter;
            },
            set(newVal) {
                this.$emit("setFilters", newVal);
            } 
        }
    }
}

</script>