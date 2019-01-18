<template>
  <v-layout row wrap>
    <v-flex xs12>
      <v-select v-model="selected"
                :items="items"
                label="Choisissez une ville"
      />
    </v-flex>
    <v-flex sm12>
      <carte-component v-model="selected.carte" :stations="getStations"
                       @fetchStationsEmit="fetchStationsWithCoordinatesWrapper"/>
    </v-flex>
  </v-layout>
</template>

<script>
import CarteComponent from "../../components/carte/Carte.vue"
import Loader from "../../components/Loader.vue"

import { createNamespacedHelpers } from "vuex"
import centers from "../../static/centers.json"

const { mapGetters, mapActions } = createNamespacedHelpers("carte")

export default {
  components: {
    CarteComponent,
    Loader
  },
  data: () => ({
    centers: centers,
    selected: centers.find(center => center.carte.city === "Paris"),
    loader: true,
    items: centers
      .filter(center => center.carte.city !== null)
      .map(center => ({ text: center.carte.city, value: center }))
  }),
  computed: {
    ...mapGetters(["getStationsByContract", "getStations"])
  },
  methods: {
    ...mapActions(["fetchStations"]),
    fetchStationsWithCoordinatesWrapper(coordinates) {
      if (this.selected.carte !== undefined) {
        this.$store.dispatch("carte/fetchStationsWithCoordinates", {
          city: this.selected.carte.city,
          coordinates
        })
      }
    }
  }
}
</script>
