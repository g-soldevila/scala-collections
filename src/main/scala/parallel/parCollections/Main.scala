package parallel.parCollections

import parallel.common.ParallelismDepth._
import parallel.parCollections.testRunners.ParArray._

object Main {

  def main(args: Array[String]): Unit = {

    val cols = Seq(
      new ParArray[Int](Array.fill(1000)(scala.util.Random.nextInt(100))),
      new ParArray[Int](Array.fill(10000)(scala.util.Random.nextInt(100))),
      new ParArray[Int](Array.fill(100000)(scala.util.Random.nextInt(100))),
      new ParArray[Int](Array.fill(1000000)(scala.util.Random.nextInt(100))),
      new ParArray[Int](Array.fill(10000000)(scala.util.Random.nextInt(100))),
      new ParArray[Int](Array.fill(100000000)(scala.util.Random.nextInt(100)))
    )

    val pds = Seq[ParallelismDepth[ParArray[Int]]](
      ParallelismDepth.partitionsExp[ParArray[Int]](8),
      ParallelismDepth.nCores()
    )

    def fMap(el: Int): Int = el + 2

    testParArrayMap[Int, Int](cols, pds, fMap)

  }
}
