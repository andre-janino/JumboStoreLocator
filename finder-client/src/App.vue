<template>
  <v-app>
    <v-app-bar v-if="currentUser" app class="appheader">
      <v-img class="mx-1 appLogo" src="./assets/logo-1.png" max-height="28" max-width="330" contain></v-img>
      <div v-if="currentUser" class="navbar-nav ml-auto"> 
        <p class="jumbo-black">
           Hallo, {{ currentUser.firstName }} !
        </p>
        <v-tooltip left max-width="300px">
          <template v-slot:activator="{ on }">
            <v-btn
              class="logoff"
              dark
              icon
              v-on="on"
              @click.prevent="logOut">
              <v-icon>mdi-logout</v-icon>
            </v-btn>
          </template>
          <span>Log out</span>
        </v-tooltip>
      </div>
    </v-app-bar>
    <v-content>
      <router-view></router-view>
    </v-content>
  </v-app>
</template>

<style>
  @import './assets/styles/app.css';
</style>

<script>
export default {
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
  },
  methods: {
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    }
  }
};

</script>
