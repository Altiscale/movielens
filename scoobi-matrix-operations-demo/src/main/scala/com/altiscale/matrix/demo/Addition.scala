package com.altiscale.matrix.demo

import com.nicta.scoobi.Scoobi._
import com.nicta.scoobi.lib._

object Addition extends ScoobiApp {

  def run = {
    /*
     * example matrices
     */
    val aMat: DMatrix[Int, Int] = DList[((Int, Int), Int)](
      ((1, 1), 1),
      ((1, 2), 1),
      ((2, 1), 1),
      ((2, 2), 1))

    val bMat: DMatrix[Int, Int] = DList[((Int, Int), Int)](
      ((1, 1), 1),
      ((1, 2), 2),
      ((2, 1), 3),
      ((2, 2), 4))

    /*
     * matrix addition function
     */

    def add[T: Numeric](x: T, y: T) = {
      implicitly[Numeric[T]].plus(x, y)
    }

    /*
     * perform addition of aMat and bMat
     */
    val addResult = aMat.byMatrix(bMat, add[Int], Reduction.Sum.int)

    /*
     * store result to hdfs
     */

    persist(addResult.data.toDelimitedTextFile("matrix_addition_result", ",", overwrite = true))

  }

}