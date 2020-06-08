package com.verik.ref.if_basic

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class ms_if: Interface {
    @In  var clk    = Bool()
    @Reg var sready = Bool()
    @Reg var rstn   = Bool()
    @Reg var addr   = Unsigned(2)
    @Reg var data   = Unsigned(8)

    class master: Port {
        @In  var addr   = Unsigned(2)
        @In  var data   = Unsigned(8)
        @In  var rstn   = Bool()
        @In  var clk    = Bool()
        @Out var sready = Bool()
    }

    class slave: Port {
        @In  var clk    = Bool()
        @In  var sready = Bool()
        @In  var rstn   = Bool()
        @Out var addr   = Unsigned(2)
        @Out var data   = Unsigned(8)
    }
}

class master: Circuit {
    @InOut var mif = ms_if.master()

    @Always fun clock() {
        on (PosEdge(mif.clk)) {
            if (!mif.rstn) {
                mif.addr set Bit("0")
                mif.data set Bit("0")
            } else {
                if (mif.sready) {
                    mif.addr set mif.addr + Unsigned("1")
                    mif.data set mif.data * Unsigned("4")
                }
            }
        }
    }
}

class slave: Circuit {
    @InOut var sif = ms_if.slave()

    @Reg var reg      = Unsigned(8) array "4"
    @Reg var dly      = Bool()
    @Reg var addr_dly = Unsigned(2)

    @Always fun delay() {
        on (PosEdge(sif.clk)) {
            if (!sif.rstn) {
                reg set (Bit("0") array "4")
            } else {
                reg[0] set sif.data // FIXME
            }
        }
    }
}
