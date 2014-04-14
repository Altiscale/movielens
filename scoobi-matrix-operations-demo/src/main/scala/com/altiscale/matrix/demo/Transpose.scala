package com.altiscale.matrix.demo

import com.nicta.scoobi.Scoobi._
import com.nicta.scoobi.lib._

object Transpose extends ScoobiApp {

  def run = {
    /*
     * example matrices
     */
    val aMat: DMatrix[Int, Int] = DList[((Int, Int), Int)](
      ((1, 1), 1),
      ((1, 2), 1),
      ((2, 1), 1),
      ((2, 2), 1))

    /*
     * transpose of aMat
     */
    val aMatTranspose = aMat.transpose

    /*
     * store result to hdfs
     */
    persist(aMatTranspose.data.toDelimitedTextFile("matrix_transpose_result", ",", overwrite = true))

  }

}