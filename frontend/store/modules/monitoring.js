const state = () => ({
  station: []
})

const getters = {
  getStation: state => state.station
}

export default {
  namespaced: true,
  state,
  getters
}
