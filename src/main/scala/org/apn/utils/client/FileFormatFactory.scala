package org.apn.utils.client

import java.io.PrintWriter
import scala.collection.JavaConversions._
import scala.io.Source
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import org.json.JSONObject

object FileType {
  final val Json = "json"
  final val Dsv = "dsv"
}

object FileFormatFactory extends App {
  formatFile(args)

  def formatFile(arguments: Array[String]) = {
    require(arguments.length > 3, "Please pass the arguments in order as 'java -jar file-format-factory-{version}.jar inFile inType outFile outType delimiter[OPTIONAL]'")

    val inFile = arguments(0)
    val inType = arguments(1)
    val outFile = arguments(2)
    val outType = arguments(3)

    if (FileType.Dsv.equalsIgnoreCase(inType) || FileType.Dsv.equalsIgnoreCase(outType))
      require(arguments.length > 4, "Please pass the arguments in order as 'java -jar file-format-factory-{version}.jar inFile inType outFile outType delimiter'" +
        "\n Delimiter is required as either 'input type' or 'output type' is " + FileType.Dsv + ".")
    val delim = arguments(4)

    val fff = new FileFormatFactory(inFile, outFile)

    inType match {
      case FileType.Dsv => fff.dsvToJson(delim)
      case FileType.Json => fff.jsonToDsv(delim)
      case _ => ""
    }
    println(s"Please find the Output File $outFile.")
  }
}

class FileFormatFactory(val inFile: String, val outFile: String) {
  require(inFile.nonEmpty && outFile.nonEmpty)

  def dsvToJson(delim: String) {
    new PrintWriter(outFile) { write(getJsonFromDsv(delim).mkString("\n")); close }
  }

  def jsonToDsv(delim: String) {
    new PrintWriter(outFile) { write(getDsvFromJson(delim)); close }
  }

  private def getDsvFromJson(delim: String) = {
    val lines: Iterator[JsValue] = Source.fromFile(inFile).getLines.map(Json.parse(_))
    val jsonArr = lines.map(x => { new JSONObject(x.toString) })
    val header: String = if (jsonArr.hasMoreElements()) jsonArr.nextElement().keySet().mkString(delim) else ""
    header + "\n" + jsonArr.map(_.toMap().valuesIterator.mkString(delim)).mkString("\n")
  }

  private def getJsonFromDsv(delim: String) = {
    val file = Source.fromFile(inFile)

    //split the columns from delimited lines
    val linesArrStr = file.getLines().map(_.split(delim))

    // remove the header and keep it for later use
    val header = linesArrStr.next()

    // Iterate the values and create json with header as keys
    linesArrStr.map(arr => {
      var i = -1
      val map = arr.map(str => {
        i += 1
        (header(i) -> str)
      }).toMap

      import play.api.libs.json._
      Json.toJson(map)
    })
  }
}