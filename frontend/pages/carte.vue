<template>
  <v-layout>
    <v-flex text-xs-center>
      <button @click="clickButton">Click !</button>
    </v-flex>
  </v-layout>
</template>

<script>
  import { createNamespacedHelpers } from "vuex"
  const { mapGetters} = createNamespacedHelpers("carte")


  export default {
    async fetch({app, store}) {
      store.dispatch("carte/fetchStations")
    },
    computed: {
      ...mapGetters(["getStations"])
    },
    methods: {
      clickButton() {
        console.log(this.getStations)
      }
    },
    middleware: 'websocket'
  }
</script>
