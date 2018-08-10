<template>
  <v-layout column justify-center align-center>
    <v-flex xs12 sm8 md6 class="mt-5">
      <v-card class="pa-5">
        <v-card-title class="headline">Bienvenue sur Real-Time Velib - by Versatile Flow</v-card-title>
        <v-card-text>
          <p>Cette application permet de visualiser en temps réel le traffic sur les bornes vélibs de France</p>
          <p>Les technologies utilisées pour le temps réel sont : Kafka, Kafka Stream, Interactive Queries</p>
          <div class="text-xs-right">
            <em>
              <small>&mdash; Anthony SSI YAN KAI</small>
            </em>
          </div>
          <hr class="my-3">
          <v-layout row>
            <v-flex xs3>
              <v-card-media class="avatar-image"
                            src="/avatar_resized.png"
                            height="125px"
                            contain
              />
            </v-flex>
            <v-flex xs8>
              <div class="pt-4 pl-1">
                <a href="http://www.versatile-flow.com/" target="_blank">Versatile Flow</a>
                <br>
                <a href="https://www.linkedin.com/in/anthony-ssi-yan-kai-386a93a0/" target="_blank">Linkedin</a>
                <br>
                <a href="https://github.com/assiyankai" target="_blank">GitHub</a>
              </div>
            </v-flex>
            <v-flex xs1/>
          </v-layout>
        </v-card-text>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
import Logo from "~/components/Logo.vue"

export default {
  components: {
    Logo
  }
}
</script>

<style scoped>
.avatar-image {
  border: 2px solid lightgray;
  border-radius: 50%;
  margin: 0 auto;
  position: relative;
  width: 130px;
  height: 130px !important;
}
</style>
