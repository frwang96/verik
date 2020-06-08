import com.verik.ref.tb_basic.tinyalu_dut.tinyalu

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class top: Module {

    enum class operation_t(val value: Bit): Enu {
        no_op  (Bit("3'b000")),
        add_op (Bit("3'b001")),
        and_op (Bit("3'b010")),
        xor_op (Bit("3'b011")),
        mul_op (Bit("3'b100")),
        rst_op (Bit("3'b111"));
    }

    @Logic val A      = UNum(8)
    @Logic val B      = UNum(8)
    @Logic val clk    = Bool()
    @Logic val reset  = Bool()
    @Logic val op     = Bit(3)
    @Logic val start  = Bool()
    @Logic val done   = Bool()
    @Logic val result = UNum(16)
    @Logic val op_set = operation_t.no_op

    val DUT = tinyalu()
    @Always fun connect() {
        DUT.connect(A, B, clk, op, reset, start, done, result)
        op set op_set.value
    }

    @Initial fun clock() {
        clk set false
        forever {
            vkDelay(10)
            clk set !clk
        }
    }

    @Fun fun get_op(): operation_t {
        return when (Bit(3).randomize()) {
            Bit("3'b000") -> operation_t.no_op
            Bit("3'b001") -> operation_t.add_op
            Bit("3'b010") -> operation_t.and_op
            Bit("3'b011") -> operation_t.xor_op
            Bit("3'b100") -> operation_t.mul_op
            Bit("3'b101") -> operation_t.no_op
            else -> operation_t.rst_op
        }
    }

    @Fun fun get_data(): UNum {
        return when (Bit(2).randomize()) {
            Bit("00") -> UNum("8'h00")
            Bit("11") -> UNum("8'hFF")
            else -> UNum(8).randomize()
        }
    }

    @Always fun scoreboard() {
        on (PosEdge(clk)) {
            vkDelay(1)
            val predicted_result = when (op_set) {
                operation_t.add_op -> A + B
                operation_t.and_op -> A and B
                operation_t.xor_op -> A xor B
                operation_t.mul_op -> A * B
                else -> UNum("0")
            }

            if ((op_set != operation_t.no_op) && (op_set != operation_t.rst_op)) {
                if (predicted_result != result) {
                    vkError("FAILED: A=$A B=$B op=$op result=$result")
                }
            }
        }
    }

    @Initial fun tester() {
        reset set true
        vkWaitOn(NegEdge(clk))
        vkWaitOn(NegEdge(clk))
        reset set true
        start set false
        repeat (1000) {
            send_op()
        }
    }

    @Task fun send_op() {
        vkWaitOn(NegEdge(clk))
        op_set set get_op()
        A set get_data()
        B set get_data()
        start set true
        when (op_set) {
            operation_t.no_op -> {
                vkWaitOn(PosEdge(clk))
                start set false
            }
            operation_t.rst_op -> {
                reset set true
                start set false
                vkWaitOn(NegEdge(clk))
                reset set false
            }
            else -> {
                vkWaitOn(PosEdge(done))
                start set false
            }
        }
    }
}