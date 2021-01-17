/*
 * Copyright (c) 2021 Francis Wang
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

import verik.base.*
import verik.collection.*
import verik.data.*

class _req: _struct() {

    var addr = _ubit(2)
    var data = _ubit(8)
}

class _req_tx_bp: _bport() {

    @input  var clk   = _bool()
    @input  var rst_n = _bool()
    @input  var ready = _bool()
    @output var req   = _req()
}

class _req_rx_bp: _bport() {

    @input  var clk   = _bool()
    @input  var rst_n = _bool()
    @input  var req   = _req()
    @output var ready = _bool()
}

class _req_bus: _bus() {

    @input var clk = _bool()

    var rst_n = _bool()
    var ready = _bool()
    var req   = _req()

    @make val tx_bp = _req_tx_bp() with {
        it.clk   = clk
        it.rst_n = rst_n
        it.ready = ready
        req      = it.req
    }

    @make val rx_bp = _req_rx_bp() with {
        it.clk   = clk
        it.rst_n = rst_n
        it.req   = req
        ready    = it.ready
    }
}

class _tx: _module() {

    @bport val req_tx_bp = _req_tx_bp()

    @seq fun clock() {
        on (posedge(req_tx_bp.clk)) {
            if (!req_tx_bp.rst_n) {
                req_tx_bp.req.addr = ubit(0)
                req_tx_bp.req.data = ubit(0)
            } else {
                if (req_tx_bp.ready) {
                    req_tx_bp.req.addr += ubit(1)
                    req_tx_bp.req.data *= ubit(4)
                }
            }
        }
    }
}

class _rx: _module() {

    @bport val req_rx_bp = _req_rx_bp()

    private var data     = _array(4, _ubit(8))
    private var dly      = _bool()
    private var addr_dly = _ubit(2)

    @seq fun reg_data() {
        on(posedge(req_rx_bp.clk)) {
            if (!req_rx_bp.rst_n) {
                for (i in range(data.SIZE)) {
                    data[i] = ubit(0)
                }
            } else {
                data[req_rx_bp.req.addr] = req_rx_bp.req.data
            }
        }
    }

    @seq fun reg_dly() {
        on(posedge(req_rx_bp.clk)) {
            dly = if (req_rx_bp.rst_n) true else req_rx_bp.ready
            addr_dly = if (req_rx_bp.rst_n) ubit(0) else req_rx_bp.req.addr
        }
    }

    @com fun put_ready() {
        req_rx_bp.ready = (req_rx_bp.req.addr.inv().red_and() || !dly)
    }
}

class _top: _module() {

    @bus val req_bus = _req_bus()

    @make val tx = _tx() with {
        it.req_tx_bp con req_bus.tx_bp
    }

    @make val rx = _rx() with {
        it.req_rx_bp con req_bus.rx_bp
    }
}

@top class _tb: _module() {

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
        req_bus.rst_n = false
        repeat(5) { wait(posedge(clk)) }
        req_bus.rst_n = true
        repeat(20) { wait(posedge(clk)) }
        finish()
    }
}
