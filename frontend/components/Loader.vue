<template>
  <div
    v-if="loader"
    class="loader"
  >
    <v-progress-circular
      indeterminate
      color="white"
      class="mb-0 progress-circle"
    />
  </div>
</template>

<script>
export default {
  props: {
    loader: {
      type: Boolean,
      default: () => true
    },
    message: {
      type: String,
      default: () => "Chargement ..."
    }
  }
}
</script>

<style scoped>
.loader {
  height: 100%;
  position: absolute;
  z-index: 10;
  width: 100%;
  background-color: #ababab6b !important;
}

.progress-circle {
  top: 50%;
}
</style>
