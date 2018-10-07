<script>
// MonthlyIncome.vue
import { Line } from "vue-chartjs"

export default {
  extends: Line,
  props: {
    data: {
      type: Array,
      default: () => []
    },
    options: {
      type: Object,
      default: () => {}
    }
  },
  mounted() {
    this.renderChart(this.data, this.options)
  }
}
</script>
