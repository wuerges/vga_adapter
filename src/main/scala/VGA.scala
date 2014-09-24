package main.scala

import Chisel._

class Counter(n: Int) extends Module {
  val io = new Bundle {
    val en = Bool(INPUT)
    val count = UInt(OUTPUT, log2Up(n))
  }

  val max = UInt(n, log2Up(n))

  val c = Reg(UInt(0, log2Up(n)))

  when (io.en) {
    when (c === max) {
      c := UInt(0)
    }.otherwise {
      c := c + UInt(1)
    }
  }
}

class VGA(width: Int)  extends Module {
  val io = new Bundle {
    val r = UInt(OUTPUT, width)
    val g = UInt(OUTPUT, width)
    val b = UInt(OUTPUT, width)
    val h_sync = Bool(OUTPUT)
    val v_sync = Bool(OUTPUT)
  }

  val seg = 50000000
  val frame = seg / 60
  val line = frame / 600

  print(seg, frame, line)

  val counter_frame = Module(new Counter(frame))
  val counter_line = Module(new Counter(line))

  counter_frame.io.en := Bool(true)
  counter_line.io.en := Bool(true)

  io.r := UInt(0)
  io.g := UInt(0)
  io.b := UInt(0)

  when(counter_line.io.count > UInt(0) && counter_line.io.count < UInt(30)) {
    io.v_sync := Bool(true)
  }.otherwise {
    io.v_sync := Bool(false)
  }

  when(counter_frame.io.count > UInt(0) && counter_frame.io.count < UInt(30)) {
    io.v_sync := Bool(true)
  }.otherwise {
    io.v_sync := Bool(false)
  }
}


