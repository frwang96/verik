package com.verik.ref.if_basic

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
        @input  val req    = _req()
        @input  val rstn   = _bool()
        @input  val clk    = _bool()
        @output val sready = _bool()
    }

    val slave = _slave()
    inner class _slave: _port {
        @input  val clk    = _bool()
        @input  val sready = _bool()
        @input  val rstn   = _bool()
        @output val req    = _req()
    }
}

class _master: _circuit {
    @port val master = _ms_if().master

    @seq fun clock() {
        on (posedge(master.clk)) {
            if (!master.rstn) {
                master.req.addr set 0
                master.req.data set 0
            } else {
                if (master.sready) {
                    master.req.addr set master.req.addr + 1
                    master.req.data set master.req.data * 4
                }
            }
        }
    }
}

class _slave: _circuit {
    @input  val req    = _req()
    @input  val rstn   = _bool()
    @output val sready = _bool()
    @port   val slave    = _ms_if().slave

    val data     = _vector(4, _uint(8))
    val dly      = _bool()
    val addr_dly = _uint(2)

    @seq fun set_data() {
        on(posedge(slave.clk)) {
            if (!slave.rstn) {
                data for_each { it set 0 }
            } else {
                data[slave.req.addr] set slave.req.data
            }
        }
    }

    @seq fun set_dly() {
        on(posedge(slave.clk)) {
            dly set if (slave.rstn) true else slave.sready
            addr_dly set if (slave.rstn) _uint.of(2, 0) else slave.req.addr
        }
    }

    @comb fun set_sready() {
        slave.sready set (red_nand(slave.req.addr) || !dly)
    }
}

class _top: _circuit {
    @intf val ms_if = _ms_if()

    val master = _master()
    @connect fun m0() {
        master.master con ms_if._master()
    }

    val slave = _slave()
    @connect fun s0() {
        slave.slave con ms_if._slave()
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

    val ms_if = _ms_if()
    @connect fun if0() {
        ms_if.clk con clk
    }

    val top = _top()
    @connect fun d0() {
        top.ms_if con ms_if
    }

    @initial fun simulate() {
        ms_if.rstn set false
        repeat (5) {vk_wait_on(posedge(clk))}
        ms_if.rstn set true
        repeat (20) {vk_wait_on(posedge(clk))}
        vk_finish()
    }
}
