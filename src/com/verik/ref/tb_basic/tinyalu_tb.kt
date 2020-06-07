import com.verik.ref.tb_basic.tinyalu_dut.tinyalu

import com.verik.common.*

// Copyright (c) 2020 Francis Wang

@Simulatable @Module class top {

    @Type enum class operation_t(val value: Bit): Logic {
        no_op  (Bit("3'b000")),
        add_op (Bit("3'b001")),
        and_op (Bit("3'b010")),
        xor_op (Bit("3'b011")),
        mul_op (Bit("3'b100")),
        rst_op (Bit("3'b111"));

        companion object {
            operator fun invoke(vararg size: Int): operation_t {return no_op}
        }
    }

    @Wire var A      = Unsigned(8)
    @Wire var B      = Unsigned(8)
    @Wire var clk    = Bool()
    @Wire var reset  = Bool()
    @Wire var op     = Bit(3)
    @Wire var start  = Bool()
    @Wire var done   = Bool()
    @Wire var result = Unsigned(16)
    @Wire var op_set = operation_t()

    @Always fun set_op() {
        op = op_set.value
    }

    @Module val DUT = tinyalu()
    @Always fun connect_tinyalu() {
        val m = DUT
        m.A      set A
        m.B      set B
        m.clk    set clk
        m.op     set op
        m.reset  set reset
        m.start  set start
        m.done   get done
        m.result get result
    }

    @Initial fun clock() {
        clk = false
        forever {
            vkDelay(10)
            clk = !clk
        }
    }

    fun get_op(): operation_t {
        val op_choice = Bit(3)
        op_choice.randomize()
        return when (op_choice) {
            Bit("3'b000") -> operation_t.no_op
            Bit("3'b001") -> operation_t.add_op
            Bit("3'b010") -> operation_t.and_op
            Bit("3'b011") -> operation_t.xor_op
            Bit("3'b100") -> operation_t.mul_op
            Bit("3'b101") -> operation_t.no_op
            Bit("3'b110") -> operation_t.rst_op
            else -> operation_t.rst_op
        }
    }

    fun get_data(): Unsigned {
        val zero_ones = Bit(2)
        zero_ones.randomize()
        return when (zero_ones) {
            Bit("00") -> Unsigned("8'h00")
            Bit("11") -> Unsigned("8'hFF")
            else -> {
                val data = Unsigned(8)
                data.randomize()
                data
            }
        }
    }

    @Always fun scoreboard() {
        on (PosEdge(clk)) {
            var predicted_result = Unsigned(16)
            vkDelay(1)
            predicted_result = when (op_set) {
                operation_t.add_op -> A + B
                operation_t.and_op -> A and B
                operation_t.xor_op -> A xor B
                operation_t.mul_op -> A * B
                else -> Unsigned("0")
            }

            if (op_set !in arrayOf(operation_t.no_op, operation_t.rst_op)) {
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
}