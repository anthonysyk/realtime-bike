// initial state
const state = () => ({
  stations: new Map()
})

// getters
const getters = {
  getStations: state => state.stations,
}

// actions
const actions = {
  async fetchStations({commit}) {
    const response = await this.$axios.$get('/api/station/access/ALL')
    // const stations = response.map(stationArray => stationArray[1])
    const stations = response.reduce((map, obj) => (map[obj[0]] = obj[1], map), {});
    commit('updateStations', stations)
  }
}

// mutations
const mutations = {
  updateStations(state, stations) {
    state.stations = stations
  },
  updateStation(state, station) {
    const externalId = [station.number, station.contract_name].join("_")
    console.log(state.stations);
    state.stations.set(externalId, station)
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
