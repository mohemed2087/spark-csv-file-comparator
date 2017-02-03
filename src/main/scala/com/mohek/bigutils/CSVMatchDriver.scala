package com.mohek.bigutils

import org.apache.spark.{ SparkContext, SparkConf }
import scala.io
import scala.io.Source
import java.io._
import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.hadoop.fs.Path
import scala.collection.mutable.HashMap
import org.apache.spark.rdd.RDD
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.sql.SQLContext
import scala.collection.mutable.Map

object CSVMatchDriver extends Serializable {

  // Determine if running on developer machine
  private val isWindows = sys.props("os.name").toLowerCase match {
    case x if x contains "windows" => true
    case _                         => false
  }

  // Commandline configuration parameters
  case class Config(inPath1: String, inPath2: String, outPath: String)

  val optsParser = new scopt.OptionParser[Config]("csv-file-matcher") {

    head("csv-file-matcher")
    opt[String]('i', "inPath") action { (x, c) => c.copy(inPath1 = x) } text ("inPath is a string")
    opt[String]('a', "inPath") action { (x, c) => c.copy(inPath2 = x) } text ("inPath is a string")
    opt[String]('o', "outPath") action { (x, c) => c.copy(outPath = x) } text ("outPath is a string")
    help("help") text ("prints this usage text")

  }

  def main(args: Array[String]) {

    if (isWindows) {
      // https://mail-archives.apache.org/mod_mbox/spark-dev/201412.mbox/%3CCAPEc=Ju0qp1tgDu8_Spk81b0JjfgzwP6L27RTT-iznz6vq+1BQ@mail.gmail.com%3E
      System.setProperty("hadoop.home.dir", "c:\\opt\\hadoop\\2.6.0\\")
    }

    val defaultConfig = Config("hdfs:///tmp/qawithccjinsight/*.csv", "", "") // "file:///tmp/demo/model.d"

    optsParser.parse(args, defaultConfig) map { config =>
      execute(config) // run application
    }

  }

  def execute(c: Config) {

    val sparkConf = new SparkConf().setAppName("csv-file-matcher").setMaster("local[*]")

    val sc = new SparkContext(sparkConf)
    val conf = Job.getInstance
    val sqlContext = new SQLContext(sc)

    var matchCount: Long = 0
    var nonMatchCount: Long = 0
    var totalRecCount: Long = 0;

    var outputSummary = ""
    //var changeMap: scala.collection.mutable.Map[String, List[StringBuilder]] = Map()

    val masterRdd = sc.textFile(c.inPath1).cache()
    val headerList = masterRdd.first().split(",").toList

    val headDF = sqlContext.read.format("com.databricks.spark.csv").options(Map("header" -> "false")).load(c.inPath1)
    val masterDF = sqlContext.read.format("com.databricks.spark.csv").options(Map("header" -> "true")).load(c.inPath1).cache()
    val scoreOutDF = sqlContext.read.format("com.databricks.spark.csv").options(Map("header" -> "true")).load(c.inPath2).cache()

    val headColumnList = headDF.head().toSeq.toList.drop(2)
    masterDF.registerTempTable("masterTable")
    scoreOutDF.registerTempTable("scoreOutTable")

    headColumnList.foreach { x =>

      val diffColumnCount = (sqlContext.sql("select " + x + " from scoreOutTable").
        except(sqlContext.sql("select " + x + " from masterTable")).count())

      if (diffColumnCount > 0) nonMatchCount = nonMatchCount + diffColumnCount

    }

    totalRecCount = scoreOutDF.count() * headColumnList.size
    matchCount = totalRecCount - nonMatchCount

    val matchinPercent: Float = (matchCount: Float) / totalRecCount

    println("MATCHING COUNT ==> " + matchCount)
    println("NON MATCHING COUNT ==> " + nonMatchCount)
    println("MATCH PERCENTAGE ==> " + matchinPercent * 100 + "%")

    val summaryMap = Map("MATCHING COUNT " -> matchCount, "NON MATCHING COUNT " -> nonMatchCount, "MATCH PERCENTAGE " -> matchinPercent * 100)

    val finalSummary = sc.parallelize(summaryMap.toSeq)

    finalSummary.coalesce(1, true).saveAsTextFile(c.outPath + "/matching")

  }

}
