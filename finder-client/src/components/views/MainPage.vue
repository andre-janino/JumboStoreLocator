<template>
  <v-layout>
    <div class="mainContainer" fluid>
      I'm in.
    </div>
  </v-layout>
</template>

<script>
import * as axios from "axios";
import config from "../../config";

// default properties for requests
axios.defaults.headers.common = { "X-Requested-With": "XMLHttpRequest" };
axios.defaults.baseURL = `${config.baseUrl}/`;

export default {
  name: "MainPage",
  data: () => ({
    stores: [], // list of queried stores
    address: "", // queried address
  }),
  methods: {
    
  }
};
</script>
