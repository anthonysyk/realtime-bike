const pkg = require("./package")

const nodeExternals = require("webpack-node-externals")

const isDev = process.env.NODE_ENV === "development"

const GoogleSearchConsoleTag = {
  name: "google-site-verification",
  content: "BSpbU-fffE31gtY5DYUSjf4RkkV31k6VaqLa1rtXTlI"
}

const meta = [
  { charset: "utf-8" },
  { name: "viewport", content: "width=device-width, initial-scale=1" },
  { hid: "description", name: "description", content: pkg.description },
  GoogleSearchConsoleTag
]

module.exports = {
  mode: "spa",
  /*
  ** Headers of the page
  */
  head: {
    title: "Real-Time Bike",
    meta: meta,
    link: [
      { rel: "icon", type: "image/x-icon", href: "/favicon.ico" },
      {
        rel: "stylesheet",
        href:
          "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons"
      },
      {
        rel: "stylesheet",
        href: "https://use.fontawesome.com/releases/v5.6.3/css/all.css",
        integrity:
          "sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/",
        crossorigin: "anonymous"
      }
    ],
    script: []
  },

  /*
  ** Customize the progress-bar color
  */
  loading: { color: "#FFFFFF" },

  /*
  ** Global CSS
  */
  css: ["vuetify/src/stylus/main.styl"],

  /*
  ** Plugins to load before mounting the App
  */
  plugins: ["@/plugins/vuetify", "@/plugins/vuelayers.js"],
  /*
  ** Nuxt.js modules
  */
  modules: [
    // Doc: https://github.com/nuxt-community/axios-module#usage
    "@nuxtjs/axios",
    "@nuxtjs/proxy",
    [
      "@nuxtjs/google-analytics",
      {
        id: "UA-115881480-2",
        autoTracking: {
          page: true, //!isDev
          exception: true
        },
        debug: {
          enabled: isDev,
          sendHitTask: !isDev
        }
      }
    ]
  ],
  /*
  ** Axios module configuration
  */
  axios: {
    // See https://github.com/nuxt-community/axios-module#options
    baseURL: isDev ? "http://localhost" : "http://realtime-bike.fr",
    browserBaseURL: isDev ? "http://localhost" : "http://realtime-bike.fr"
  },
  /*
  ** Build configuration
  */
  build: {
    /*
    ** You can extend webpack config here
    */
    extend(config, ctx) {
      // Run ESLint on save
      if (ctx.isDev && ctx.isClient) {
        config.module.rules.push({
          enforce: "pre",
          test: /\.(js|vue)$/,
          loader: "eslint-loader",
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
