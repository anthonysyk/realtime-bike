<template>
  <div>
    <div>
      <div>
        <v-card class="map">
          <vl-map ref="map" :load-tiles-while-animating="true"
                  :load-tiles-while-interacting="true" data-projection="EPSG:4326"
                  style="height: 70vh; width: 100%" @click="handleClickMap"
                  @movestart="enableLoader" @moveend="onMapPostCompose">
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

            <loader :loader="loader" :clickable="true"/>
            <vl-layer-tile id="bingmaps">
              <vl-source-bing-maps :api-key="apiKey" :imagery-set="imagerySet"/>
            </vl-layer-tile>

            <!-- selected feature popup -->
            <div v-for="station in getStations()" :key="`overlay-${station.number}`">
              {{ selectStation(station) }}
              <!--CIRCLES-->
              <vl-feature>
                <vl-geom-circle :coordinates="station.position" :radius="selectedStation.number === station.number ? 100 : 60"/>
                <vl-style-box>
                  <vl-style-stroke :color="getStationColor(station.status)"/>
                  <vl-style-fill :color="getStationColor(station.status)"/>
                </vl-style-box>
              </vl-feature>
              <!--CIRCLES-->

            </div>
            <!--// selected popup -->
          </vl-map>
          <div v-if="Object.keys(selectedStation).length > 0" class="information-layout">
            <v-flex xs12 sm5 class="station-information text-xs-center">
              <station-info-component :station="selectedStation"/>
            </v-flex>
          </div>
        </v-card>
      </div>
    </div>
  </div>
</template>

<script>
import { transformExtent } from "vuelayers/lib/_esm/ol-ext"
import { OVERLAY_POSITIONING } from "vuelayers/lib/_esm/ol-ext/consts"
import StationInfoComponent from "./StationInfoComponent"
import Loader from "~/components/Loader.vue"

export default {
  components: { StationInfoComponent, Loader },
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
      apiKey:
        "Anb1dy0vZfx5t6afKqjCGe8iwGcTabo1r9AuJZt1OX3oMpawO0eR0Ef1Yk1NYmyL",
      imagerySet: "Road",
      positionning: OVERLAY_POSITIONING.CENTER_CENTER,
      zoom: 2,
      center: [0, 0],
      rotation: 0,
      overlayCoordinate: [30, 30],
      clickCoordinate: [],
      selectedStation: {},
      corners: {},
      currExtent: [],
      hasMap: false,
      loader: false
    }
  },
  watch: {
    stations: function(val, oldval) {
      if (oldval !== val) this.loader = false
    }
  },
  methods: {
    handleClickMap(event) {
      this.clickCoordinate = event.coordinate
      return Object.keys(this.selectedStation).length > 0 &&
        this.isStationClicked(this.selectedStation)
        ? this.selectedStation
        : (this.selectedStation = {})
    },
    selectStation(station) {
      this.isStationClicked(station)
        ? (this.selectedStation = station)
        : this.selectedStation
    },
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
        return Math.abs(zClick - zStation) < 0.0006
      }

      return (
        xClick &&
        yClick &&
        isInside(xClick, xStation) &&
        isInside(yClick, yStation)
      )
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

      const coordinates = {
        topLeft: cornerCoordinates[0],
        bottomLeft: cornerCoordinates[1],
        bottomRight: cornerCoordinates[2],
        topRight: cornerCoordinates[3]
      }

      this.$emit("fetchStationsEmit", {
        coordinates
      })

      this.corners = coordinates
    },
    isInsideMap(station) {
      const xStation = station.position[0]
      const yStation = station.position[1]

      const { topLeft, bottomRight, bottomLeft, topRight } = this.corners

      const xIsInside = xStation > topLeft && xStation < bottomRight
      const yIsInside = yStation > bottomLeft && yStation < topRight
      return xIsInside && yIsInside
    },
    enableLoader() {
      this.value.city === null ? (this.loader = false) : (this.loader = true)
    }
  }
}
</script>

<style lang="scss" scoped>
.station-information {
  position: sticky;
  pointer-events: auto;
  z-index: 1;
}

.isSelected {
  transform: scale(1.2);
  color: green;
}

.information-layout {
  pointer-events: none;
  position: absolute;
  z-index: 0;
  width: 100%;
  top: 0;
}

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

<style>
.ol-attribution {
  display: none !important;
}
.ol-zoom {
  top: initial;
  bottom: 0.5rem !important;
  left: 0.5rem !important;
}
</style>
