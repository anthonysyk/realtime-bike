import { event } from "vue-analytics"
import Colors from "../../utils/colors"

const getEmptyState = () => ({
  stations: [],
  fullStations: [],
  labels: [],
  data: [],
  bikesDroped: [],
  bikesTaken: [],
  availability: [],
  minBikesTaken: 0,
  minBikesDroped: 0,
  minAvailableBike: 0,
  minAvailableBikeStand: 0
})

const state = () => getEmptyState()

const getters = {
  getFullStations: state => state.fullStations,
  getStationsByCity: state => city =>
    state.stations
      .filter(station => station.contract_name === city)
      .map(station => ({ text: station.address, value: station.id })),
  getStats: state => ({
    labels: state.labels,
    datasets: [
      {
        label: "Nombre de vélos dans la station",
        backgroundColor: Colors.color().blue,
        data: state.data
      }
    ],
    fill: false
  }),
  getStatsDelta: state => ({
    labels: state.labels,
    datasets: [
      {
        label: "Nombre de vélos déposés",
        backgroundColor: Colors.color().green,
        data: state.bikesDroped
      },
      {
        label: "Nombre de vélos pris",
        backgroundColor: Colors.color().lightred,
        data: state.bikesTaken.map(value => value)
      }
    ],
    fill: false
  }),
  getStatsAvailability: state => ({
    labels: state.labels,
    datasets: [
      {
        label: "Places disponibles sur la station en %",
        backgroundColor: Colors.color().purple,
        data: state.availability
      }
    ]
  })
}

const actions = {
  async fetchFullStations({ commit }, city) {
    const fullStations = await this.$axios.$get(`/kafka/station/access/${city}`)
    const parsedStations = fullStations.map(
      station =>
        Object.assign({
          ...station,
          position: [station.position.lng, station.position.lat]
        }),
      {}
    )
    commit("updateFullStations", parsedStations)
  },
  async fetchStations({ commit }) {
    const stations = await this.$axios.$get("/kafka/stations")
    commit("updateStations", stations)
  },
  async fetchStats({ commit }, { selectedInterval, selectedStation }) {
    const url = `kafka/station/access/win/${selectedInterval}/${
      selectedStation.value
    }`

    event("charts", "hit", selectedStation.value)
    event("charts", `show-${selectedInterval}`, selectedStation.value)

    const stats = await this.$axios.$get(url)
    commit("updateData", stats)
    commit("updateLabel", stats)
  }
}

const mutations = {
  updateFullStations(state, stations) {
    state.fullStations = stations
  },
  updateStations(state, stations) {
    state.stations = stations
  },
  updateData(state, stats) {
    state.data = stats.map(station => station.available_bikes)
    state.bikesDroped = stats.map(station => station.bikes_droped)
    state.bikesTaken = stats.map(station => station.bikes_taken)
    state.availability = stats.map(station =>
      Number.parseFloat(
        station.bike_stands !== 0
          ? (station.available_bike_stands / station.bike_stands) * 100
          : 0
      ).toFixed(1)
    )
  },
  updateLabel(state, stats) {
    state.labels = stats.map(station => station.label)
  },
  resetState(state) {
    Object.assign(state, getEmptyState())
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
