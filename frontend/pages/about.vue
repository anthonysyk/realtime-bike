<template>
  <v-layout column justify-center align-center class="about">
    <v-flex xs12>
      <v-card-media class="avatar-image"
                    src="/avatar_resized.png"
                    contain
      />
    </v-flex>
    <v-flex xs8 column justify-center align-center fluid d-flex class="pa-2">
      <a href="https://www.linkedin.com/in/anthony-ssi-yan-kai-386a93a0/" target="_blank">
        <i class="fab fa-linkedin pb-1 icon linkedin"/>
      </a>
      <a href="http://www.versatile-flow.com/" target="_blank">
        <img src="/versatile-flow.png" alt="Versatile Flow" class="icon" width="50px">
      </a>
      <a href="https://github.com/assiyankai" target="_blank">
        <i class="fab fa-github pb-1 icon github"/>
      </a>
    </v-flex>

    <!--<v-flex xs12 sm8 md6 class="mt-5">-->
    <!--<v-card class="pa-5">-->
    <!--<v-card-title class="headline">Bienvenue sur Real-Time Bike - by Versatile Flow</v-card-title>-->
    <!--<v-card-text>-->
    <!--<p>Cette application permet de visualiser en temps réel le traffic sur les stations de vélos de France</p>-->
    <!--<p>Les technologies utilisées pour le temps réel sont : Kafka, Kafka Stream, Interactive Queries</p>-->
    <!--<div class="text-xs-right">-->
    <!--<em>-->
    <!--<small>&mdash; Anthony SSI YAN KAI</small>-->
    <!--</em>-->
    <!--</div>-->
    <!--<hr class="my-3">-->
    <!--<v-layout row>-->
    <!--<v-flex xs3/>-->
    <!--<v-flex xs8>-->
    <!--<div class="pt-4 pl-1">-->
    <!--<a href="http://www.versatile-flow.com/" target="_blank">Versatile Flow</a>-->
    <!--<br>-->
    <!--<a href="https://www.linkedin.com/in/anthony-ssi-yan-kai-386a93a0/" target="_blank">Linkedin</a>-->
    <!--<br>-->
    <!--<a href="https://github.com/assiyankai" target="_blank">GitHub</a>-->
    <!--</div>-->
    <!--</v-flex>-->
    <!--<v-flex xs1/>-->
    <!--</v-layout>-->
    <!--</v-card-text>-->
    <!--</v-card>-->
    <!--</v-flex>-->
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

<style lang="scss" scoped>
.avatar-image {
  border: 2px solid lightgray;
  border-radius: 50%;
  margin: 0 auto;
  position: relative;
  width: 200px;
  height: 200px !important;
}

i {
  font-size: 3em;
}

.icon {
  margin-left: 10px;
  margin-right: 10px;
  transition: 0.5s;
  &:hover {
    transform: scale(1.3);
  }
}

.linkedin {
  color: #0077b5;
}

.github {
  color: #24292e;
}

.about {
  margin-top: 5rem;
}
</style>
