package coinyser

import org.apache.spark.sql.functions.{explode, from_json}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Dataset, SparkSession}

object BatchProducer {

  def jsonToHttpTransactions(json: String) (implicit spark: SparkSession): Dataset[HttpTransaction] = {
    // Importing Spark implicits to use it's functions
    import spark.implicits._

    // Creating a dataset from JSON response
    val ds: Dataset[String] = Seq(json).toDS()
    val txSchema: StructType = Seq.empty[HttpTransaction].toDS().schema
    val schema = ArrayType(txSchema)
    val arrayColumn = from_json($"value".cast(StringType), schema)

    ds.select(explode(arrayColumn).alias("v"))
      .select("v.*")
      .as[HttpTransaction]
  }

  // Creating a method to store all the transactions with their timestamps
  def httpToDomainTransactions(ds: Dataset[HttpTransaction]): Dataset[Transaction] = {
    import ds.sparkSession.implicits._
    ds.select(
      $"date".cast(LongType).cast(TimestampType).as("timestamp"),
      $"date".cast(LongType).cast(TimestampType).
        cast(DateType).as("date"),
      $"tid".cast(IntegerType),
      $"price".cast(DoubleType),
      $"type".cast(BooleanType).as("sell"),
      $"amount".cast(DoubleType))
      .as[Transaction]
  }
}
