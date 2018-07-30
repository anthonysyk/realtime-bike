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
    </vl-map>
    <!--<div style="padding: 20px">-->
      <!--Zoom: {{ value.zoom }}<br>-->
      <!--Center: {{ value.center }}<br>-->
      <!--Rotation: {{ value.rotation }}<br>-->
      <!--My geolocation: {{ value.geolocPosition }}-->
    <!--</div>-->
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: Object,
      required: true,
      default: () => {}
    }
  }
}
</script>
