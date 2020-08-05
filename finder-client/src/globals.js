import Vue from "vue";
import VueGlobalVar from "vue-global-var";

Vue.use(VueGlobalVar, {
    globals: {
        colors: {
            valid: "success",
            idle: "warning",
            running: "primary",
            done: "success",
            error: "error"
        },
        icons: {
            valid: "mdi-check",
            idle: "mdi-clock-outline",
            running: "mdi-loading",
            done: "mdi-marker-check",
            error: "mdi-alert-circle-outline"
        },
    },
});