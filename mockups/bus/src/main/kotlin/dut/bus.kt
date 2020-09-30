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

package dut

import verik.common.*
import verik.common.collections.*
import verik.common.data.*

class _req: _struct {

    var addr = _uint(2)
    var data = _uint(8)
}

class _req_tx: _busport {

    @input  var clk   = _bool()
    @input  var rstn  = _bool()
    @input  var ready = _bool()
    @output var req   = _req()
}

class _req_rx: _busport {

    @input  var clk   = _bool()
    @input  var rstn  = _bool()
    @output var ready = _bool()
    @input  var req   = _req()
}

class _req_bus: _bus {

    @input var clk = _bool()

    var rstn   = _bool()
    var ready = _bool()
    var req    = _req()

    @make val tx = _req_tx() with {
        it.clk   con clk
        it.rstn  con rstn
        it.ready con ready
        it.req   con req
    }

    @make val rx = _req_rx() with {
        it.clk   con clk
        it.rstn  con rstn
        it.ready con ready
        it.req   con req
    }
}

class _tx: _module {

    @busport val req_tx = _req_tx()

    @seq fun clock() {
        on (posedge(req_tx.clk)) {
            if (!req_tx.rstn) {
                req_tx.req.addr reg 0
                req_tx.req.data reg 0
            } else {
                if (req_tx.ready) {
                    req_tx.req.addr reg_add 1
                    req_tx.req.data reg_mul 4
                }
            }
        }
    }
}

class _rx: _module {

    @busport val req_rx = _req_rx()

    var data     = _array(_uint(8), 4)
    var dly      = _bool()
    var addr_dly = _uint(2)

    @seq fun reg_data() {
        on(posedge(req_rx.clk)) {
            if (!req_rx.rstn) {
                data for_each { it reg 0 }
            } else {
                data[req_rx.req.addr] reg req_rx.req.data
            }
        }
    }

    @seq fun reg_dly() {
        on(posedge(req_rx.clk)) {
            dly reg if (req_rx.rstn) true else req_rx.ready
            addr_dly reg if (req_rx.rstn) uint(2, 0b00) else req_rx.req.addr
        }
    }

    @comb fun put_sready() {
        req_rx.ready put (red_nand(req_rx.req.addr) || !dly)
    }
}

class _top: _module {

    @bus val req_bus = _req_bus()

    @make val tx = _tx() with {
        it.req_tx con req_bus.tx
    }

    @make val rx = _rx() with {
        it.req_rx con req_bus.rx
    }
}

@top class _tb: _module {

    var clk = _bool()

    @run fun clock() {
        clk put false
        forever {
            delay(10)
            clk put !clk
        }
    }

    @make val req_bus = _req_bus() with {
        it.clk con clk
    }

    @make val top = _top() with {
        it.req_bus con req_bus
    }

    @run fun simulate() {
        req_bus.rstn put false
        repeat(5) { wait(posedge(clk)) }
        req_bus.rstn put true
        repeat(20) { wait(posedge(clk)) }
        finish()
    }
}
