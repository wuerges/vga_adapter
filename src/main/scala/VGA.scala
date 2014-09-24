package main.scala

import Chisel._

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
  //
  //  when(counter_line.io.count > UInt(300) && counter_line.io.count <= UInt(340)) {
  //    io.r := UInt(0xf, width)
  //    io.g := UInt(0x0, width)
  //    io.b := UInt(0x0, width)
  //  }.elsewhen(counter_line.io.count > UInt(340) && counter_line.io.count <= UInt(440)) {
  //    io.r := UInt(0x0, width)
  //    io.g := UInt(0xf, width)
  //    io.b := UInt(0x0, width)
  //  }.elsewhen(counter_line.io.count > UInt(440) && counter_line.io.count <= UInt(540)) {
  //    io.r := UInt(0x0, width)
  //    io.g := UInt(0x0, width)
  //    io.b := UInt(0xf, width)
  //  }.elsewhen(counter_line.io.count > UInt(540) && counter_line.io.count <= UInt(640)) {
  //    io.r := UInt(0xf, width)
  //    io.g := UInt(0x0, width)
  //    io.b := UInt(0x0, width)
  //  }.elsewhen(counter_line.io.count > UInt(640) && counter_line.io.count <= UInt(740)) {
  //    io.r := UInt(0x0, width)
  //    io.g := UInt(0xf, width)
  //    io.b := UInt(0x0, width)
  //  }.elsewhen(counter_line.io.count > UInt(740) && counter_line.io.count <= UInt(840)) {
  //    io.r := UInt(0x0, width)
  //    io.g := UInt(0x0, width)
  //    io.b := UInt(0xf, width)
  //  }.elsewhen(counter_line.io.count > UInt(840) && counter_line.io.count <= UInt(940)) {
  //    io.r := UInt(0xf, width)
  //    io.g := UInt(0x0, width)
  //    io.b := UInt(0x0, width)
  //  }.elsewhen(counter_line.io.count > UInt(940) && counter_line.io.count <= UInt(1000)) {
  //    io.r := UInt(0x0, width)
  //    io.g := UInt(0xf, width)
  //    io.b := UInt(0x0, width)
  //  }.otherwise {
  //    io.r := UInt(0x0, width)
  //    io.g := UInt(0x0, width)
  //    io.b := UInt(0x0, width)
  //  }

  val visible_horizontal = counter_line.io.count > UInt(240) && counter_line.io.count <= UInt(1040)
  val visible_vertical = counter_frame.io.count > UInt(66) && counter_frame.io.count <= UInt(666)
  val visible = visible_horizontal && visible_vertical
  
  when(visible) {
    io.r := counter_line.io.count(3, 0)
    io.g := counter_frame.io.count(3, 0) + counter_line.io.count(3, 0)   
    io.b := counter_frame.io.count(3, 0)   
  }.otherwise {
    io.r := UInt(0x0, width)
    io.g := UInt(0x0, width)
    io.b := UInt(0x0, width)
  }

}


