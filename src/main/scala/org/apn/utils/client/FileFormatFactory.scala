package org.apn.utils.client

import java.io.PrintWriter
import scala.collection.JavaConversions._
import scala.io.Source
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import org.json.JSONObject

object FileFormatFactory extends App {
  assert(args.length > 4, "Please pass the arguments in order as 'java -jar file-format-factory-{version}.jar inFile inType outFile outType delimiter[OPTIONAL]'")

  val inFile = args(0)
  val inType = args(1)
  val outFile = args(2)
  val outType = args(3)
  val delim = args(4)
  val fff = new FileFormatFactory(inFile, outFile)

  inType match {
    case "dsv" => fff.dsvToJson(delim)
    case "json" => fff.jsonToDsv(delim)
    case _ => ""
  }
  println(s"Output File = $outFile.")
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