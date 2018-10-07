package enums


object WindowInterval extends Enumeration {
  type WindowInterval = Value
  val `5min`, `15min`, `30min`, `1h`, `3h` = Value

  final val WINDOW_STATION_STATE_5min = createNamespace("5min")
  final val WINDOW_STATION_STATE_15min = createNamespace("15min")
  final val WINDOW_STATION_STATE_30min = createNamespace("30min")
  final val WINDOW_STATION_STATE_1H = createNamespace("1h")
  final val WINDOW_STATION_STATE_3H = createNamespace("3h")

  def validateInterval(interval: String): Boolean = this.values.map(_.toString).contains(interval)

  def createPath(interval: String): String = {
    assert(validateInterval(interval) )
    s"/stations/access/win/$interval"
  }

  def createNamespace(interval: String): String = {
    assert(validateInterval(interval))
    Seq (
      "window-station-state-",
      interval
    ).mkString ("-")
  }

}
