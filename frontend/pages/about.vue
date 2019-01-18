<template>
  <v-layout column justify-center align-center class="about">
    <v-card xs12 sm8 md6>
      <v-card-title justify-center>
        <v-flex xs12>
          <v-card-media class="avatar-image"
                        src="/avatar_resized.png"
                        contain
          />
        </v-flex>
        <v-flex xs12 column justify-center align-center fluid d-flex class="pa-2 mt-2">
          <v-flex xs2 column justify-center align-center fluid d-flex class="pa-2 mt-2">
            <a href="https://www.linkedin.com/in/anthony-ssi-yan-kai-386a93a0/" target="_blank">
              <i class="fab fa-linkedin pb-1 icon linkedin"/>
            </a>
            <a href="http://www.versatile-flow.com/" target="_blank">
              <img src="/versatile-flow.png" alt="Versatile Flow" class="icon" width="50px">
            </a>
            <a href="https://github.com/anthonysyk" target="_blank">
              <i class="fab fa-github pb-1 icon github"/>
            </a>
          </v-flex>
        </v-flex>
        <v-flex xs12 column justify-center align-center fluid d-flex class="pa-2 mt-2 text-xs-center">
          <h2>Anthony SSI YAN KAI</h2>
        </v-flex>
      </v-card-title>
      <v-card-text>
        <div class="pl-5 pr-5 mb-4">
          <p>Realtime Bike est un side-project permettant de visualiser des données sur les stations de vélo en France.</p>
          <ul>
            <li>
              Collecte de données en <b>temps réel</b> à partir de données issues de l'open data avec <b>Kafka</b> et <b>Scala/Akka</b>
            </li>
            <li>
              Stockage des données dans Cassandra et S3 avec <b>Kafka Connect</b>
            </li>
            <li>
              <b>Agrégation</b> et <b>Fenêtrage</b> avec <b>Kafka Streams</b>
            </li>
            <li>
              Interrogation du <b>state</b> de Kafka Streams avec <b>Interactive Queries</b>
            </li>
          </ul>
        </div>
      </v-card-text>
    </v-card>
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
  width: 150px;
  height: 150px !important;
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

li {
  margin: 10px 0;
}
</style>
