// initial state
const state = () => ({
  responsive: false
})

// getters
const getters = {
  getResponsive: state => state.responsive
}

// actions
const actions = {
  updateResponsive({ commit }, responsive) {
    commit("updateResponsive", responsive)
  }
}

// mutations
const mutations = {
  updateResponsive(state, responsive) {
    state.responsive = responsive
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
