package parallel.parCollections.testRunners

import parallel.common.ParallelismDepth.ParallelismDepth
import parallel.common.TestRunner
import parallel.parCollections.ParArray
import parallel.common.ForkJoinParallel._

object ParArray {

  def testParArrayMap[A, B: Manifest](
      cols: Seq[ParArray[A]],
      pds: Seq[ParallelismDepth[ParArray[A]]],
      f: A => B
  ): Unit = {

    def seqOp(arr: ParArray[A]) = arr.mapSeq(f)
    def parOp(arr: ParArray[A], pd: ParallelismDepth[ParArray[A]]) = {
      implicit val parallelismDepth: ParallelismDepth[ParArray[A]] = pd
      arr.mapPar(f)
    }

    val tr = new TestRunner[ParArray[A]](seqOp)

    tr.runallTests(parOp, cols, pds)

  }

  def testParArrayReduce[T](
      cols: Seq[ParArray[T]],
      pds: Seq[ParallelismDepth[ParArray[T]]],
      f: (T, T) => T
  ): Unit = {

    def seqOp(arr: ParArray[T]): Any = arr.reduceSeq(f)
    def parOp(arr: ParArray[T], pd: ParallelismDepth[ParArray[T]]) = {
      implicit val parallelismDepth = pd
      arr.reducePar(f)
    }

    val tr = new TestRunner[ParArray[T]](seqOp)

    tr.runallTests(
      parOp,
      cols,
      pds
    )
  }

}
