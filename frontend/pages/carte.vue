<template>
  <v-layout wrap>
    <v-flex text-xs-center>
      <v-flex xs12 sm6 d-flex>
        <v-select v-model="selected"
                  :items="items"
                  label="Choisissez une ville"
                  @change="fetchStations(selected.carte.city)"
        />
      </v-flex>
      <v-flex>
        <v-card>
          <carte-component v-model="selected.carte" :stations="getStations"/>
        </v-card>
      </v-flex>
    </v-flex>
  </v-layout>
</template>

<script>
import CarteComponent from "~/components/carte/Carte.vue"
import { createNamespacedHelpers } from "vuex"
import centers from "~/resources/centers.json"

const { mapGetters, mapActions } = createNamespacedHelpers("carte")

export default {
  components: {
    CarteComponent
  },
  data: () => ({
    centers: centers,
    selected: centers.default,
    items: [
      {
        text: "Paris",
        value: centers.paris
      },
      {
        text: "Lyon",
        value: centers.lyon
      },
      {
        text: "Marseille",
        value: centers.marseille
      }
    ]
  }),
  computed: {
    ...mapGetters(["getStationsByContract", "getStations"])
  },
  methods: {
    ...mapActions(["fetchStations"])
  }
}
</script>
