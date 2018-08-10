<template>
  <div class="map">
    <vl-map :load-tiles-while-animating="true" :load-tiles-while-interacting="true"
            data-projection="EPSG:4326"
            style="height: 70vh" @click="clickCoordinate = $event.coordinate">
      <vl-view :zoom.sync="value.zoom" :center.sync="value.center" :rotation.sync="value.rotation"/>
      <vl-geoloc>
        <template slot-scope="geoloc">
          <vl-feature v-if="geoloc.position" id="position-feature">
            <vl-geom-point :coordinates="geoloc.position"/>
            <vl-style-box>
              <vl-style-icon :scale="0.4" :anchor="[0.5, 1]" src="_media/marker.png"/>
            </vl-style-box>
          </vl-feature>
        </template>
      </vl-geoloc>

      <vl-layer-tile id="osm">
        <vl-source-osm/>
      </vl-layer-tile>

      <!--cercle-->
      <div v-for="station in stations" :key="station.id">
        <vl-layer-vector>
          <vl-source-vector>
            <vl-feature>
              <vl-geom-circle :coordinates="station.position" :radius="100"/>
              <vl-style-box>
                <vl-style-stroke color="#3399cc"/>
                <vl-style-fill color="rgba(255,255,255,0.5)"/>
                <!--<vl-style-text text="I'm circle"/>-->
              </vl-style-box>
            </vl-feature>
          </vl-source-vector>
        </vl-layer-vector>
      </div>
      <!--circle-->

      <!-- selected feature popup -->
      <div v-for="station in stations" v-if="isStationClicked(station)" :key="`overlay-${station.id}`">
        <vl-overlay id="overlay" :position="station.position" class="overlay-content">
          <template slot-scope="scope">
            <div class="overlay-content">
              Hello world!<br>
              Position: {{ scope.position }}
              Click: {{ clickCoordinate }}
              isStationClick: {{ isStationClicked(station) }}
            </div>
          </template>
        </vl-overlay>
      </div>
      <!--// selected popup -->
    </vl-map>
    <div style="padding: 20px">
      Zoom: {{ value.zoom }}<br>
      Center: {{ value.center }}<br>
      Rotation: {{ value.rotation }}<br>
      My geolocation: {{ value.geolocPosition }}

      Exemple: {{ stations[0] && stations[0].position }}
    </div>
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: Object,
      required: true,
      default: () => {}
    },
    stations: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  data() {
    return {
      zoom: 2,
      center: [0, 0],
      rotation: 0,
      overlayCoordinate: [30, 30],
      clickCoordinate: [],
      selectedStation: {}
    }
  },
  methods: {
    isStationClicked(station) {
      const xClick = this.clickCoordinate[0]
      const yClick = this.clickCoordinate[1]

      const xStation = station.position[0]
      const yStation = station.position[1]

      function isInside(zClick, zStation) {
        return Math.abs(zClick - zStation) < 0.0008
      }

      return (
        xClick &&
        yClick &&
        isInside(xClick, xStation) &&
        isInside(yClick, yStation)
      )
    }
  }
}
</script>

<style>
.cover-main pre {
  position: relative;
  -webkit-font-smoothing: initial;
  line-height: 1.5rem;
  margin: 1.2em 0;
  overflow: auto;
  padding: 0 1.4rem;
  word-wrap: normal;
  background-color: #f8f8f8;
  font-family: Roboto Mono, Monaco, courier, monospace;
}

.cover-main .logo {
  width: 100px;
  height: 100px;
}

.map {
  cursor: pointer;
}

.markdown-section {
  max-width: 1000px;
}

.vuep {
  flex-direction: column;
  clear: both;
  overflow: hidden;
  box-shadow: 0 0 13px #a0a0a0;
  height: auto;
  margin: 30px 0;
}

.vuep-editor {
  height: 400px;
}

.vuep-editor,
.vuep-preview {
  width: auto;
  overflow: visible;
  margin-right: 0;
}

.vuep-preview {
  padding: 0;
}

.overlay-content {
  background: #efefef;
  box-shadow: 0 5px 10px rgba(2, 2, 2, 0.2);
  padding: 10px 20px;
  font-size: 16px;
}
</style>
