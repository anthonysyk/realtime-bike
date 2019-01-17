<script>
import { Radar } from "./BaseCharts"

export default {
  extends: Radar,
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
            label: "Trafic total",
            backgroundColor: "rgba(179,181,198,0.2)",
            borderColor: "rgba(179,181,198,1)",
            pointBackgroundColor: "rgba(179,181,198,1)",
            pointBorderColor: "#fff",
            pointHoverBackgroundColor: "#fff",
            pointHoverBorderColor: "rgba(179,181,198,1)",
            data: this.data.map(station => station.bikes_taken)
          }
        ]
      },
      { responsive: true, maintainAspectRatio: false }
    )
  }
}
</script>
