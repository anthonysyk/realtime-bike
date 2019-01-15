import { event } from "vue-analytics"

const getEmptyState = () => ({
  stations: [],
  fullStations: [],
  labels: [],
  data: [],
  bikesDroped: [],
  bikesTaken: []
})

const state = () => getEmptyState()

const colors = {
  blue: "rgba(52,152,219,.5)",
  grey: "rgba(93,109,126,.5)",
  green: "rgba(88,214,141,.5)",
  yellow: "rgba(244,208,63,.5)",
  lightblue: "rgba(133,193,233,.5)",
  lightred: "rgba(236,112,99,.5)",
  purple: "rgba(215,189,226,.5)"
}

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
        backgroundColor: colors.blue,
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
        backgroundColor: colors.green,
        data: state.bikesDroped
      },
      {
        label: "Nombre de vélos pris",
        backgroundColor: colors.lightred,
        data: state.bikesTaken.map(value => -value)
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
    state.bikesDroped = stats.map(station => station.state.bikes_droped)
    state.bikesTaken = stats.map(station => station.state.bikes_taken)
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
