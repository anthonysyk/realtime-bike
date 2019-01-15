<template>
  <v-flex>
    <v-card class="chart-container">
      <line-chart :chart-data="data"
                  :options="{responsive: true, maintainAspectRatio: false}"/>
    </v-card>
  </v-flex>
</template>

<script>
import LineChart from "~/components/charts/LineChart"

export default {
  components: { LineChart },
  props: {
    data: {
      type: Object,
      required: true,
      default: () => {}
    }
  },
  data: () => ({}),
  async fetch({ store }) {
    store.dispatch("charts/fetchStats")
  },
  created() {
    this.$store.watch(
      () => {
        return this.$store.getters[`charts/${this.getter}`] // could also put a Getter here
      },
      () => {
        //something changed do something
      },
      //Optional Deep if you need it
      {
        deep: true
      }
    )
  }
}
</script>

<style scoped>
.chart-container {
  margin: 1rem;
}
</style>
