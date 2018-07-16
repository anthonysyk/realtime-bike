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

  lazy val ProjectReference = getCanonical("./src/main/resources")

  lazy val StateStoreDirectory = getCanonical("./local_state_data")

}