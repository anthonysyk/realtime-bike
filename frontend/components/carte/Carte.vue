<template>
  <div class="map">
    <vl-map ref="map" :load-tiles-while-animating="true"
            :load-tiles-while-interacting="true" data-projection="EPSG:4326"
            style="height: 70vh" @click="clickCoordinate = $event.coordinate" @pointermove="clickCoordinate = $event.coordinate" @moveend="onMapPostCompose">
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

      <vl-layer-tile id="sputnik">
        <vl-source-sputnik/>
      </vl-layer-tile>

      <!-- selected feature popup -->
      <div v-for="station in getStations()" :key="`overlay-${station.number}`">
        <vl-overlay v-if="isStationClicked(station)" id="overlay" :position="station.position" :positioning="positionning" class="overlay-content">
          <template slot-scope="scope">
            <station-info-component :station="station"/>
          </template>
        </vl-overlay>

        <!--CIRCLES-->
        <vl-feature>
          <vl-geom-circle :coordinates="station.position" :radius="60"/>
          <vl-style-box>
            <vl-style-stroke :color="getStationColor(station.status)"/>
            <vl-style-fill color="rgba(255,255,255,0.5)"/>
          </vl-style-box>
        </vl-feature>
        <!--CIRCLES-->

      </div>
      <!--// selected popup -->
    </vl-map>
    <div style="padding: 20px">
      Zoom: {{ value.zoom }}<br>
      Center: {{ value.center }}<br>
      Rotation: {{ value.rotation }}<br>
      Click: {{ clickCoordinate }}<br>
      Stations: {{ getStations().length }}<br>
    </div>
  </div>
</template>

<script>
import { transformExtent } from "vuelayers/lib/_esm/ol-ext"
import { OVERLAY_POSITIONING } from "vuelayers/lib/_esm/ol-ext/consts"
import StationInfoComponent from "./StationInfoComponent"

export default {
  components: { StationInfoComponent },
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
      positionning: OVERLAY_POSITIONING.CENTER_CENTER,
      zoom: 2,
      center: [0, 0],
      rotation: 0,
      overlayCoordinate: [30, 30],
      clickCoordinate: [],
      selectedStation: {},
      corners: {},
      currExtent: [],
      hasMap: false
    }
  },
  methods: {
    getStations() {
      return this.stations.filter(station => this.isInsideMap(station))
    },
    getOverlayPosition() {
      const xClick = this.clickCoordinate[0]
      const yClick = this.clickCoordinate[1]

      const { topLeft, bottomRight, bottomLeft, topRight } = this.corners

      let vertical
      let horizontal

      if (Math.abs(yClick - topRight) > Math.abs(yClick - bottomLeft)) {
        vertical = "bottom"
      } else vertical = "top"

      if (Math.abs(xClick - topLeft) > Math.abs(xClick - bottomRight)) {
        horizontal = "right"
      } else horizontal = "left"

      if (xClick && yClick) {
        this.positionning = `${vertical}-${horizontal}`
      }
    },
    isStationClicked(station) {
      const xClick = this.clickCoordinate[0]
      const yClick = this.clickCoordinate[1]

      const xStation = station.position[0]
      const yStation = station.position[1]

      this.getOverlayPosition()

      function isInside(zClick, zStation) {
        return Math.abs(zClick - zStation) < 0.0004
      }

      return (
        xClick &&
        yClick &&
        isInside(xClick, xStation) &&
        isInside(yClick, yStation)
      )
    },
    isLoaded() {
      // console.log(this.stations)
      // console.log("STATIONS ARE LOADED ! YAAAY")
    },
    getStationColor(status) {
      return status === "CLOSED" ? "red" : "#3399cc"
    },
    onMapPostCompose() {
      if (this.currExtent === this.$refs.map.$map.frameState_.extent) return

      const cornerCoordinates = transformExtent(
        this.$refs.map.$map.frameState_.extent,
        "EPSG:3857",
        "EPSG:4326"
      )

      this.corners = {
        topLeft: cornerCoordinates[0],
        bottomLeft: cornerCoordinates[1],
        bottomRight: cornerCoordinates[2],
        topRight: cornerCoordinates[3]
      }
    },
    isInsideMap(station) {
      const xStation = station.position[0]
      const yStation = station.position[1]

      const { topLeft, bottomRight, bottomLeft, topRight } = this.corners

      const xIsInside = xStation > topLeft && xStation < bottomRight
      const yIsInside = yStation > bottomLeft && yStation < topRight
      return xIsInside && yIsInside
    }
  }
}
</script>

<style lang="scss" scoped>
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
</style>
