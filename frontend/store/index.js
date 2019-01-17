import carte from "./modules/carte"
import charts from "./modules/charts"
import app from "./modules/app"
import classement from "./modules/classement"
import dashboard from "./modules/dashboard"

const debug = process.env.NODE_ENV !== "production"

const store = {
  modules: {
    app,
    carte,
    charts,
    classement,
    dashboard
  },
  strict: debug
}

export default store
