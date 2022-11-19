package parallel.parCollections

import parallel.common.{ParallelRunner}
import parallel.common.ParallelismDepth._
import parallel.common.Interf._

class ParArray[T](var arr: Array[T]) extends Sizeable {

  import parallel.common.ForkJoinParallel._
  private implicit val parallelDepth: ParallelismDepth[ParArray[T]] = ParallelismDepth.nCores()

  def size: Int = arr.length

  private def mapSegSeq[B](inp: Array[T], left: Int, right: Int, f: T => B, out: Array[B]): Unit = {
    var i = left
    while (i < right) {
      out(i) = f(inp(i))
      i += 1
    }
  }

  private def mapSegPar[B](inp: Array[T], left: Int, right: Int, f: T => B, out: Array[B])(implicit threshold: Int, parallelRunner: ParallelRunner): Unit = {
    if (right - left < threshold) mapSegSeq(inp,left,right,f,out)
    else {
      val middle = left + ((right - left) / 2)
      parallelRunner.parallel(mapSegPar(inp,left,middle,f,out), mapSegPar(inp,middle,right,f,out))
    }
  }

  private def reduceSegSeq(inp: Array[T], left: Int, right: Int, f: (T,T) => T): T = {
      var acc = inp(left)
      var i = left + 1
      while (i < right) {
        acc = f(acc, inp(i))
        i += 1
      }
      acc
  }

  private def reduceSegPar(inp: Array[T], left: Int, right: Int, f: (T,T) => T)(implicit threshold: Int, parallelRunner: ParallelRunner): T = {
    if (right - left < threshold) reduceSegSeq(inp,left,right,f)
    else {
      val middle = left + ((right - left) / 2)
      val (a, b) = parallelRunner.parallel(reduceSegSeq(inp,left,middle,f), reduceSegSeq(inp,middle,right,f))
      f(a,b)
    }
  }

  def mapSeq[B: Manifest](f: T => B): ParArray[B] = {
    val out = Array.ofDim[B](arr.length)
    mapSegSeq(arr,0, arr.length, f, out)
    new ParArray(out)
  }

  def mapPar[B: Manifest](f: T => B)(implicit pd: ParallelismDepth[ParArray[T]], parallelRunner: ParallelRunner): ParArray[B] = {
    implicit val th = pd(this)
    val out = Array.ofDim[B](arr.length)
    mapSegPar(arr, 0, arr.length, f, out)
    new ParArray(out)
  }

  def reduceSeq(f: (T, T) => T) = {
    reduceSegSeq(arr,0, arr.length,f)
  }

  def reducePar(f: (T, T) => T)(implicit pd: ParallelismDepth[ParArray[T]], parallelRunner: ParallelRunner): T = {
    implicit val th = pd(this)
    reduceSegPar(arr, 0, arr.length, f)
  }

}
