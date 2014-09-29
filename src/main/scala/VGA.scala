package main.scala

import Chisel._
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO;
import java.io.File

class Counter(n: Int) extends Module {
  val io = new Bundle {
    val en = Bool(INPUT)
    val count = UInt(OUTPUT, log2Up(n))
    val max = Bool(OUTPUT)
  }

  val c = Reg(init = UInt(0))
  val wrap = c === UInt(n - 1)
  val cond = new Bool();

  cond := Bool(true)

  io.count := c
  io.max := wrap

  when(io.en & cond) {
    when(wrap) {
      c := UInt(0, log2Up(n))
    }.otherwise {
      c := c + UInt(1)
    }
  }
}

class SlowCounter(n: Int, n2: Int) extends Counter(n) {
  val internal = Module(new Counter(n2))
  cond := internal.io.max
  internal.io.en := io.en
}

class VGA(width: Int) extends Module {
  val io = new Bundle {
    val r = UInt(OUTPUT, width)
    val g = UInt(OUTPUT, width)
    val b = UInt(OUTPUT, width)
    val h_sync = Bool(OUTPUT)
    val v_sync = Bool(OUTPUT)
  }

  val seg = 50000000
  val frame = seg / 60
  val frame_pix = frame / 800

  val line = frame / 600
  val line_pix = line / 600

  print(seg, frame, line)

  val counter_frame = Module(new SlowCounter(666, 1040))
  val counter_line = Module(new Counter(1040))

  counter_frame.io.en := Bool(true)
  counter_line.io.en := Bool(true)

  io.r := UInt(0x0, width)
  io.g := UInt(0x0, width)
  io.b := UInt(0x0, width)

  //  when(counter_line.io.count > UInt(856) && counter_line.io.count < UInt(976)) {
  when(counter_line.io.count > UInt(56) && counter_line.io.count < UInt(176)) {
    io.h_sync := Bool(false)

  }.otherwise {

    io.h_sync := Bool(true)
  }

  //  when(counter_frame.io.count > UInt(637) && counter_frame.io.count < UInt(643)) {
  when(counter_frame.io.count > UInt(37) && counter_frame.io.count < UInt(43)) {
    io.v_sync := Bool(false)

  }.otherwise {
    io.v_sync := Bool(true)

  }

  val visible_horizontal = counter_line.io.count > UInt(240) && counter_line.io.count <= UInt(1040)
  val visible_vertical = counter_frame.io.count > UInt(66) && counter_frame.io.count <= UInt(666)
  val visible = visible_horizontal && visible_vertical

  def X: UInt = {
    counter_line.io.count - UInt(240)
  }
  def Y: UInt = {
    counter_frame.io.count - UInt(240)
  }
  def Pos: UInt = {
    Y * UInt(800) + X
  }

//  val image = ImageIO.read(new File("src/main/resources/image.jpg"));
//  val w = image.getWidth()
//  val h = image.getHeight()

  val memory = Mem(UInt(width = 24), 800 * 600, false)
//  for (i <- 0 until w) {
//    for (j <- 0 until h) {
//      val pixel = image.getRGB(i, j) & 0xffffff;
//      memory(UInt(i*w+j)) := UInt(pixel)
//      println(i*w+j)
//      
//      //        alpha = (pixel >> 24) & 0xff;
////      val red = (pixel >> 16) & 0xff;
////      val green = (pixel >> 8) & 0xff;
////      val blue = (pixel) & 0xff;
//    }
//  }

  val read = Reg(UInt(0, width))
  read := memory(Pos)
  when(visible) {
    io.r := read(3, 0) 
    io.g := read(7, 4) 
    io.b := read(11, 8) 
//    for (i <- 0 until w) {
//      for (j <- 0 until h) {
//
//        when(X === UInt(i) && Y === UInt(j)) {
//          io.r := UInt(red, width)
//          io.g := UInt(green, width)
//          io.b := UInt(blue, width)
//
//        }
//      }
//    }

    //    val cl_max = log2Up(1040)
    //    val cf_max = log2Up(666)
    //    io.r := counter_line.io.count(cl_max - 1, cl_max - 4)
    //    io.g := counter_frame.io.count(cf_max - 1, cf_max - 4) + counter_line.io.count(cl_max - 1, cl_max - 4)
    //    io.b := counter_frame.io.count(cf_max - 1, cf_max - 4)
  }.otherwise {
    io.r := UInt(0x0, width)
    io.g := UInt(0x0, width)
    io.b := UInt(0x0, width)
  }

}


