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

    val addr = _uint(2)
    val data = _uint(8)
}

class _link_tx: _modport {

    @input  val clk   = _bool()
    @input  val rstn  = _bool()
    @input  val ready = _bool()
    @output val req   = _req()
}

class _link_rx: _modport {

    @input  val clk   = _bool()
    @input  val rstn  = _bool()
    @output val ready = _bool()
    @input  val req   = _req()
}

class _link: _interf {

    @input val clk = _bool()

    val rstn   = _bool()
    val ready = _bool()
    val req    = _req()

    @comp val tx = _link_tx() with {
        it.clk con clk
        it.rstn con rstn
        it.ready con ready
        it.req con req
    }

    @comp val rx = _link_rx() with {
        it.clk con clk
        it.rstn con rstn
        it.ready con ready
        it.req con req
    }
}

class _tx: _module {

    @modport val link_tx = _link_tx()

    @reg fun clock() {
        on (posedge(link_tx.clk)) {
            if (!link_tx.rstn) {
                link_tx.req.addr reg 0
                link_tx.req.data reg 0
            } else {
                if (link_tx.ready) {
                    link_tx.req.addr reg_add 1
                    link_tx.req.data reg_mul 4
                }
            }
        }
    }
}

class _rx: _module {

    @modport val link_rx = _link_rx()

    val data     = _array(_uint(8), 4)
    val dly      = _bool()
    val addr_dly = _uint(2)

    @reg fun reg_data() {
        on(posedge(link_rx.clk)) {
            if (!link_rx.rstn) {
                data for_each { it reg 0 }
            } else {
                data[link_rx.req.addr] reg link_rx.req.data
            }
        }
    }

    @reg fun reg_dly() {
        on(posedge(link_rx.clk)) {
            dly reg if (link_rx.rstn) true else link_rx.ready
            addr_dly reg if (link_rx.rstn) uint(0b00) else link_rx.req.addr
        }
    }

    @put fun put_sready() {
        link_rx.ready put (red_nand(link_rx.req.addr) || !dly)
    }
}

class _top: _module {

    @interf val link = _link()

    @comp val tx = _tx() with {
        it.link_tx con link.tx
    }

    @comp val rx = _rx() with {
        it.link_rx  con link.rx
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

    @comp val link = _link() with { clk }

    @comp val top = _top() with { link }

    @initial fun simulate() {
        link.rstn put false
        wait(posedge(clk), 5)
        link.rstn put true
        wait(posedge(clk), 20)
        finish()
    }
}
