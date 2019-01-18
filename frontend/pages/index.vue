<template>
  <div>
    <v-subheader class="pl-0 pr-0 mb-2 ml-3">Consultez le nombre de vélos par station sur les dernières 24h</v-subheader>
    <div :class="getResponsive() ? 'pa-2' : 'pa-5'">
      <v-layout wrap>
        <v-flex xs12 sm6 d-flex class="pr-2">
          <v-select v-model="selectedCity" :items="cities"
                    label="Choisissez une ville"
                    @change="updateCity(selectedCity.carte.city)"
          />
        </v-flex>
        <v-flex xs12 sm6 d-flex class="pr-2">
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
          <v-layout row wrap justify-space-around align-center>
            <v-flex xs12 sm6 md6>
              <monitoring-chart :city="selectedCity" :station="selectedStation" :interval="selectedInterval" :data="getStats" getter="getStats"/>
            </v-flex>
            <v-flex xs12 sm6 md6>
              <monitoring-chart :city="selectedCity" :station="selectedStation" :interval="selectedInterval" :data="getStatsDelta" getter="getStatsDelta"/>
            </v-flex>
            <!--<v-flex xs12 sm6 md6>-->
            <!--<monitoring-chart :city="selectedCity" :station="selectedStation" :interval="selectedInterval" :data="getStatsAvailability" getter="getStatsAvailability"/>-->
            <!--</v-flex>-->
          </v-layout>
        </div>
      </v-layout>
    </div>
    <v-snackbar
      v-if="!selectedStation.value.length > 0 && dialog === false"
      v-model="snackbar"
      :timeout="0"
      :vertical="getResponsive()"
      top="true"
      right="true"
      color="info"
    >
      Veuillez choisir une station
      <v-btn
        flat
        @click="snackbar = false"
      >
        Close
      </v-btn>
    </v-snackbar>
  </div>
</template>

<script>
import MonitoringChart from "../components/charts/MonitoringChart"
import centers from "../static/centers.json"
import Carte from "../components/charts/Carte"

import { createNamespacedHelpers } from "vuex"

const { mapGetters, mapActions } = createNamespacedHelpers("charts")

export default {
  components: { MonitoringChart, Carte },
  data: () => ({
    dialog: false,
    snackbar: true,
    selectedCity: centers.find(center => center.carte.city === "Paris"),
    selectedStation: { text: "Cambon - Rivoli", value: "1020_Paris" },
    defaultStation: { text: "", value: "" },
    selectedInterval: "1h",
    intervals: ["15min", "30min", "1h", "3h", "6h"],
    cities: centers
      .filter(center => center.carte.city !== null)
      .map(center => ({ text: center.carte.city, value: center }))
  }),
  async fetch({ store }) {
    store.dispatch("charts/fetchStations")
  },
  computed: {
    ...mapGetters([
      "getStationsByCity",
      "getFullStations",
      "getStats",
      "getStatsDelta",
      "getStatsAvailability"
    ])
  },
  destroyed: function() {
    this !== undefined ? this.$store.commit("charts/resetState") : undefined
  },
  beforeMount() {
    this.updateCity(this.selectedCity.carte.city)
    this.selectedStation = { text: "Cambon - Rivoli", value: "1020_Paris" }
    const that = this
    this.fetchStats({
      selectedInterval: that.selectedInterval,
      selectedStation: that.selectedStation,
      selectedCity: that.selectedCity
    })
  },
  methods: {
    ...mapActions({
      fetchStats: "fetchStats",
      fetchFullStations: "fetchFullStations"
    }),
    getResponsive() {
      return this.$store.getters["app/getResponsive"]
    },
    updateInterval(interval) {
      this.selectedInterval = interval
      this.fetchStats({
        selectedInterval: interval,
        selectedStation: this.selectedStation,
        city: this.selectedCity
      })
    },
    resetInputs() {
      this.$store.commit("charts/resetState")
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
