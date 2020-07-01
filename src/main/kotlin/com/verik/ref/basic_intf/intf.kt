package com.verik.ref.basic_intf

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class _req: _struct {
    val addr = _uint(2)
    val data = _uint(8)
}

class _ms_if: _intf {
    @input val clk = _bool()

    val sready = _bool()
    val rstn   = _bool()
    val req    = _req()

    val master = _master(this)
    class _master(it: _ms_if): _port {
        @input  val req    = it.req
        @input  val rstn   = it.rstn
        @input  val clk    = it.clk
        @output val sready = it.sready
    }

    val slave = _slave(this)
    class _slave(it: _ms_if): _port {
        @input  val clk    = it.clk
        @input  val sready = it.sready
        @input  val rstn   = it.rstn
        @output val req    = it.req
    }
}

class _master: _circuit {
    @port val master = _ms_if().master

    @seq fun clock() {
        on (posedge(master.clk)) {
            if (!master.rstn) {
                master.req.addr put 0
                master.req.data put 0
            } else {
                if (master.sready) {
                    master.req.addr put_add 1
                    master.req.data put_mul 4
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

    val data     = _array(4, _uint(8))
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
            addr_dly put if (slave.rstn) uint(0b00) else slave.req.addr
        }
    }

    @comb fun set_sready() {
        slave.sready set (red_nand(slave.req.addr) || !dly)
    }
}

class _top: _circuit {
    @intf val ms_if = _ms_if()

    @comp val master = _master() with {
        it.master con ms_if.master
    }

    @comp val slave = _slave() with {
        it.req    con null
        it.rstn   con null
        it.sready con null
        it.slave  con ms_if.slave
    }
}

@top class _tb: _module {
    val clk = _bool()
    @initial fun clock() {
        clk set false
        forever {
            vk_wait(10)
            clk set !clk
        }
    }

    @comp val ms_if = _ms_if() with { clk }

    @comp val top = _top() with { ms_if }

    @initial fun simulate() {
        ms_if.rstn set false
        vk_wait_on(posedge(clk), 5)
        ms_if.rstn set true
        vk_wait_on(posedge(clk), 20)
        vk_finish()
    }
}
