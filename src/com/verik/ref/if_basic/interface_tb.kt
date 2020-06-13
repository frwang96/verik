package com.verik.ref.if_basic

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class Req: Data {
    val addr = UNum(2)
    val data = UNum(8)
}

open class ms_if: Interface {
    @In val clk = Bool()

    val sready = Bool()
    val rstn   = Bool()
    val req    = Req()

    inner class master: Interport {
        private val x = this@ms_if
        @In  val req    = x.req
        @In  val rstn   = x.rstn
        @In  val clk    = x.clk
        @Out val sready = x.sready
    }

    inner class slave: Interport {
        private val x = this@ms_if
        @In  val clk    = x.clk
        @In  val sready = x.sready
        @In  val rstn   = x.rstn
        @Out val req    = x.req
    }
}

class master: Circuit {
    @Port val mif = ms_if().master()

    @Seq fun clock() {
        on (PosEdge(mif.clk)) {
            if (!mif.rstn) {
                mif.req.addr set 0
                mif.req.data set 0
            } else {
                if (mif.sready) {
                    mif.req.addr set mif.req.addr + 1
                    mif.req.data set mif.req.data * 4
                }
            }
        }
    }
}

class slave: Circuit {
    @In   val req    = Req()
    @In   val rstn   = Bool()
    @Out  val sready = Bool()
    @Port val sif    = ms_if().slave()

    val data     = Vector(4, UNum(8))
    val dly      = Bool()
    val addr_dly = UNum(2)

    @Seq fun set_data() {
        on(PosEdge(sif.clk)) {
            if (!sif.rstn) {
                data.forEach { it set 0 }
            } else {
                data[sif.req.addr] set sif.req.data
            }
        }
    }

    @Seq fun set_dly() {
        on(PosEdge(sif.clk)) {
            dly set if (sif.rstn) true else sif.sready
            addr_dly set if (sif.rstn) UNum.of(2, 0) else sif.req.addr
        }
    }

    @Comb fun set_sready() {
        sif.sready set (redNand(sif.req.addr) || !dly)
    }
}

class d_top: Circuit {
    @Port val tif = ms_if()

    val m0 = master()
    @Connect fun m0() {
        m0.mif con tif.master()
    }

    val s0 = slave()
    @Connect fun s0() {
        s0.sif con tif.slave()
    }
}

@Main class tb: Module {
    val clk = Bool()
    @Initial fun clock() {
        clk set false
        forever {
            vkDelay(10)
            clk set !clk
        }
    }

    val if0 = ms_if()
    @Connect fun if0() {
        if0.clk con clk
    }

    val d0 = d_top()
    @Connect fun d0() {
        d0.tif con if0
    }

    @Initial fun simulate() {
        if0.rstn set false
        repeat (5) {vkWaitOn(PosEdge(clk))}
        if0.rstn set true
        repeat (20) {vkWaitOn(PosEdge(clk))}
        vkFinish()
    }
}
