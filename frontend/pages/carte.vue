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
        center: [2.3380340037250313, 48.85926008555791],
        rotation: 0,
        geolocPosition: undefined,
        stations: []
      }
    },
    items: [
      {
        text: "Paris",
        value: {
          carte: {
            city: "Paris",
            zoom: 15,
            center: [2.3256653515384667, 48.86351312359906],
            rotation: 0,
            geolocPosition: undefined,
            stations: []
          }
        }
      },
      {
        text: "Lyon",
        value: {
          carte: {
            city: "Lyon",
            zoom: 15,
            center: [4.842469526028807, 45.755144634660354],
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
            zoom: 15,
            center: [5.3809710350488436, 43.29184455955078],
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
