<script>
import { Doughnut } from "./BaseCharts"
import Colors from "../../utils/colors"

export default {
  extends: Doughnut,
  props: {
    data: {
      type: Array,
      default: () => [],
      required: true
    }
  },
  mounted() {
    this.renderChart(
      {
        labels: this.data.map(station => station.name),
        datasets: [
          {
            backgroundColor: Object.values(Colors.color()),
            data: this.data.map(station => station.bikes_taken)
          }
        ]
      },
      { responsive: true, maintainAspectRatio: false }
    )
  }
}
</script>
