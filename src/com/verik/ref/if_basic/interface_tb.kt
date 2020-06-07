package com.verik.ref.if_basic

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@Interface class ms_if: Component {
    @In   var clk    = Bool()
    @Wire var sready = Bool()
    @Wire var rstn   = Bool()
    @Wire var addr   = Bit(2)
    @Wire var data   = Bit(8)

    var slave = object: Port {
        @In  var addr   = Bit(2)
        @In  var data   = Bit(8)
        @In  var rstn   = Bool()
        @In  var clk    = Bool()
        @Out var sready = Bool()
    }

    var master = object: Port {
        @In  var clk    = Bool()
        @In  var sready = Bool()
        @In  var rstn   = Bool()
        @Out var addr   = Bit(2)
        @Out var data   = Bit(8)
    }
}
