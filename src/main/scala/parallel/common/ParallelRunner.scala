package parallel.common

trait ParallelRunner {
  def parallel[A,B](t1: => A, t2: => B): (A, B)
}
