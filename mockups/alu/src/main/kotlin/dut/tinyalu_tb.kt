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
import verik.common.data.*
import verik.common.system.println
import verik.common.system.random_uint

enum class _alu_op(override val value: _uint = _uint(3)): _enum {
    NOP(uint(0b000)),
    ADD(uint(0b001)),
    AND(uint(0b010)),
    XOR(uint(0b011)),
    MUL(uint(0b100)),
    RST(uint(0b111))
}

@top class _tb: _module {
    val a      = _uint(8)
    val b      = _uint(8)
    val clk    = _bool()
    val reset  = _bool()
    val start  = _bool()
    val done   = _bool()
    val result = _uint(16)
    val op_set = _alu_op()

    val op = op_set.value

    @comp val tinyalu = _tinyalu() with {
        a; b; clk; op; reset; start; done; result
    }

    @initial fun clock() {
        clk put false
        forever {
            wait(10)
            clk put !clk
        }
    }

    fun get_op() = when (random_uint(3)) {
        uint(0b000) -> _alu_op.NOP
        uint(0b001) -> _alu_op.ADD
        uint(0b010) -> _alu_op.AND
        uint(0b011) -> _alu_op.XOR
        uint(0b100) -> _alu_op.MUL
        uint(0b101) -> _alu_op.NOP
        else -> _alu_op.RST
    }

    fun get_data() = when (random_uint(2)) {
        uint(0b00) -> uint(0x00)
        uint(0b11) -> uint(0xFF)
        else -> random_uint(2)
    }

    @reg fun scoreboard() {
        on (posedge(clk)) {
            wait(1)
            val predicted_result = when (op_set) {
                _alu_op.ADD -> ext(16, a add b)
                _alu_op.AND -> ext(16, a and b)
                _alu_op.XOR -> ext(16, a xor b)
                _alu_op.MUL -> a mul b
                else -> uint(16, 0)
            }

            if (op_set neq _alu_op.NOP && op_set neq _alu_op.RST) {
                if (predicted_result neq result) {
                    println("FAILED: A=$a B=$b op=$op result=$result")
                }
            }
        }
    }

    @initial fun tester() {
        reset put true
        wait(negedge(clk))
        wait(negedge(clk))
        reset put true
        start put false
        repeat (1000) {
            send_op()
        }
    }

    @task fun send_op() {
        wait(negedge(clk))
        op_set put get_op()
        a put get_data()
        b put get_data()
        start put true
        when (op_set) {
            _alu_op.NOP -> {
                wait(posedge(clk))
                start put false
            }
            _alu_op.RST -> {
                reset put true
                start put false
                wait(negedge(clk))
                reset put false
            }
            else -> {
                wait(posedge(done))
                start put false
            }
        }
    }
}