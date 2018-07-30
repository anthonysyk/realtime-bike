import Vue from 'vue'
import VueLayers from 'vuelayers'
import 'vuelayers/lib/style.css' // needs css-loader

export default ({store}, inject) => {
  Vue.use(VueLayers)
}
