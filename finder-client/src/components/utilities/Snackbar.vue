<template>
    <div class="text-center">
      <v-snackbar :color="snackbarColor" v-model="value" :timeout="snackbarTimeout">
        {{ contentText }}
        <v-btn color="white" text @click="value = false">
          Fechar
        </v-btn>
      </v-snackbar>
    </div>
</template>

<script>
export default {
  props: ["value", "text", "timeout", "color"],
  computed: {
      // computed property so that vue does not complain about mutating a default property D:
      contentText() {
          if(this.text) {
              return this.text;
          }
          return "Request was processed successfully.";
      },
      snackbarTimeout() {
          if(this.timeout) {
              return this.timeout;
          }
          return 4000;
      },
      snackbarColor() {
          if(this.color) {
              return this.color;
          }
          return "blue-grey";
      }
  },
};
</script>