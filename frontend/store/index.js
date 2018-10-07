import carte from "./modules/carte"
import monitoring from "./modules/monitoring"

const debug = process.env.NODE_ENV !== "production"

const store = {
  modules: {
    carte,
    monitoring
  },
  strict: debug
}

export default store
