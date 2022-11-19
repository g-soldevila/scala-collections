package parallel.parCollections

import parallel.common.FutureParallel.parallel

import scala.reflect.ClassTag

object ParTree {

  sealed abstract class Tree[A] {
    val size: Int
    def map[B: ClassTag](f: A => B): Tree[B]
    def reduce(f: (A, A) => A): A
    def toList: List[A]
  }

  case class Leaf[A](arr: Array[A]) extends Tree[A] {
    override val size: Int = arr.length
    override def map[B: ClassTag](f: A => B): Tree[B] = Leaf(arr.map(f))
    override def reduce(f: (A, A) => A): A = arr.reduce(f)
    override def toList: List[A] = arr.toList
  }

  case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A] {
    override val size: Int= left.size + right.size
    override def map[B: ClassTag](f: A => B): Tree[B] = {
      val (nl, nr) = parallel(left.map(f), right.map(f))
      Node(nl, nr)
    }
    override def reduce(f: (A, A) => A): A = {
      val (a, b) = parallel(left.reduce(f), right.reduce(f))
      f(a,b)
    }
    override def toList: List[A] = {
      val (l, r) = parallel(left.toList, right.toList)
      l ++ r
    }
  }

  implicit class ArrayToParTree[T: ClassTag](val a: Array[T]) {
    def toParTree(threshold: Int): Tree[T] = {

      def splitArrPar(a: Array[T]): Tree[T] = {
        if (a.length < threshold) Leaf(a)
        else {
          val (leftArr, rightArr) = a.splitAt(a.length / 2)
          val (leftTree, rightTree) = parallel(splitArrPar(leftArr), splitArrPar(rightArr))
          Node(leftTree, rightTree)
        }
      }

      splitArrPar(a)
    }
  }

}
