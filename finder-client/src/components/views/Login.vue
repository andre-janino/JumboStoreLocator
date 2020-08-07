<template>
    <v-container fill-height fluid>
      <v-layout row wrap>
        <v-flex class="loginbackground-static" xs12 sm12 md5>
          <v-img alt="Jumbo" class="ml-3 loginLogo" contain position="center right" src="../.././assets/img/Hallo.png" ></v-img>
        </v-flex>
        <v-flex xs12 sm12 md7>
            <LoginForm/>
            <LoginCreateAcc/>
            <LoginCreateBussAcc/>
            <LoginAdditionalInfo/>
        </v-flex>
      </v-layout>
    </v-container>
</template>

<script>
import LoginForm from "./LoginForm";
import LoginCreateAcc from "./LoginCreateAcc";
import LoginCreateBussAcc from "./LoginCreateBussAcc";
import LoginAdditionalInfo from "./LoginAdditionalInfo";

export default {
  name: 'Login',
  components: {
    LoginForm,
    LoginCreateAcc,
    LoginCreateBussAcc,
    LoginAdditionalInfo
  }
};
</script>