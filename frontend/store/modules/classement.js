const getEmptyState = () => ({
  topStations: {}
})

const state = () => getEmptyState()

const getters = {
  getTopStations: state =>
    Object.keys(state.topStations)
      .map(key => {
        return {
          city: key,
          tops: state.topStations[key],
          total: state.topStations[key]
            .map(station => station.bikes_taken)
            .reduce((a, b) => a + b)
        }
      })
      .sort((a, b) => b.total - a.total)
}

const actions = {
  async fetchTopStations({ commit }) {
    const topStations = await this.$axios.$get(`/kafka/station/top`)
    commit("updateTopStations", topStations)
  }
}

const mutations = {
  updateTopStations(state, stations) {
    state.topStations = stations
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
