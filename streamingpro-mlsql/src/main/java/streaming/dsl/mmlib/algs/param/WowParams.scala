package streaming.dsl.mmlib.algs.param

import org.apache.spark.ml.param.{ParamMap, Params}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Created by allwefantasy on 20/9/2018.
  */
trait WowParams extends Params {
  override def copy(extra: ParamMap): Params = defaultCopy(extra)

  def _explainParams(sparkSession: SparkSession, f: () => Params) = {
    val model = f()
    val rfcParams2 = this.params.map(this.explainParam).map(f => Row.fromSeq(f.split(":", 2)))
    val rfcParams = model.params.map(model.explainParam).map { f =>
      val Array(name, value) = f.split(":", 2)
      Row.fromSeq(Seq("fitParam.[group]." + name, value))
    }
    sparkSession.createDataFrame(sparkSession.sparkContext.parallelize(rfcParams2 ++ rfcParams, 1), StructType(Seq(StructField("param", StringType), StructField("description", StringType))))
  }
}
