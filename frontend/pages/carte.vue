<template>
  <v-layout wrap>
    <v-flex text-xs-center >
      <v-flex xs12 sm6 d-flex>
        <!--<button @click="clickButton">Click !</button>-->
        <v-select v-model="selected"
                  :items="items"
                  label="Choisissez une ville"
        />
      </v-flex>
      <v-flex>
        <v-card>
          <carte v-model="selected.carte"/>
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
      selected: {
        carte: {
          zoom: 5,
          center: [2.552748209185793, 46.90598730737233],
          rotation: 0,
          geolocPosition: undefined
        }
      },
      items: [
        {
          text: "Lyon",
          value: {
            carte: {
              zoom: 12,
              center: [4.839146847753511, 45.75762703924812],
              rotation: 0,
              geolocPosition: undefined
            }
          }
        },
        {
          text: "Marseille",
          value: {
            carte: {
              zoom: 13,
              center: [5.377084514569716, 43.28811010524572],
              rotation: 0,
              geolocPosition: undefined
            }
          }
        }
      ]
    }),
    async fetch({store}) {
      store.dispatch("carte/fetchStations")
    },
    computed: {
      ...mapGetters(["getStations"])
    },
    methods: {
      clickButton() {
        console.log(this.selected.value)
      }
    },
    middleware: "websocket"
  }
</script>
