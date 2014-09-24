package VGA

import Chisel._

class VGA(width: Int)  extends Module {
  val io = new Bundle {
    val r = new UInt(OUTPUT, width)
    val g = new UInt(OUTPUT, width)
    val b = new UInt(OUTPUT, width)
    val h_sync = new Bool(OUTPUT)
    val v_sync = new Bool(OUTPUT)
  }

  val seg = 50000000
  val frame = seg / 60
  val line = seg / 600

  val counter_frame = Counter(frame)
  val counter_line = Counter(frame)

  counter_frame.io.en := true
  counter_line.io.en := true

  r := UInt(0)
  g := UInt(0)
  b := UInt(0)

  when(counter_line > 0 && counter_line < 30) {
    v_sync := Bool(true)
  }.otherwise {
    v_sync := Bool(false)
  }

  when(counter_frame > 0 && counter_frame < 30) {
    v_sync := Bool(true)
  }.otherwise {
    v_sync := Bool(false)
  }
}


