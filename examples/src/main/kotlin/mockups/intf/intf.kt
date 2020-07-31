/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mockups.intf

import io.verik.common.*
import io.verik.common.types.*

class _req: _struct {
    val addr = _uint(2)
    val data = _uint(8)
}

class _ms_if: _intf {
    @input val clk = _bool()

    val sready = _bool()
    val rstn   = _bool()
    val req    = _req()

    class _master(it: _ms_if): _iport {
        @input  val req    = it.req
        @input  val rstn   = it.rstn
        @input  val clk    = it.clk
        @output val sready = it.sready
    }

    val master = _master(this)

    class _slave(it: _ms_if): _iport {
        @input  val clk    = it.clk
        @input  val sready = it.sready
        @input  val rstn   = it.rstn
        @output val req    = it.req
    }

    val slave = _slave(this)
}

class _master: _module {
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

class _slave: _module {
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

class _top: _module {
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
