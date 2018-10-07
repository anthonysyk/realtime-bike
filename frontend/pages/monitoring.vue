<template>
  <v-layout wrap>
    <v-flex text-xs-center>
      <v-flex xs12 sm12 d-flex>
        <v-flex xs12 sm6>
          <v-select v-model="selectedCity" :items="cities"
                    label="Choisissez une ville"
          />
        </v-flex>
        <v-flex xs12 sm6>
          <v-select
            v-if="selectedCity.length > 0"
            v-model="selectedStation"
            :items="getStationsByCity(selectedCity)"
            label="Choisissez une station"
          />
        </v-flex>
      </v-flex>
      <v-flex xs12 sm6>
        <v-select
          v-if="selectedStation.length > 0"
          v-model="selectedInterval"
          :items="intervals"
          label="Search for an option"
          hide-selected
          solo
          chips
          @change="fetchStats({selectedInterval, selectedStation})"
        />
      </v-flex>
      <monitoring-chart :city="selectedCity" :station="selectedStation" :interval="selectedInterval"/>
    </v-flex>
  </v-layout>
</template>

<script>
import MonitoringChart from "~/components/monitoring/MonitoringChart"

import { createNamespacedHelpers } from "vuex"

const { mapGetters, mapActions } = createNamespacedHelpers("monitoring")

export default {
  components: { MonitoringChart },
  data: () => ({
    selectedCity: {},
    selectedStation: {},
    selectedInterval: {},
    cities: ["Lyon", "Marseille"],
    intervals: ["5min", "15min", "30min", "1h", "3h"]
  }),
  async fetch({ store }) {
    store.dispatch("monitoring/fetchStations")
  },
  computed: {
    ...mapGetters(["getStationsByCity"])
  },
  methods: {
    ...mapActions({
      fetchStats: "fetchStats"
    })
  }
}
</script>

<style lang="scss" scoped>
</style>
