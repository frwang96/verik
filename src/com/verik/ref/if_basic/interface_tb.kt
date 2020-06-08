package com.verik.ref.if_basic

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class ms_if: Interface {
    @In  var clk    = Bool()
    @Reg var sready = Bool()
    @Reg var rstn   = Bool()
    @Reg var addr   = UNum(2)
    @Reg var data   = UNum(8)

    inner class master: Port {
        @In  var addr   = UNum(2)
        @In  var data   = UNum(8)
        @In  var rstn   = Bool()
        @In  var clk    = Bool()
        @Out var sready = Bool()
    }

    inner class slave: Port {
        @In  var clk    = Bool()
        @In  var sready = Bool()
        @In  var rstn   = Bool()
        @Out var addr   = UNum(2)
        @Out var data   = UNum(8)
    }
}

class master: Circuit {
    @Bundle var mif = ms_if().master()

    @Always fun clock() {
        on (PosEdge(mif.clk)) {
            if (!mif.rstn) {
                mif.addr set Bit("0")
                mif.data set Bit("0")
            } else {
                if (mif.sready) {
                    mif.addr set mif.addr + UNum("1")
                    mif.data set mif.data * UNum("4")
                }
            }
        }
    }
}

class slave: Circuit {
    @Bundle var sif = ms_if().slave()

    @Reg var reg      = UNum(8) array "4"
    @Reg var dly      = Bool()
    @Reg var addr_dly = UNum(2)

    @Always fun delay() {
        on (PosEdge(sif.clk)) {
            if (!sif.rstn) {
                reg set (Bit("0") array "4")
            } else {
                reg[sif.addr] set sif.data
            }
        }

        on (PosEdge(sif.clk)){
            dly set if (sif.rstn) true else sif.sready
            addr_dly set if (sif.rstn) UNum("0") else sif.addr
        }

        sif.sready con (!(sif.addr[1] and sif.addr[0]) || !dly)
    }
}

class d_top: Circuit {
    @Bundle var tif = ms_if()

    var m0 = master()
    var s0 = slave()

    @Always fun connect() {
        m0.mif con tif.master()
        s0.sif con tif.slave()
    }
}

class tb: Module {
    @Reg var clk = Bool()
    @Always fun clock() {
        vkDelay(10)
        clk = !clk
    }

    var if0 = ms_if()
    var d0 = d_top()
    @Always fun connect() {
        if0.clk con clk
        d0.tif con if0
    }

    @Initial fun simulate() {
        clk = false
        if0.rstn = false
        repeat (5) {vkWaitOn(PosEdge(clk))}
        if0.rstn = true
        repeat (20) {vkWaitOn(PosEdge(clk))}
        vkFinish()
    }
}
