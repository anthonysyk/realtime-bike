package enums


object WindowInterval extends Enumeration {
  type WindowInterval = Value
  val `15min`, `30min`, `1h`, `3h`, `6h`, `12h` = Value

  final val WINDOW_STATION_STATE_15min = createNamespace("15min")
  final val WINDOW_STATION_STATE_30min = createNamespace("30min")
  final val WINDOW_STATION_STATE_1h = createNamespace("1h")
  final val WINDOW_STATION_STATE_3h = createNamespace("3h")
  final val WINDOW_STATION_STATE_6h = createNamespace("6h")
  final val WINDOW_STATION_STATE_12h = createNamespace("12h")

  final val WINDOW_STATION_TOPIC_15min = createTopicName("15min")
  final val WINDOW_STATION_TOPIC_30min = createTopicName("30min")
  final val WINDOW_STATION_TOPIC_1h = createTopicName("1h")
  final val WINDOW_STATION_TOPIC_3h = createTopicName("3h")
  final val WINDOW_STATION_TOPIC_6h = createTopicName("6h")
  final val WINDOW_STATION_TOPIC_12h = createTopicName("12h")


  def validateInterval(interval: String): Boolean = this.values.map(_.toString).contains(interval)

  def createPath(interval: String): String = {
    assert(validateInterval(interval))
    s"/stations/access/win/$interval"
  }

  def createNamespace(interval: String): String = {
    assert(validateInterval(interval))
    Seq(
      "window-station-state-",
      interval
    ).mkString("-")
  }

  def createTopicName(interval: String): String = {
    assert(validateInterval(interval))
    s"window_$interval"
  }

}
