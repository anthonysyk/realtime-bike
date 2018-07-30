<template>
  <v-layout>
    <v-flex text-xs-center>
      <v-flex xs12 sm6 d-flex>
        <v-select
          :items="items"
          label="Choisissez une ville"
        ></v-select>
      </v-flex>
      <v-flex>
        <v-card>
          <carte :zoom="zoom" :center="center" :rotation="rotation" :geolocPosition="geolocPosition"></carte>
        </v-card>
      </v-flex>
    </v-flex>
  </v-layout>
</template>

<script>

  import Carte from "~/components/carte/Carte.vue"
  import {createNamespacedHelpers} from "vuex"

  const {mapGetters} = createNamespacedHelpers("carte")

  export default {
    components: {
      Carte
    },
    data: () => ({
      items: ['Lyon', 'Marseille'],
      zoom: 2,
      center: [0, 0],
      rotation: 0,
      geolocPosition: undefined
    }),
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
