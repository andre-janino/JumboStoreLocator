<template>
  <v-progress-linear
    :active="loading"
    :color="color"
    :indeterminate="true"
    class="ma-0"
    height="4"
    style="top: -2px;"
  />
</template>

<script>
export default {
  props: {
    loading: Boolean,
    color: String,
  },
};
</script>