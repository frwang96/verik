import com.verik.ref.tb_basic.tinyalu_dut.tinyalu

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

class top: Module {

    enum class operation_t(val value: Bit): Data {
        no_op  (Bit("3'b000")),
        add_op (Bit("3'b001")),
        and_op (Bit("3'b010")),
        xor_op (Bit("3'b011")),
        mul_op (Bit("3'b100")),
        rst_op (Bit("3'b111"));
    }

    @Wire var A      = Unsigned(8)
    @Wire var B      = Unsigned(8)
    @Wire var clk    = Bool()
    @Wire var reset  = Bool()
    @Wire var op     = Bit(3)
    @Wire var start  = Bool()
    @Wire var done   = Bool()
    @Wire var result = Unsigned(16)
    @Wire var op_set = operation_t.no_op

    val DUT = tinyalu()
    @Always fun connect() {
        DUT.connect(A, B, clk, op, reset, start, done, result)
        op con op_set.value
    }

    @Initial fun clock() {
        clk = false
        forever {
            vkDelay(10)
            clk = !clk
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

    @Fun fun get_data(): Unsigned {
        return when (Bit(2).randomize()) {
            Bit("00") -> Unsigned("8'h00")
            Bit("11") -> Unsigned("8'hFF")
            else -> Unsigned(8).randomize()
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
                else -> Unsigned("0")
            }

            if ((op_set != operation_t.no_op) && (op_set != operation_t.rst_op)) {
                if (predicted_result != result) {
                    vkError("FAILED: A=$A B=$B op=$op result=$result")
                }
            }
        }
    }

    @Initial fun tester() {
        reset = true
        vkWaitOn(NegEdge(clk))
        vkWaitOn(NegEdge(clk))
        reset = true
        start = false
        repeat (1000) {
            send_op()
        }
    }

    @Task fun send_op() {
        vkWaitOn(NegEdge(clk))
        op_set = get_op()
        A = get_data()
        B = get_data()
        start = true
        when (op_set) {
            operation_t.no_op -> {
                vkWaitOn(PosEdge(clk))
                start = false
            }
            operation_t.rst_op -> {
                reset = true
                start = false
                vkWaitOn(NegEdge(clk))
                reset = false
            }
            else -> {
                vkWaitOn(PosEdge(done))
                start = false
            }
        }
    }
}