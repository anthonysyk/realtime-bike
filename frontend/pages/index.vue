<template>
  <v-layout column justify-center align-center>
    <v-flex xs12 sm8 md6>
      <v-card>
        <v-card-title class="headline">Bienvenue sur Paris Velib - by Versatile Flow</v-card-title>
        <v-card-text>
          <p>Cette application permet de visualiser en temps réel le traffic sur les bornes vélibs de Paris</p>
          <p>Les technologies utilisées pour le temps réel sont : Kafka, Kafka Stream, Interactive Queries</p>
          <p>Si vous souhaitez mon expertise sur ces sujets voici mon site <a href="http://www.versatile-flow.com" target="_blank">Versatile Flow</a>.</p>
          <p>Je suis disponible en tant que développeur indépendant</p>
          <div class="text-xs-right">
            <em><small>&mdash; Anthony SSI YAN KAI</small></em>
          </div>
          <hr class="my-3">
          <a href="http://www.versatile-flow.com/" target="_blank">Versatile Flow</a>
          <br>
          <a href="https://www.linkedin.com/in/anthony-ssi-yan-kai-386a93a0/" target="_blank">Linkedin</a>
          <br>
          <a href="https://github.com/assiyankai" target="_blank">GitHub</a>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" flat nuxt to="/carte">Continue</v-btn>
        </v-card-actions>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>
import Logo from '~/components/Logo.vue'

export default {
  components: {
    Logo
  }
}
</script>
