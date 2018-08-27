import org.apache.spark.sql.SparkSession

object Analyzer {

  val ss = SparkSession.builder().master("local[*]").getOrCreate()

  def main(args: Array[String]): Unit = {

    // Fetch data


    // Transform to fit algorythm

  }

}
