import org.json4s.DefaultFormats
import org.json4s.native.Json

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import java.io._
import scala.io.Source
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Using


object lab_3_19 {
  def main(args: Array[String]): Unit = {
    val url: String = "https://en.wikipedia.org/wiki/List_of_presidents_of_the_United_States";
    val presidentsList = parsePresidents(url)
    writePresidentsToFileAsJSON(presidentsList, "C:\\Projects\\presidents\\")
  }

  def parsePresidents(url: String): List[Map[String, String]] = {
    val content: String = get(url)
    val doc: Document = Jsoup.parse(content);
    val table = doc.select("table.wikitable").asScala
    val tableRows = Jsoup.parse(table.toString()).select("table tbody tr").asScala
    val tableObjects = tableRows.map(item =>
      Map(
        "name" -> item.select("tr td b a").attr("title"),
        "termStarts" -> item.select("tr th ~ td ~ td ~ td > span:first-child").text(),
        "termEnds" -> item.select("tr th ~ td ~ td ~ td > span:last-child").text(),
        "url" -> item.select("tr td b a").attr("href"),
      )
    )
    val cleanedData = tableObjects.filter(_ ("name") != "")
    cleanedData.map(item => item++getPresident(s"https://en.wikipedia.org/${item("url")}")).toList
  }

  def writePresidentsToFileAsJSON(dataToWrite: Iterable[Map[String, String]], pathToFile: String): Unit = {
    for (item <- dataToWrite) {
      Using(new PrintWriter(new File(pathToFile + item("name") + ".json"))) {
        writer => writer.print(Json(DefaultFormats).write(item));
      }
    }
  }

  def getPresident(url: String): Map[String, String] = {
    val content: String = get(url)
    val doc: Document = Jsoup.parse(content);
    val table1: Iterable[Element] = doc.select("table.infobox tbody tr").asScala
    val table2: Iterable[(String, String)] = table1.map(i => (i.select("th").text(), i.select("td").text()))
    val table3: Iterable[(String, String)] = table2.map(i => giveMePartOfThePresident(i._1, i._2))
    val table4: Iterable[(String, String)] = table3.filter(_ != null)
    table4.map(i => i._1 -> i._2).toMap
  }

  def giveMePartOfThePresident(th: String, td: String): (String, String) = {
    if (th == "Born" | th == "Died") {
      val from: Int = td.indexOf("(") + 1
      val to: Int = td.indexOf(")") - 1
      val left = th
      val right = td.slice(from, to)
      (left, right)
    }
    else if (th == "Political party" |
      th == "Education" |
      th == "Occupation" ) {
      val left = th
      val right = td

      (left, right)
    }
    else null
  }

  def get(url: String): String = {
    val source = Source.fromURL(url)
    val string = source.mkString
    source.close()
    string
  }
}
