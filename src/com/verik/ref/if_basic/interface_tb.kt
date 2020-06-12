package com.verik.ref.if_basic

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class Req: Data {
    val addr = UNum(2)
    val data = UNum(8)
}

class ms_if: Interface {
    @In    val clk    = Bool()
    @Logic val sready = Bool()
    @Logic val rstn   = Bool()
    @Logic val txn    = Req()

    inner class master: Port {
        @In  val req    = Req()
        @In  val rstn   = Bool()
        @In  val clk    = Bool()
        @Out val sready = Bool()
    }

    inner class slave: Port {
        @In  val clk    = Bool()
        @In  val sready = Bool()
        @In  val rstn   = Bool()
        @Out val req    = Req()
    }
}

class master: Circuit {
    @Bundle val mif = ms_if().master()

    @Always fun clock() {
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
    @Bundle val sif      = ms_if().slave()
    @Logic  val data     = Vector(4, UNum(8))
    @Logic  val dly      = Bool()
    @Logic  val addr_dly = UNum(2)

    @Always fun delay() {
        on (PosEdge(sif.clk)) {
            if (!sif.rstn) {
                data.forEach {it set 0}
            } else {
                data[sif.req.addr] set sif.req.data
            }
        }

        on (PosEdge(sif.clk)){
            dly set if (sif.rstn) true else sif.sready
            addr_dly set if (sif.rstn) UNum.of(2, 0) else sif.req.addr
        }

        sif.sready set (redNand(sif.req.addr) || !dly)
    }
}

class d_top: Circuit {
    @Bundle val tif = ms_if()

    val m0 = master()
    val s0 = slave()

    @Always fun connect() {
        m0.mif set tif.master()
        s0.sif set tif.slave()
    }
}

class tb: Module {
    @Logic val clk = Bool()
    @Always fun clock() {
        vkDelay(10)
        clk set !clk
    }

    val if0 = ms_if()
    val d0 = d_top()
    @Always fun connect() {
        if0.clk set clk
        d0.tif set if0
    }

    @Initial fun simulate() {
        clk set false
        if0.rstn set false
        repeat (5) {vkWaitOn(PosEdge(clk))}
        if0.rstn set true
        repeat (20) {vkWaitOn(PosEdge(clk))}
        vkFinish()
    }
}
