import carte from "./modules/carte"
import monitoring from "./modules/monitoring"
import app from "./modules/app"

const debug = process.env.NODE_ENV !== "production"

const store = {
  modules: {
    app,
    carte,
    monitoring
  },
  strict: debug
}

export default store
