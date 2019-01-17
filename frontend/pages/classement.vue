<template>
  <div>
    <v-subheader class="pl-3 pr-0">Top 5 des stations ayant le plus de trafic par ville sur 24h</v-subheader>
    <v-layout row wrap justify-space-around align-center class="ml-2 mr-2">
      <v-flex v-for="(top, idx) in getTopStations" :key="idx" xs12 sm6 md6 class="pa-3">
        <v-card>
          <v-card-title>
            {{ idx + 1 }} -
            {{ top.city }}
            <v-spacer/>
          </v-card-title>
          <v-data-table
            :headers="headers"
            :items="top.tops"
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
import { createNamespacedHelpers } from "vuex"

const { mapGetters } = createNamespacedHelpers("classement")

export default {
  data: () => ({
    pagination: { sortBy: "bikes_taken", descending: true, rowsPerPage: -1 },
    headers: [
      { text: "Station", value: "station", sortable: false, align: "left" },
      { text: "Vélos pris", value: "bikes_taken", align: "center" },
      { text: "Vélos déposés", value: "bikes_droped", align: "center" },
      { text: "Total", value: "total", align: "center" }
    ]
  }),
  async fetch({ store }) {
    store.dispatch("classement/fetchTopStations")
  },
  computed: {
    ...mapGetters(["getTopStations"])
  }
}
</script>

<style>
</style>
