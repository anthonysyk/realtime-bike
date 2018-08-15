<template>
  <v-card class="overlay-content">
    <v-card-title primary-title>
      <h1>{{ station.name }}</h1><br>
    </v-card-title>
    <ul>
      <li>Disponibilité : {{ Number.parseFloat(station.state.availability).toFixed(1) }} %</li>
      <li>Adresse: {{ station.address }}</li>
      <li><v-icon>directions_bike</v-icon> Vélos Disponibles: {{ station.available_bikes }}</li>
      <li><v-icon>local_parking</v-icon>Nombres de stands: {{ station.bike_stands }}</li>
    </ul>
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
  }
}
</script>

<style lang="scss" scoped>
.overlay-content {
  padding: 10px 20px;
  font-size: 16px;
  h1 {
    font-size: 30px;
  }
  ul {
    list-style: none;
  }
}
</style>
