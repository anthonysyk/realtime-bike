<template>
  <v-layout wrap>
    <v-flex xs12 sm6 d-flex>
      <v-select v-model="selectedCity" :items="cities"
                label="Choisissez une ville"
                @change="fetchFullStations(selectedCity.carte.city)"
      />
    </v-flex>
    <v-flex xs12 sm6 d-flex>
      <v-dialog v-if="selectedCity.carte.city !== null" v-model="dialog" full-width lazy>
        <v-select
          slot="activator"
          v-model="selectedStation"
          :items="getStationsByCity(selectedCity.carte.city)"
          label="Choisissez une station"
          return-object
          @change="Object.keys(selectedInterval).length > 0 && fetchStats({selectedInterval, selectedStation})"
        />
        <v-card>
          <v-card-title class="headline secondary" primary-title full-width>
            <span class="white-label">{{ selectedCity.carte.city }}</span>
            <v-spacer/>
            <v-btn left icon dark @click="dialog = false">
              <v-icon>close</v-icon>
            </v-btn>
          </v-card-title>
          <v-card-text>
            <carte v-if="dialog === true" :stations="getFullStations" :center-map="selectedCity.carte" @updateStation="updateStation"/>
          </v-card-text>
        </v-card>
      </v-dialog>
    </v-flex>
    <v-flex xs12 sm6 d-flex>
      <v-select
        v-if="selectedStation.value.length > 0"
        v-model="selectedInterval"
        :items="intervals"
        label="Sélectionnez un interval"
        hide-selected
        chips
        @change="fetchStats({selectedInterval, selectedStation})"
      />
    </v-flex>
    <monitoring-chart :city="selectedCity" :station="selectedStation" :interval="selectedInterval"/>
  </v-layout>
</template>

<script>
import MonitoringChart from "~/components/monitoring/MonitoringChart"
import centers from "~/resources/centers.json"
import Carte from "@/components/monitoring/Carte"

import { createNamespacedHelpers } from "vuex"

const { mapGetters, mapActions } = createNamespacedHelpers("monitoring")

export default {
  components: { MonitoringChart, Carte },
  data: () => ({
    dialog: false,
    selectedCity: centers.default,
    selectedStation: { text: "", value: "" },
    defaultStation: { text: "toto", value: "toto" },
    selectedInterval: "5min",
    intervals: ["5min", "15min", "30min", "1h", "3h", "12h", "1j"],
    cities: [
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
  async fetch({ store }) {
    store.dispatch("monitoring/fetchStations")
  },
  computed: {
    ...mapGetters(["getStationsByCity", "getFullStations"])
  },
  destroyed: function() {
    this !== undefined ? this.$store.commit("monitoring/resetState") : undefined
  },
  methods: {
    ...mapActions({
      fetchStats: "fetchStats",
      fetchFullStations: "fetchFullStations"
    }),
    updateStation(station) {
      this.selectedStation = {
        text: station.address,
        value: [station.number, station.contract_name].join("_")
      }
      this.fetchStats({
        selectedInterval: this.selectedInterval,
        selectedStation: this.selectedStation
      })
      this.dialog = false
    }
  }
}
</script>

<style lang="scss" scoped>
.white-label {
  color: white;
}
</style>
