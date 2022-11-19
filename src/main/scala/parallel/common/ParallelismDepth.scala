package parallel.common

import parallel.common.Interf._

object ParallelismDepth {

  trait ParallelismDepth[T <: Sizeable] {
    def toString: String
    def apply(col: T): Int
  }

  case class FixedThreshold[T <: Sizeable](n: Int) extends ParallelismDepth[T] {
    override def toString: String = s"FixedThreshold($n)"
    override def apply(col: T): Int = n
  }

  case class NPartitionsThreshold[T <: Sizeable]( n: Int) extends ParallelismDepth[T] {
    override def toString: String = s"NPartitionsThreshold($n)"
    override def apply(col: T): Int = if (col.size > n) col.size / n else 1
  }

  case class NCoresThreshold[T <: Sizeable]() extends ParallelismDepth[T] {
    val cores = Runtime.getRuntime.availableProcessors
    override def toString: String = s"NCoresThreshold(cores: $cores, partitions: $cores)"
    override def apply(col: T): Int =  if (col.size > cores) col.size / cores else 1
  }

  object ParallelismDepth {

    def fixed[A <: Sizeable](n: Int): ParallelismDepth[A] = FixedThreshold[A](n)
    def partitions[A <: Sizeable](n: Int): ParallelismDepth[A] = NPartitionsThreshold[A](n)
    def partitionsExp[A <: Sizeable](n: Int): ParallelismDepth[A] = partitions[A](scala.math.pow(2,n).toInt)
    def nCores[A <: Sizeable](): ParallelismDepth[A] = NCoresThreshold[A]()

  }

}
