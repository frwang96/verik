package mockups.intf

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
    class _master(it: _ms_if): _iport {
        @input  val req    = it.req
        @input  val rstn   = it.rstn
        @input  val clk    = it.clk
        @output val sready = it.sready
    }

    val slave = _slave(this)
    class _slave(it: _ms_if): _iport {
        @input  val clk    = it.clk
        @input  val sready = it.sready
        @input  val rstn   = it.rstn
        @output val req    = it.req
    }
}

class _master: _circuit {
    @iport val master = _ms_if().master

    @reg fun clock() {
        on (posedge(master.clk)) {
            if (!master.rstn) {
                master.req.addr reg 0
                master.req.data reg 0
            } else {
                if (master.sready) {
                    master.req.addr reg_add 1
                    master.req.data reg_mul 4
                }
            }
        }
    }
}

class _slave: _circuit {
    @input  val req    = _req()
    @input  val rstn   = _bool()
    @output val sready = _bool()
    @iport   val slave  = _ms_if().slave

    val data     = _array(4, _uint(8))
    val dly      = _bool()
    val addr_dly = _uint(2)

    @reg fun reg_data() {
        on(posedge(slave.clk)) {
            if (!slave.rstn) {
                data.for_each { it reg 0 }
            } else {
                data[slave.req.addr] reg slave.req.data
            }
        }
    }

    @reg fun reg_dly() {
        on(posedge(slave.clk)) {
            dly reg if (slave.rstn) true else slave.sready
            addr_dly reg if (slave.rstn) uint(0b00) else slave.req.addr
        }
    }

    @put fun put_sready() {
        slave.sready put (red_nand(slave.req.addr) || !dly)
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
        clk put false
        forever {
            wait(10)
            clk put !clk
        }
    }

    @comp val ms_if = _ms_if() with { clk }

    @comp val top = _top() with { ms_if }

    @initial fun simulate() {
        ms_if.rstn put false
        wait(posedge(clk), 5)
        ms_if.rstn put true
        wait(posedge(clk), 20)
        finish()
    }
}
