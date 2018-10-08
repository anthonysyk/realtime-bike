package enums


object WindowInterval extends Enumeration {
  type WindowInterval = Value
  val `5min`, `15min`, `30min`, `1h`, `3h`, `12h`, `1j` = Value

  final val WINDOW_STATION_STATE_5min = createNamespace("5min")
  final val WINDOW_STATION_STATE_15min = createNamespace("15min")
  final val WINDOW_STATION_STATE_30min = createNamespace("30min")
  final val WINDOW_STATION_STATE_1h = createNamespace("1h")
  final val WINDOW_STATION_STATE_3h = createNamespace("3h")
  final val WINDOW_STATION_STATE_12h = createNamespace("12h")
  final val WINDOW_STATION_STATE_1j = createNamespace("1j")


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