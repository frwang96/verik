module multiplier (
    input  logic        clk,
    input  logic        rst,
    input  logic [7:0]  in_a,
    input  logic [7:0]  in_b,
    input  logic        in_vld,
    output logic [15:0] res,
    output logic        res_rdy
);
    timeunit 1ns / 1ns;

    logic [7:0] a;
    logic [7:0] b;
    logic [7:0] prod;
    logic [7:0] tp;
    logic [3:0] i;

    always_ff @(posedge clk) begin: mul_step
        if (rst) begin
            a <= 8'h00;
            b <= 8'h00;
            prod <= 8'h00;
            tp <= 8'h00;
            i <= dut_pkg::WIDTH;
        end
        else if (in_vld) begin
            a <= in_a;
            b <= in_b;
            prod <= 8'h00;
            tp <= 8'h00;
            i <= 4'h0;
        end
        else if (i < 4'h8) begin
            automatic logic [8:0] sum;
            sum = b[0] ? tp + a : 9'(tp);
            b <= b >> 1;
            prod <= {sum[0], prod[7:1]};
            tp <= sum[8:1];
            i <= i + 4'h1;
        end
    end: mul_step

    always_comb begin: set_res
        res_rdy = i == 4'h8;
        res = {tp, prod};
    end: set_res

endmodule: multiplier