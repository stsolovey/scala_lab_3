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


object third_lab {}


package object myGlobalFunctions {


 /* def get(url: String): String = {
    val source = Source.fromURL(url)
    val string = source.mkString
    source.close()
    string
  }*/

  /*def writeToFile(pathToWrite: String, contentToWrite: String): Unit = {
    Using(new PrintWriter(new File(pathToWrite))) {
      writer => contentToWrite.foreach(writer.print)
    }
  }*/

  /*def writeToFiles(map: Map[String, String], docName: String): Unit = {
    val m = Json(DefaultFormats).write(map)

    Using(new PrintWriter(new File(docName + "president\\" + map("name") + ".txt"))) {
      writer => writer.print(m);
    }
  }*/



  /*def writeToFiles(map: Map[String, String], pathToFile: String): Unit = {
    val m = Json(DefaultFormats).write(map)

    Using(new PrintWriter(new File(pathToFile + map("name") + ".txt"))) {
      writer => writer.print(m);
    }
  }*/
}

/*object writeToFileOnePresident extends App {
  val url: String = "https://en.wikipedia.org/wiki/George_Washington"
  val content = get(url)
  val filePath = "C:\\Projects\\folderForFile\\"
  val fileName = "george_wash.txt"
  val filePathName = filePath + fileName
  writeToFile(filePathName, content)
}*/

/*object test extends App {
  val url = "https://en.wikipedia.org/wiki/George_Washington"
  val p = new parseFromUrlOnePresident(url)
  println(p.map)
}*/

/*class getPresidentByUrl(val url: String) {
  val content: String = get(url)
  val doc: Document = Jsoup.parse(content);
  val table1: Iterable[Element] = doc.select("table.infobox tbody tr").asScala
  val table2: Iterable[(String, String)] = table1.map(i => (i.select("th").text(), i.select("td").text()))
  val table3: Iterable[(String, String)] = table2.map(i => giveMePartOfThePresident(i._1, i._2))
  val table4: Iterable[(String, String)] = table3.filter(_ != null)
  val map: Map[String, String] = table4.map(i => i._1 -> i._2).toMap
}*/


/*object writeToFileAllPresidents extends App {

  val url: String = "https://en.wikipedia.org/wiki/List_of_presidents_of_the_United_States";

  val filePath = "C:\\Projects\\folderForFile\\";
  val fileName = "List_of_presidents_of_the_United_States.txt";

  val content = get(url);
  Using(new PrintWriter(new File(filePath + fileName))) {
    writer => content.foreach(writer.print)
  }
}*/

/*object parsePresidents extends App {

  val url: String = "https://en.wikipedia.org/wiki/List_of_presidents_of_the_United_States";
  val content: String = get(url)
  val doc: Document = Jsoup.parse(content);
  val table = doc.select("table.wikitable").asScala
  val tableRows = Jsoup.parse(table.toString()).select("table tbody tr").asScala
  val tableObjects = tableRows.map(item =>
    Map(
      "name" -> item.select("tr td b a").attr("title"), // Имя "portrait" -> item.select("tr td a img").attr("src"), // портрет
      //"birthDateDeathDate" -> item.select("tr td b ~ span").text(), // Годы жизни
      "termStarts" -> item.select("tr th ~ td ~ td ~ td > span:first-child").text(), // начало срока
      "termEnds" -> item.select("tr th ~ td ~ td ~ td > span:last-child").text(), // конец срока срока
      "url" -> item.select("tr td b a").attr("href"), // ссылка в вики
    )
  )
  val cleanedData = tableObjects.filter(_ ("name") != "")
  for (row <- cleanedData) println(row)

  for (row <- cleanedData) {
    val url = s"https://en.wikipedia.org/${row("url")}"
    val fromPresidentPage = new getPresidentByUrl(url)
    val map: Map[String, String] = row ++ fromPresidentPage.map
    writeToFiles(map, "C:\\Projects\\presidents\\")
    println(url)
    println(map.mkString(" "))
  }


}*/





/*object parseFromFileOnePresident extends App {
  val filePath = "C:\\Projects\\folderForFile\\";
  val fileName = "george_wash.txt";
  val file = Source.fromFile(filePath + fileName);
  val lines = file.getLines.toList;
  val doc = Jsoup.parse(lines.mkString(" "));
  val table1 = doc.select("table.infobox.vcard tbody tr").asScala
  val table2 = table1.map(i=>(i.select("th").text(), i.select("td").text()))
  val table3 = table2.map(i => fnc(i._1,i._2 ))
  val table4 = table3.filter(_ != null)
  val map = table4.map(i => Map(i._1 -> i._2))

  map.foreach(println)

  def fnc(th: String, td: String): (String, String) = {
    if (th == "Born" | th == "Died")   {
      val from: Int = td.indexOf("(")+1
      val to: Int = td.indexOf(")")-1
      val left = th
      val right = td.slice(from, to)
      (left,right)
    }
    else if ( th == "Political party" |
      th == "Education" |
      th == "Occupation" |
      th == "Political party" |
      th == "Political party") {
      val left = th
      val right = td
      //println(th, td);
      (left,right)
    }
    else null
  }

}

object funny extends App {
  val pfuop = new parseFromUrlOnePresident("https://en.wikipedia.org/wiki/George_Washington")
  println(pfuop.map.mkString("\n"))

  println(Map("hello" -> "world") ++ Map("hello2" -> "world2"))
}
object jsoupObj extends App {
  val doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_presidents_of_the_United_States").get()
  println(doc.title())
}

*/