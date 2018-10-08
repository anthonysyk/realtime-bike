<template>
  <v-flex>
    <v-card>
      <line-chart :chart-data="getStats"
                  :options="{responsive: true, maintainAspectRatio: false}"
                  :width="1000"
                  :height="460"/>
    </v-card>
  </v-flex>
</template>

<script>
import LineChart from "~/components/charts/LineChart"

import { createNamespacedHelpers } from "vuex"

const { mapGetters } = createNamespacedHelpers("monitoring")

export default {
  components: { LineChart },
  data: () => ({}),
  async fetch({ store }) {
    store.dispatch("monitoring/fetchStats")
  },
  computed: {
    ...mapGetters(["getStats"])
  },
  created() {
    this.$store.watch(
      () => {
        return this.$store.getters["monitoring/getStats"] // could also put a Getter here
      },
      (newValue, oldValue) => {
        //something changed do something
        console.log(oldValue)
        console.log(newValue)
      },
      //Optional Deep if you need it
      {
        deep: true
      }
    )
  }
}
</script>
