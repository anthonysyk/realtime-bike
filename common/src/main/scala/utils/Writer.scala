package utils

import java.io.{BufferedWriter, File, FileWriter}

object Writer {

  def write(canonicalFilename: String, text: String) = {
    val file = new File(canonicalFilename)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(text)
    bw.close()
  }

}

object PathHelper {

  private def getCanonical(pathname: String) = new java.io.File(pathname).getCanonicalPath

  lazy val CollectorReference = getCanonical("./src/main/resources")

  lazy val ProcessorReferenceTest = getCanonical("./processor/src/test/resources")

  lazy val CommonReference = getCanonical("./src/main/resources")

  lazy val StateStoreDirectory = getCanonical("./local_state_data")

  def main(args: Array[String]): Unit = {
    println(getCanonical("."))
  }

}