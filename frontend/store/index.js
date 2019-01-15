import carte from "./modules/carte"
import charts from "./modules/charts"
import app from "./modules/app"

const debug = process.env.NODE_ENV !== "production"

const store = {
  modules: {
    app,
    carte,
    charts
  },
  strict: debug
}

export default store
