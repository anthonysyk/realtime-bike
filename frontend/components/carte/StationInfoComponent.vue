<template>
  <v-card class="overlay-content">
    <v-card-title primary-title>
      <h3 :style="getStationColor(station)" >{{ station.name }}</h3><br>
      <v-chip v-if="isStationClosed(station)" class="ml-3 pl-1" color="red" text-color="white">
        <v-avatar>
          <v-icon dark>remove_circle_outline</v-icon>
        </v-avatar>
        Fermée
      </v-chip>
    </v-card-title>
    <hr>
    <div class="grid-container">
      <!--<div/><div class="grid-item-text">Disponibilité : {{ Number.parseFloat(station.state.availability).toFixed(1) }} %</div>-->
      <v-icon>directions_bike</v-icon><div class="grid-item-text"> {{ station.available_bikes }} vélo(s)</div>
      <v-icon>local_parking</v-icon><div class="grid-item-text"> {{ station.bike_stands }} place(s)</div>
    </div>
  </v-card>
</template>

<script>
export default {
  name: "StationInfoComponent",
  props: {
    station: {
      type: Object,
      required: true,
      default: () => {}
    }
  },
  methods: {
    isStationClosed(station) {
      return station.status === "CLOSED"
    },
    getStationColor(station) {
      return station.status === "CLOSED" ? "color: red" : ""
    }
  }
}
</script>

<style lang="scss" scoped>
.grid-container {
  margin: 2rem 0 2rem 0;
  display: inline-grid;
  grid-template-columns: auto auto;
  grid-column-gap: 25px;
  grid-row-gap: 7px;
}

.grid-item-text {
  text-align: left;
}

.overlay-content {
  padding: 5px 10px;
  font-size: 1rem;
  border-radius: 15px;
  ul {
    text-align: left;
    list-style: none;
  }
}
</style>
