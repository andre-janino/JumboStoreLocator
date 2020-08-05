<template>
    <v-container fill-height fluid>
      <v-layout row wrap>
        <v-flex class="loginbackground-static" xs12 sm12 md5>
          <v-img alt="Jumbo" class="ml-3 loginLogo" contain position="center right" src="../.././assets/img/Hallo.png" ></v-img>
        </v-flex>
        <v-flex xs12 sm12 md7>
            <v-card elevation="2" style="margin: 15px;" light tag="section" >
              <v-card-title>
                <v-layout align-center justify-space-between>
                    <h3 class="jumbo-special-black">
                    Login
                    </h3>
                </v-layout>
              </v-card-title>
              <v-divider></v-divider>
              <v-card-text>
                <p>Log in with your email and password:</p>
                <v-form>
                    <v-text-field
                        @keyup.enter="handleLogin()"
                        outline
                        label="E-mail"
                        type="text"
                        v-model="user.email"></v-text-field>
                    <v-text-field
                        @keyup.enter="handleLogin()"
                        outline
                        hide-details
                        label="Password"
                        type="password"
                        v-model="user.password"></v-text-field>
                </v-form>
              </v-card-text>
              <v-divider></v-divider>
              <v-card-actions :class="{ 'pa-3': $vuetify.breakpoint.smAndUp }">
                <v-spacer></v-spacer>
                <v-btn class="jum-button" :disabled="isLoading" :large="$vuetify.breakpoint.smAndUp" @click="handleGuestLogin">
                    <v-progress-circular
                        style="margin-left: 0px; margin-right:15px" 
                        indeterminate
                        size="16"
                        width="2"
                        v-if="guestLoading"/>
                    <v-icon style="margin-left: 0px" left v-else>mdi-account-check</v-icon>
                    <div style="margin-top: 2px">Login as a guest</div>
                </v-btn>
                <v-btn class="jum-button" :disabled="isLoading" :large="$vuetify.breakpoint.smAndUp" @click="handleLogin">
                    <v-progress-circular
                        style="margin-left: 0px; margin-right:15px" 
                        indeterminate
                        size="16"
                        width="2"
                        v-if="loginLoading"/>
                    <v-icon style="margin-left: 0px" left v-else>mdi-lock-open</v-icon>
                    <div style="margin-top: 2px">Login</div>
                </v-btn>
              </v-card-actions>
            </v-card>

            <v-card elevation="2" style="margin: 15px;" light tag="section" >
              <v-card-title>
                <v-layout align-center justify-space-between>
                    <h3 class="jumbo-special-black">
                    Create an account
                    </h3>
                </v-layout>
              </v-card-title>
              <v-divider></v-divider>
              <v-card-text>
                <p>With your account you can order groceries online and save your favorite recipes and products.</p>
              </v-card-text>
            </v-card>

            <v-card elevation="2" style="margin: 15px;" light tag="section" >
              <v-card-title>
                <v-layout align-center justify-space-between>
                    <h3 class="jumbo-special-black">
                    Create a business account
                    </h3>
                </v-layout>
              </v-card-title>
              <v-divider></v-divider>
              <v-card-text>
                <p>If you order for business, it is possible to pay on account and have your groceries delivered to the office.</p>
              </v-card-text>
            </v-card>

            <v-card elevation="2" style="margin: 15px;" light tag="section" >
              <v-card-title>
                <v-layout align-center justify-space-between>
                    <h3 class="jumbo-special-black">
                    Why a Jumbo.com account?
                    </h3>
                </v-layout>
              </v-card-title>
              <v-divider></v-divider>
              <v-card-text>
                <p><v-icon color="success">mdi-check</v-icon> Order your groceries easily online</p>
                <p><v-icon color="success">mdi-check</v-icon> Save your favorite recipes and products</p>
                <p><v-icon color="success">mdi-check</v-icon> Ordered on work-days, delivered tomorrow</p>
                <p><v-icon color="success">mdi-check</v-icon> Search and save your favorite stores</p>
              </v-card-text>
            </v-card>
        </v-flex>
      </v-layout>
      <Snackbar v-model="loginRequested" :text="loginResultText" :color="submitFailedColor"/>
    </v-container>
</template>

<script>
import User from '../../models/user';
import Snackbar from ".././utilities/Snackbar";

export default {
  name: 'Login',
  components: {
    Snackbar,
  },
  data() {
    return {
      user: new User('', '', '', '', ''),
      loginLoading: false,
      guestLoading: false,
      message: '',
      loginRequested: false,
      loginResultText: "",
      submitFailedColor: "error"
    };
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    },
    isLoading() {
      return this.guestLoading || this.loginLoading;
    }
  },
  created() {
    if (this.loggedIn) {
      this.$router.push('/');
    }
  },
  methods: {
    handleLogin() {
      if (this.user.email && this.user.password) {
        this.loginLoading = true;
        this.$store.dispatch('auth/login', this.user).then(
        () => {
            this.$router.push('/');
        },
        error => {
            this.loginFeedback('Invalid e-mail or password.');
            console.log('There was an error loging the user in.');
            console.log(error);
            this.loginLoading = false;
            this.message =
            (error.response && error.response.data) ||
            error.message ||
            error.toString();
        });
      } else {
          this.loginFeedback('Please fill the e-mail and password to login.');
      }
    },
    handleGuestLogin() {
      this.guestLoading = true;
      this.$store.dispatch('auth/loginGuest').then(
      () => {
          this.$router.push('/');
      },
      error => {
          this.loginFeedback('The store locator is unavailable.');
          console.log('There was an error loging the user in.');
          console.log(error);
          this.guestLoading = false;
          this.message =
          (error.response && error.response.data) ||
          error.message ||
          error.toString();
      });
    },
    loginFeedback(text) {
      this.loginResultText = text;
      this.loginRequested = true;  
      setTimeout(() => {
        this.loginRequested = false;
      }, 2000);
    },
  }
};
</script>