import carte from "./modules/carte"

const debug = process.env.NODE_ENV !== "production"

const store = {
  modules: {
    carte
  },
  strict: debug
}

export default store
