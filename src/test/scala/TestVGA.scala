package test.scala

/**
 * @author ew
 *
 */

import main.scala._
import Chisel._

class SimpleTest(c: VGA) extends Tester(c) {
}

object TestVGA {
  def main(args: Array[String]): Unit = {
    chiselMainTest(args, () => Module(new VGA(4))) {
      c => new SimpleTest(c)
    }
  }
}
