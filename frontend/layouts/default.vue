<template>
  <v-app light>
    <v-navigation-drawer
      :mini-variant="miniVariant"
      :clipped="clipped"
      v-model="drawer"
      fixed
      app
    >
      <v-list>
        <v-list-tile avatar>
          <v-list-tile-avatar
            color="white"
          >
            <img src="/versatile-flow.png" alt="Versatile Flow" class="logo">
          </v-list-tile-avatar>
          <v-list-tile-title class="title">
            Realtime Bike
          </v-list-tile-title>
        </v-list-tile>
        <v-divider/>

        <div
          v-for="(item, i) in items"
          :key="i"
        >
          <template v-if="item.type === 'classic'">
            <v-list-tile
              :to="item.to"
              router
              exact
            >
              <v-list-tile-action>
                <v-icon v-html="item.icon"/>
              </v-list-tile-action>
              <v-list-tile-content>
                <v-list-tile-title v-text="item.title"/>
              </v-list-tile-content>
            </v-list-tile>
          </template>
          <template v-else-if="item.type === 'nested'">
            <v-list-group
              :prepend-icon="item.icon"
            >
              <v-list-tile slot="activator">
                <v-list-tile-title>{{ item.title }}</v-list-tile-title>
              </v-list-tile>

              <v-list-tile
                v-for="(element, ii) in item.inner"
                :to="element.to"
                :key="ii"
                router
                exact
              >
                <v-list-tile-title class="inner-element" v-text="element.title"/>
              </v-list-tile>
            </v-list-group>
          </template>
        </div>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar-side-icon v-if="getResponsive" @click="drawer = !drawer"/>
    <v-toolbar-title/>
    <v-content>
      <div class="ma-0 pa-0">
        <nuxt :responsive="getResponsive" />
      </div>
    </v-content>
    <v-footer v-if="getResponsive === false" :fixed="fixed" app class="footer">
      <span class="margin-auto"><i class="logo"/>&nbsp;Copyright &copy; Versatile Flow 2018</span>
    </v-footer>
  </v-app>
</template>

<script>
import { createNamespacedHelpers } from "vuex"

import cities from "../resources/centers"

const { mapGetters, mapActions } = createNamespacedHelpers("app")

export default {
  data() {
    return {
      clipped: false,
      drawer: false,
      fixed: false,
      items: [
        // { icon: "map", title: "Carte", to: "/" },
        {
          icon: "show_chart",
          title: "Séries Temporelles",
          to: "/",
          type: "classic"
        },
        {
          icon: "map",
          title: "Carte",
          type: "nested",
          to: "/carte",
          inner: cities.filter(city => city.carte.city !== null).map(city => ({
            title: city.carte.city,
            to: `/carte/${city.carte.slug}`
          }))
        },
        { icon: "dashboard", title: "Ville", to: "/city", type: "classic" },
        {
          icon: "list",
          title: "Classement",
          to: "/classement",
          type: "classic"
        },
        { icon: "person", title: "A propos", to: "/about", type: "classic" }
      ],
      miniVariant: false,
      right: true,
      rightDrawer: false,
      title: "Real-Time Bike",
      responsive: false
    }
  },
  computed: {
    ...mapGetters(["getResponsive"])
  },
  mounted() {
    this.onResponsiveInverted()
    window.addEventListener("resize", this.onResponsiveInverted)
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.onResponsiveInverted)
  },
  methods: {
    ...mapActions(["updateResponsive"]),
    onResponsiveInverted() {
      this.updateResponsive(window.innerWidth < 1300)
      if (this.getResponsive === false) {
        this.drawer = true
      }
    }
  }
}
</script>

<style scoped>
.logo {
  width: 3rem;
  height: 3rem;
}

.inner-element {
  padding-left: 85px;
  font-weight: 400;
}

.footer {
  height: 50px !important;
}

i.logo {
  background: url("../static/versatile-flow.svg") no-repeat center;
  background-size: contain;
  display: inline-block;
  height: 40px;
  vertical-align: middle;
  width: 40px;
}

.margin-auto {
  margin: auto;
}
</style>
