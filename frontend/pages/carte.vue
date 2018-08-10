<template>
  <v-layout wrap>
    <v-flex text-xs-center>
      <v-flex xs12 sm6 d-flex>
        <v-select v-model="selected"
                  :items="items"
                  label="Choisissez une ville"
        />
      </v-flex>
      <v-flex>
        <v-card>
          <carte-component v-model="selected.carte" :stations="getStationsByContract(selected.carte.city)"/>
        </v-card>
      </v-flex>
    </v-flex>
  </v-layout>
</template>

<script>
import CarteComponent from "~/components/carte/Carte.vue"
import { createNamespacedHelpers } from "vuex"

const { mapGetters } = createNamespacedHelpers("carte")

export default {
  components: {
    CarteComponent
  },
  data: () => ({
    selected: {
      carte: {
        city: null,
        zoom: 5,
        center: [2.552748209185793, 46.90598730737233],
        rotation: 0,
        geolocPosition: undefined,
        stations: []
      }
    },
    items: [
      {
        text: "Lyon",
        value: {
          carte: {
            city: "Lyon",
            zoom: 14,
            center: [4.845058547900202, 45.74479404710428],
            rotation: 0,
            geolocPosition: undefined,
            stations: []
          }
        }
      },
      {
        text: "Marseille",
        value: {
          carte: {
            city: "Marseille",
            zoom: 14,
            center: [5.380769198966677, 43.29004776051272],
            rotation: 0,
            geolocPosition: undefined,
            stations: []
          }
        }
      }
    ]
  }),
  async fetch({ store }) {
    store.dispatch("carte/fetchStations")
  },
  computed: {
    ...mapGetters(["getStationsByContract", "getStations"])
  }
}
</script>
