<template>
  <div>
    <vl-map :load-tiles-while-animating="true" :load-tiles-while-interacting="true"
            data-projection="EPSG:4326" style="height: 70vh">
      <vl-view :zoom.sync="value.zoom" :center.sync="value.center" :rotation.sync="value.rotation"/>
      <vl-geoloc @update:position="value.geolocPosition = $event">
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
      <div v-for="station in value.stations" :key="station.id">
        <vl-layer-vector>
          <vl-source-vector>
            <vl-feature>
              <vl-geom-circle :coordinates="station.coordinates" :radius="100"/>
              <vl-style-box>
                <vl-style-stroke color="blue"/>
                <vl-style-fill color="rgba(255,255,255,0.5)"/>
                <!--<vl-style-text text="I'm circle"/>-->
              </vl-style-box>
            </vl-feature>
          </vl-source-vector>
        </vl-layer-vector>
      </div>
      <!--circle-->

      <!-- selected feature popup -->
      <div v-for="station in value.stations" :key="station.id">
        <vl-overlay id="overlay" :position="station.coordinates" class="overlay-content">
          <template slot-scope="scope">
            <div class="overlay-content">
              Hello world!<br>
              Position: {{ scope.position }}
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
    </div>
  </div>
</template>

<script>
// import { random } from "lodash"

export default {
  props: {
    value: {
      type: Object,
      required: true,
      default: () => {}
    }
  },
  data: () => ({
    zoom: 2,
    center: [0, 0],
    rotation: 0,
    overlayCoordinate: [30, 30]
  }),
  methods: {
    pointOnSurface: this.findPointOnSurface
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
  position: relative;
  word-wrap: normal;
  ackground-color: #f8f8f8;
  font-family: Roboto Mono, Monaco, courier, monospace;
}

.cover-main .logo {
  width: 100px;
  height: 100px;
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
