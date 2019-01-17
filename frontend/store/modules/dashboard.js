const getEmptyState = () => ({
  topCities: []
})

const state = () => getEmptyState()

const getters = {
  getTopCities: state => state.topCities
}

const actions = {
  async fetchTopCities({ commit }) {
    const topCities = await this.$axios.$get(`/kafka/city/top`)
    commit("updateTopCities", topCities)
  }
}

const mutations = {
  updateTopCities(state, stations) {
    state.topCities = stations.sort(
      (a, b) => (a.bikes_taken < b.bikes_taken ? 1 : -1)
    )
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
