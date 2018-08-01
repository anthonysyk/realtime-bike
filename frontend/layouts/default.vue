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
        <v-list-tile
          v-for="(item, i) in items"
          :to="item.to"
          :key="i"
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
      </v-list>
    </v-navigation-drawer>
    <v-toolbar :clipped-left="clipped" fixed app light>
      <v-toolbar-side-icon @click="drawer = !drawer"/>
      <img src="/versatile-flow.png" alt="Versatile Flow" class="ml-2 logo">
      <v-toolbar-title class="ml-2" v-text="title"/>
      <v-spacer/>
    </v-toolbar>
    <v-content>
      <v-container>
        <nuxt/>
      </v-container>
    </v-content>
    <v-navigation-drawer
      :right="right"
      v-model="rightDrawer"
      temporary
      fixed
    >
      <v-list>
        <v-list-tile @click.native="right = !right">
          <v-list-tile-action>
            <v-icon light>compare_arrows</v-icon>
          </v-list-tile-action>
          <v-list-tile-title>Switch drawer (click me)</v-list-tile-title>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-footer :fixed="fixed" app class="footer">
      <span class="margin-auto"><i class="logo"/>&nbsp;Copyright &copy; Versatile Flow 2018</span>
    </v-footer>
  </v-app>
</template>

<script>
export default {
  data() {
    return {
      clipped: false,
      drawer: true,
      fixed: false,
      items: [
        { icon: "apps", title: "Bienvenue", to: "/" },
        { icon: "map", title: "Carte", to: "/carte" }
      ],
      miniVariant: false,
      right: true,
      rightDrawer: false,
      title: "Real-Time Velib"
    }
  }
}
</script>

<style scoped>
.logo {
  width: 3rem;
  height: 3rem;
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
