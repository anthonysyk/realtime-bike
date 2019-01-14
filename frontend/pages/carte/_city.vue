<template>
  <v-layout row wrap>
    <v-flex sm12>
      <carte-component v-model="selected.carte" :stations="getStations"
                       :responsive="$store.getters['app/getResponsive']"
                       @fetchStationsEmit="fetchStationsWithCoordinatesWrapper"
      />
    </v-flex>
  </v-layout>
</template>

<script>
import CarteComponent from "../../components/carte/Carte.vue"
import Loader from "../../components/Loader.vue"

import { createNamespacedHelpers } from "vuex"
import centers from "../../resources/centers.json"

const { mapGetters, mapActions } = createNamespacedHelpers("carte")

export default {
  components: {
    CarteComponent,
    Loader
  },
  data: () => ({
    centers: centers,
    loader: true,
    items: centers
      .filter(center => center.carte.city !== null)
      .map(center => ({ text: center.carte.city, value: center }))
  }),
  asyncData: ({ params }) => ({
    selected: centers
      .filter(center => center.carte.city !== null)
      .find(center => center.carte.slug === params.city)
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
