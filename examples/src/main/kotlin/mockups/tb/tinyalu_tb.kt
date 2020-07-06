package mockups.tb

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

enum class _alu_op(val rep: _uint = _uint(3)): _enum {
    NOP  (uint(0b000)),
    ADD (uint(0b001)),
    AND (uint(0b010)),
    XOR (uint(0b011)),
    MUL (uint(0b100)),
    RST (uint(0b111));
}

fun _alu_op() = _enum(_alu_op.values())

@top class _tb: _module {
    val a      = _uint(8)
    val b      = _uint(8)
    val clk    = _bool()
    val reset  = _bool()
    val start  = _bool()
    val done   = _bool()
    val result = _uint(16)
    val op_set = _alu_op()

    val op = op_set.rep

    @comp val tinyalu = _tinyalu() with {
        a; b; clk; op; reset; start; done; result
    }

    @initial fun clock() {
        clk put false
        forever {
            vk_wait(10)
            clk put !clk
        }
    }

    fun get_op() = when (uint(3, vk_random())) {
        uint(0b000) -> _alu_op.NOP
        uint(0b001) -> _alu_op.ADD
        uint(0b010) -> _alu_op.AND
        uint(0b011) -> _alu_op.XOR
        uint(0b100) -> _alu_op.MUL
        uint(0b101) -> _alu_op.NOP
        else -> _alu_op.RST
    }

    fun get_data() = when (uint(2, vk_random())) {
        uint(0b00) -> uint(0x00)
        uint(0b11) -> uint(0xFF)
        else -> uint(8, vk_random())
    }

    @reg fun scoreboard() {
        on (posedge(clk)) {
            vk_wait(1)
            val predicted_result = when (op_set) {
                _alu_op.ADD -> ext(16, a add b)
                _alu_op.AND -> ext(16, a and b)
                _alu_op.XOR -> ext(16, a xor b)
                _alu_op.MUL -> a mul b
                else -> uint(16, 0)
            }

            if (op_set != _alu_op.NOP && op_set != _alu_op.RST) {
                if (predicted_result != result) {
                    vk_error("FAILED: A=$a B=$b op=$op result=$result")
                }
            }
        }
    }

    @initial fun tester() {
        reset put true
        vk_wait_on(negedge(clk))
        vk_wait_on(negedge(clk))
        reset put true
        start put false
        repeat (1000) {
            send_op()
        }
    }

    @task fun send_op() {
        vk_wait_on(negedge(clk))
        op_set put get_op()
        a put get_data()
        b put get_data()
        start put true
        when (op_set) {
            _alu_op.NOP -> {
                vk_wait_on(posedge(clk))
                start put false
            }
            _alu_op.RST -> {
                reset put true
                start put false
                vk_wait_on(negedge(clk))
                reset put false
            }
            else -> {
                vk_wait_on(posedge(done))
                start put false
            }
        }
    }
}