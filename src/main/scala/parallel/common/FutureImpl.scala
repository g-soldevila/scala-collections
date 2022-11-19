package parallel.common

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object FutureParallel extends ParallelRunner {

  implicit val parallelRunner = this

  import ExecutionContext.Implicits.global

  def parallel[A,B](t1: => A, t2: => B): (A, B) = {

    val f1 = Future { t1 }
    val f2 = Future { t2 }

    val f3 = for {
      a <- f1
      b <- f2
    } yield (a, b)

    Await.result(f3, Duration.Inf)

  }
}
