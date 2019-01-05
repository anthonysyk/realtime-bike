<template>
  <v-layout wrap>
    <v-flex xs12 sm6 d-flex>
      <v-select v-model="selectedCity" :items="cities"
                label="Choisissez une ville"
                @change="updateCity(selectedCity.carte.city)"
      />
    </v-flex>
    <v-flex xs12 sm6 d-flex>
      <v-dialog v-if="selectedCity.carte.city !== null" v-model="dialog" lazy fullscreen
                hide-overlay
                transition="dialog-bottom-transition">
        <v-select
          slot="activator"
          v-model="selectedStation"
          :items="[selectedStation]"
          label="Choisissez une station"
          return-object
          @change="Object.keys(selectedInterval).length > 0 && fetchStats({selectedInterval, selectedStation, selectedCity})"
        />
        <v-card>
          <v-card-title class="headline accent" primary-title full-width>
            <span class="white-label">{{ selectedCity.carte.city }}</span>
            <v-spacer/>
            <v-btn left icon dark @click="dialog = false">
              <v-icon>close</v-icon>
            </v-btn>
          </v-card-title>
          <v-card-text>
            <carte v-if="dialog === true" :stations="getFullStations" :center-map="selectedCity.carte"
                   @updateStationEvent="updateStationEvent"/>
          </v-card-text>
        </v-card>
      </v-dialog>
    </v-flex>
    <div class="text-xs-center chart-container">
      <div class="interval-container">
        <v-chip v-for="interval in intervals" :key="interval"
                :color="selectedInterval === interval ? 'primary' : 'secondary'"
                dark class="interval-chip" text-color="white"
                @click="updateInterval(interval)"
        >{{ interval }}</v-chip>
      </div>
      <monitoring-chart :city="selectedCity" :station="selectedStation" :interval="selectedInterval"/>
    </div>
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
    intervals: ["5min", "15min", "30min", "1h", "3h", "12h"],
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
    updateInterval(interval) {
      this.selectedInterval = interval
      this.fetchStats({
        selectedInterval: interval,
        selectedStation: this.selectedStation,
        city: this.selectedCity
      })
    },
    resetInputs() {
      this.$store.commit("monitoring/resetState")
      this.selectedStation = { text: "", value: "" }
    },
    updateCity(city) {
      this.resetInputs()
      this.fetchFullStations(city)
    },
    updateStationEvent(station) {
      this.selectedStation = {
        text: station.address,
        value: [station.number, station.contract_name].join("_")
      }
      this.fetchStats({
        selectedInterval: this.selectedInterval,
        selectedStation: this.selectedStation,
        selectedCity: this.selectedCity
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
.interval-container {
  margin: auto;
}

.interval-chip {
  position: initial;
  margin-right: 0.5rem;
  &:hover {
    transform: scale(1.1) !important;
  }
  margin-bottom: 1rem;
}

.chart-container {
  margin-top: 1rem;
  display: block;
  width: 100%;
}
.v-card__text {
  padding: 0;
}
</style>

<style>
.v-dialog {
  margin: 0px !important;
}
</style>
