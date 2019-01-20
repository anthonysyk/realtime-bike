<template>
  <div>
    <v-subheader class="pl-3 pr-0">Comparaison entre les différentes villes sur les dernières 24h</v-subheader>
    <v-layout row wrap justify-space-around align-center>
      <v-flex xs12 class="pa-3">
        <v-card class="pa-4">
          <v-card-title>
            <h1 class="ma-auto">Nombre de vélos utilisés par ville en 24h</h1>&nbsp;&nbsp;
            <v-spacer/>
          </v-card-title>
          <doughnut-example v-if="getTopCities.length > 0" :data="getTopCities"/>
        </v-card>
      </v-flex>
      <v-flex xs12 class="pa-3 mb-2">
        <v-card>
          <v-card-title>
            <h1 class="ma-auto">Trafic par ville sur 24h</h1>
            <v-spacer/>
          </v-card-title>
          <v-data-table
            :headers="headers"
            :items="getTopCities"
            :pagination.sync="pagination"
            class="elevation-1"
            hide-actions
          >
            <template slot="items" slot-scope="props">
              <td class="text-xs-left">{{ props.item.name }}</td>
              <td class="text-xs-center red--text">{{ props.item.bikes_taken }}</td>
              <td class="text-xs-center green--text">{{ props.item.bikes_droped }}</td>
              <td class="text-xs-center">{{ props.item.bikes_taken + props.item.bikes_droped }}</td>
            </template>
          </v-data-table>
        </v-card>
      </v-flex>
    </v-layout>
  </div>
</template>

<script>
import DoughnutExample from "../components/charts/DoughnutChart"

import { createNamespacedHelpers } from "vuex"

const { mapGetters } = createNamespacedHelpers("dashboard")

export default {
  components: {
    DoughnutExample
  },
  data: () => ({
    pagination: { sortBy: "bikes_taken", descending: true, rowsPerPage: -1 },
    headers: [
      { text: "Ville", value: "city", sortable: false, align: "left" },
      { text: "Vélos pris", value: "bikes_taken", align: "center" },
      { text: "Vélos déposés", value: "bikes_droped", align: "center" },
      { text: "Total", value: "total", align: "center" }
    ]
  }),
  async fetch({ store }) {
    store.dispatch("dashboard/fetchTopCities")
  },
  computed: {
    ...mapGetters(["getTopCities"])
  }
}
</script>
