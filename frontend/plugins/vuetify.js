import Vue from "vue"
import Vuetify from "vuetify"
import colors from "vuetify/es5/util/colors"

Vue.use(Vuetify, {
  theme: {
    primary: colors.blue.darken1,
    secondary: colors.blueGrey.lighten1,
    accent: colors.blueGrey.darken2,
    error: colors.deepOrange.darken2,
    warning: colors.yellow.base,
    info: colors.blue.base,
    success: colors.green.base
  }
})
