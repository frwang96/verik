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

import verik.base.*
import verik.collections.*
import verik.data.*

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
    @input  var req   = _req()
    @output var ready = _bool()
}

class _req_bus: _bus {

    @input var clk = _bool()

    var rstn  = _bool()
    var ready = _bool()
    var req   = _req()

    @make val tx = _req_tx() with {
        it.clk   = clk
        it.rstn  = rstn
        it.ready = ready
        req      = it.req
    }

    @make val rx = _req_rx() with {
        it.clk  = clk
        it.rstn = rstn
        it.req  = req
        ready   = it.ready
    }
}

class _tx: _module {

    @busport val req_tx = _req_tx()

    @seq fun clock() {
        on (posedge(req_tx.clk)) {
            if (!req_tx.rstn) {
                req_tx.req.addr = uint(0)
                req_tx.req.data = uint(0)
            } else {
                if (req_tx.ready) {
                    req_tx.req.addr += 1
                    req_tx.req.data *= 4
                }
            }
        }
    }
}

class _rx: _module {

    @busport val req_rx = _req_rx()

    private var data     = _array(_uint(8), 4)
    private var dly      = _bool()
    private var addr_dly = _uint(2)

    @seq fun reg_data() {
        on(posedge(req_rx.clk)) {
            if (!req_rx.rstn) {
                data for_indices { data[it] = uint(0) }
            } else {
                data[req_rx.req.addr] = req_rx.req.data
            }
        }
    }

    @seq fun reg_dly() {
        on(posedge(req_rx.clk)) {
            dly = if (req_rx.rstn) true else req_rx.ready
            addr_dly = if (req_rx.rstn) uint(0) else req_rx.req.addr
        }
    }

    @com fun put_sready() {
        req_rx.ready = (red_nand(req_rx.req.addr) || !dly)
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

    private var clk = _bool()

    @run fun clock() {
        clk = false
        forever {
            delay(10)
            clk = !clk
        }
    }

    @make val req_bus = _req_bus() with {
        it.clk = clk
    }

    @make val top = _top() with {
        it.req_bus con req_bus
    }

    @run fun simulate() {
        req_bus.rstn = false
        repeat(5) { wait(posedge(clk)) }
        req_bus.rstn = true
        repeat(20) { wait(posedge(clk)) }
        finish()
    }
}
