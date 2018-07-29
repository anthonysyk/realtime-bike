const pkg = require('./package')

const nodeExternals = require('webpack-node-externals')

module.exports = {
  mode: 'spa',

  /*
  ** Headers of the page
  */
  head: {
    title: pkg.name,
    meta: [
      {charset: 'utf-8'},
      {name: 'viewport', content: 'width=device-width, initial-scale=1'},
      {hid: 'description', name: 'description', content: pkg.description}
    ],
    link: [
      {rel: 'icon', type: 'image/x-icon', href: '/favicon.ico'},
      {rel: 'stylesheet', href: 'https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons'}
    ]
  },

  /*
  ** Customize the progress-bar color
  */
  loading: {color: '#FFFFFF'},

  /*
  ** Global CSS
  */
  css: [
    'vuetify/src/stylus/main.styl',
    '@/assets/css/utility.scss'
  ],

  /*
  ** Plugins to load before mounting the App
  */
  plugins: [
    '@/plugins/vuetify',
    {src: '@/plugins/vue-native-websocket.js', ssr: false}

  ],

  /*
  ** Nuxt.js modules
  */
  modules: [
    // Doc: https://github.com/nuxt-community/axios-module#usage
    '@nuxtjs/axios',
    '@nuxtjs/proxy'
  ],
  /*
  ** Axios module configuration
  */
  axios: {
    // See https://github.com/nuxt-community/axios-module#options
    proxy: true
  },
  proxy: {
    '/api/': { target: 'http://localhost:9000', pathRewrite: {'^/api/': ''} },
  },
  /*
  ** Build configuration
  */
  build: {
    /*
    ** You can extend webpack config here
    */
    vendor: ['vue-native-websocket'],
    extend(config, ctx) {
      // Run ESLint on save
      if (ctx.isDev && ctx.isClient) {
        config.module.rules.push({
          enforce: 'pre',
          test: /\.(js|vue)$/,
          loader: 'eslint-loader',
          exclude: /(node_modules)/
        })
      }
      if (ctx.isServer) {
        config.externals = [
          nodeExternals({
            whitelist: [/^vuetify/]
          })
        ]
      }
    }
  }
}
