const express = require("express")
const { Nuxt, Builder } = require("nuxt")
const app = express()
const host = process.env.HOST || "0.0.0.0"
const port = process.env.PORT || 4000
const proxy = require("http-proxy-middleware")

app.set("port", port)

// Import and Set Nuxt.js options
let config = require("../nuxt.config.js")
config.dev = !(process.env.NODE_ENV === "production")

app.use(
  "/kafka",
  proxy({
    target: "http://192.168.1.26:9001",
    changeOrigin: true,
    pathRewrite: { "^/kafka": "" }
  })
)

async function start() {
  // Init Nuxt.js
  const nuxt = new Nuxt(config)

  // Build only in dev mode
  if (config.dev) {
    const builder = new Builder(nuxt)
    await builder.build()
  }

  // Give nuxt middleware to express
  app.use(nuxt.render)

  // Listen the server
  app.listen(port, host)
  console.log("Server listening on http://" + host + ":" + port) // eslint-disable-line no-console
}

start()
