import { event } from "vue-analytics"

const getEmptyState = () => ({
  stations: [],
  fullStations: [],
  labels: [],
  data: []
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
        backgroundColor: "#f87979",
        data: state.data
      }
    ],
    fill: false
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
    event("monitoring", `show-${selectedInterval}`, selectedStation.value)

    const stats = await this.$axios.$get(
      `kafka/station/access/win/${selectedInterval}/${selectedStation.value}`
    )
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
