<template>

    <v-chip-group 
        class="mapStoreTypes" 
        v-model="stores"
        :mandatory=true
        multiple
        active-class="primary--text"
    >
        <v-chip outlined>
            <img class="chipStoreIcon" src="../../assets/img/store.webp">
            <strong>Store</strong>
        </v-chip>
        <v-chip outlined>
            <img class="chipStoreIcon" src="../../assets/img/pup.webp">
            <strong>Pick-up</strong>
        </v-chip>
        <v-chip outlined>
            <img class="chipStoreIcon" src="../../assets/img/pup-store.webp">
            <strong>Drive-through</strong>
        </v-chip>
        <!--
        <v-chip v-for="type in storeTypes" :key="type" v-model="typeSelection" @click="changeStoreTypeSelection">
            <img class="chipStoreIcon" :src="storeTypesImgs[type]" alt="Store">
            <strong>{{ type }}</strong>
        </v-chip>
        -->
    </v-chip-group>
</template>

<script>

export default {
    props: ["storeTypes"],
    name: "MapStoreTypes",
    computed: {
        stores: {
            get() {
                return this.storeTypes;
            },
            set(newVal) {
                this.$emit("setStoreTypes", newVal);
            } 
        }
    }
}

</script>