package com.verik.ref.basic_intf

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _req: _struct {
    val addr = _uint(2)
    val data = _uint(8)
}

open class _ms_if: _intf {
    @input val clk = _bool()

    val sready = _bool()
    val rstn   = _bool()
    val req    = _req()

    val master = _master()
    inner class _master: _port {
        @input  val req    = this@_ms_if.req
        @input  val rstn   = this@_ms_if.rstn
        @input  val clk    = this@_ms_if.clk
        @output val sready = this@_ms_if.sready
    }

    val slave = _slave()
    inner class _slave: _port {
        @input  val clk    = this@_ms_if.clk
        @input  val sready = this@_ms_if.sready
        @input  val rstn   = this@_ms_if.rstn
        @output val req    = this@_ms_if.req
    }
}

class _master: _circuit {
    @port val master = _ms_if().master

    @seq fun clock() {
        on (posedge(master.clk)) {
            if (!master.rstn) {
                master.req put {
                    it.addr put 0
                    it.data put 0
                }
            } else {
                if (master.sready) {
                    master.req put {
                        it.addr put it.addr + 1
                        it.data put it.data * 4
                    }
                }
            }
        }
    }
}

class _slave: _circuit {
    @input  val req    = _req()
    @input  val rstn   = _bool()
    @output val sready = _bool()
    @port   val slave  = _ms_if().slave

    val data     = _vector(4, _uint(8))
    val dly      = _bool()
    val addr_dly = _uint(2)

    @seq fun put_data() {
        on(posedge(slave.clk)) {
            if (!slave.rstn) {
                data for_each { it put 0 }
            } else {
                data[slave.req.addr] put slave.req.data
            }
        }
    }

    @seq fun put_dly() {
        on(posedge(slave.clk)) {
            dly put if (slave.rstn) true else slave.sready
            addr_dly put if (slave.rstn) _uint.of(2, 0) else slave.req.addr
        }
    }

    @comb fun set_sready() {
        slave.sready set (red_nand(slave.req.addr) || !dly)
    }
}

class _top: _circuit {
    @intf val ms_if = _ms_if()

    @def val master = _master() con {
        it.master con ms_if.master
    }

    @def val slave = _slave() con { it ->
        it.req con {
            it.addr con null
            it.data con null
        }
        it.rstn   con null
        it.sready con null
        it.slave  con ms_if.slave
    }
}

@main class _tb: _module {
    val clk = _bool()
    @initial fun clock() {
        clk set false
        forever {
            vk_delay(10)
            clk set !clk
        }
    }

    @def val ms_if = _ms_if() con {clk}

    @def val top = _top() con {ms_if}

    @initial fun simulate() {
        ms_if.rstn set false
        repeat (5) {vk_wait_on(posedge(clk))}
        ms_if.rstn set true
        repeat (20) {vk_wait_on(posedge(clk))}
        vk_finish()
    }
}
