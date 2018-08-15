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
  async fetchStations({ commit }) {
    const stations = await this.$axios.$get("/kafka/station/access/ALL")
    const parsedStations = stations.map(
      station =>
        Object.assign({
          ...station,
          position: [station.position.lng, station.position.lat]
        }),
      {}
    )
    console.log(stations[0])
    console.log(parsedStations[0])
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
