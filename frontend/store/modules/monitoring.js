const state = () => ({
  stations: [],
  labels: [],
  data: []
})

const getters = {
  getStationsByCity: state => city =>
    state.stations
      .filter(station => station.contract_name === city)
      .map(station => station.id),
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
  async fetchStations({ commit }) {
    const stations = await this.$axios.$get("/kafka/stations")
    commit("updateStations", stations)
  },
  async fetchStats({ commit }, { selectedInterval, selectedStation }) {
    const stats = await this.$axios.$get(
      `kafka/station/access/win/${selectedInterval}/${selectedStation}`
    )
    commit("updateData", stats)
    commit("updateLabel", stats)
  }
}

const mutations = {
  updateStations(state, stations) {
    state.stations = stations
  },
  updateData(state, stats) {
    state.data = stats.map(station => station.available_bikes)
  },
  updateLabel(state, stats) {
    state.labels = stats.map(station => station.last_update)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
