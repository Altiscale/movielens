package com.altiscale.matrix.demo

import com.nicta.scoobi.Scoobi._
import com.nicta.scoobi.lib._

object Multiplication extends ScoobiApp {

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
     *  matrix multiplication function
     */
    def mult[T: Numeric](x: T, y: T) = {
      implicitly[Numeric[T]].times(x, y)
    }

    /*
     * multiply aMat by bMat
     */
    val multResult = aMat.byMatrix(bMat, mult[Int], Reduction.Sum.int)

    /*
     * store result to hdfs
     */

    persist(multResult.data.toDelimitedTextFile("matrix_multiplication_result", ",", overwrite = true))

  }

}