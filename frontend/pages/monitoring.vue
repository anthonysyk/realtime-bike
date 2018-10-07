<template>
  <v-layout wrap>
    <v-flex text-xs-center>
      <v-flex xs12 sm9 d-flex>
        <v-select v-model="selected"
                  :items="cities"
                  label="Choisissez une ville"
        />
        <v-select v-model="selected"
                  :items="items"
                  label="Choisissez une station"
        />
        <v-select v-model="selected"
                  :items="items"
                  label="Choississez un interval"
        />
      </v-flex>
      <v-flex>
        <v-card>
          <monthly-income :data="chartData"
                          :options="{responsive: true, maintainAspectRatio: true}"
                          :width="1000"
                          :height="500"/>
          <v-card/>
        </v-card>
      </v-flex>
    </v-flex>
  </v-layout>
</template>

<script>
import MonthlyIncome from "~/components/monitoring/MonthlyIncome"

export default {
  components: { MonthlyIncome },
  data: () => ({
    cities: ["Lyon", "Marseille"],
    chartData: {
      labels: ["Mai", "Juin", "Juillet", "Aout", "Septembre"],
      datasets: [
        {
          label: "GitHub Commits",
          backgroundColor: "#f87979",
          data: [12000, 4500, 6200, 6200]
        }
      ],
      fill: false
    }
  }),
  async fetch({ store }) {
    store.dispatch("carte/fetchStations")
  }
}
</script>

<style lang="scss" scoped>
</style>
