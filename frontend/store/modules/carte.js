// initial state
const state = () => ({
  stations: []
})

// getters
const getters = {
  getStations: state => state.stations,
  getStationsByContract: state => contract =>
    state.stations.filter(station => station.contract_name === contract)
}

// actions
const actions = {
  async fetchStations({ commit }, city) {
    const stations = await this.$axios.$get(`/kafka/station/access/${city}`)
    const parsedStations = stations.map(
      station =>
        Object.assign({
          ...station,
          position: [station.position.lng, station.position.lat]
        }),
      {}
    )
    commit("updateStations", parsedStations)
  }
}

// mutations
const mutations = {
  updateStations(state, stations) {
    state.stations = stations
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
