<template>
  <v-card class="overlay-content text-xs-center" width="300">
    <v-card-title primary-title class="text-xs-center">
      <h3 :style="getStationColor(station)" class="text-xs-center">{{ station.name }}</h3><br>
      <v-chip v-if="isStationClosed(station)" color="red" text-color="white" class="margin-auto">
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
    <hr>
    <div class="grid-container">
      <div/><div class="grid-item-text">Places disponibles : {{ Number.parseFloat(station.bike_stands !== 0 ? (station.available_bikes / station.bike_stands)*100 : 0).toFixed(1) }} %</div>
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
h3 {
  width: 100%;
  margin-bottom: 0.4rem;
}

.margin-auto {
  margin: auto !important;
}

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
