package parallel.common

import org.scalameter._
import parallel.common.Interf._
import parallel.common.ParallelismDepth.ParallelismDepth

class TestRunner[T <: Sizeable](seqOp: T => Any) {

  private val standardConfig: MeasureBuilder[Unit, Double] = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 80,
    Key.verbose -> false
  ) withWarmer(new Warmer.Default)

  private def runTest(t: (T, ParallelismDepth[T]) => Any, col: T, parallelismDepth: ParallelismDepth[T]): Quantity[Double] = {
    val time = standardConfig measure {
      t(col, parallelismDepth)
    }
    println(s"parallel count time: $time")
    time
  }

  def runallTests(t: (T, ParallelismDepth[T]) => Any, cols: Seq[T], pds: Seq[ParallelismDepth[T]]): Unit = {
    for (pd <- pds) {
      println(s"Using parallelismDepth ${pd.toString}")
      println("=========================================")
      for (col <- cols) {
        val seqtime = standardConfig measure { seqOp(col) }
        println("----------------------------------------")
        println(s"Running for array of size: ${col.size} ")
        println(s"sequential count time: $seqtime")
        println("----------------------------------------")
        val parTime = runTest(t, col, pd)
        println(s"speedup: ${seqtime.value / parTime.value}")
        println("----------------------------------------")
      }
    }
  }

}
