package com.verik.ref.basic_tb

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

fun _alu_op() = _enum(_alu_op.values())
enum class _alu_op(val rep: _uint = _uint(3)): _enum {
    no_op  (uint(0b000)),
    add_op (uint(0b001)),
    and_op (uint(0b010)),
    xor_op (uint(0b011)),
    mul_op (uint(0b100)),
    rst_op (uint(0b111));
}

@top class _tb: _module {

    val A      = _uint(8)
    val B      = _uint(8)
    val clk    = _bool()
    val reset  = _bool()
    val start  = _bool()
    val done   = _bool()
    val result = _uint(16)
    val op_set = _alu_op()

    val op = op_set.rep

    @comp val tinyalu = _tinyalu() with {
        A; B; clk; op; reset; start; done; result
    }

    @initial fun clock() {
        clk set false
        forever {
            vk_wait(10)
            clk set !clk
        }
    }

    fun get_op(): _alu_op {
        return when (uint(3, vk_random())) {
            uint(0b000) -> _alu_op.no_op
            uint(0b001) -> _alu_op.add_op
            uint(0b010) -> _alu_op.and_op
            uint(0b011) -> _alu_op.xor_op
            uint(0b100) -> _alu_op.mul_op
            uint(0b101) -> _alu_op.no_op
            else -> _alu_op.rst_op
        }
    }

    fun get_data(): _uint {
        return when (uint(2, vk_random())) {
            uint(0b00) -> uint(0x00)
            uint(0b11) -> uint(0xFF)
            else -> uint(8, vk_random())
        }
    }

    @seq fun scoreboard() {
        on (posedge(clk)) {
            vk_wait(1)
            val predicted_result = when (op_set) {
                _alu_op.add_op -> ext(16, A add B)
                _alu_op.and_op -> ext(16, A and B)
                _alu_op.xor_op -> ext(16, A xor B)
                _alu_op.mul_op -> A mul B
                else -> uint(16, 0)
            }

            if (op_set != _alu_op.no_op && op_set != _alu_op.rst_op) {
                if (predicted_result != result) {
                    vk_error("FAILED: A=$A B=$B op=$op result=$result")
                }
            }
        }
    }

    @initial fun tester() {
        reset set true
        vk_wait_on(negedge(clk))
        vk_wait_on(negedge(clk))
        reset set true
        start set false
        repeat (1000) {
            send_op()
        }
    }

    @task fun send_op() {
        vk_wait_on(negedge(clk))
        op_set set get_op()
        A set get_data()
        B set get_data()
        start set true
        when (op_set) {
            _alu_op.no_op -> {
                vk_wait_on(posedge(clk))
                start set false
            }
            _alu_op.rst_op -> {
                reset set true
                start set false
                vk_wait_on(negedge(clk))
                reset set false
            }
            else -> {
                vk_wait_on(posedge(done))
                start set false
            }
        }
    }
}