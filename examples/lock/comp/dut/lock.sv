module lock (
    input logic rst,
    input logic clk,
    input logic open,
    input logic close
);
    timeunit 1ns / 1ns;

    dut_pkg::state state;

    always_ff @(posedge clk) begin: update
        if (rst) begin
            state <= dut_pkg::STATE_CLOSED;
        end
        else if (state == dut_pkg::STATE_OPENED) begin
            if (close) begin
                state <= dut_pkg::STATE_CLOSING;
            end
        end
        else if (state == dut_pkg::STATE_OPENING) begin
            state <= dut_pkg::STATE_OPENED;
        end
        else if (state == dut_pkg::STATE_CLOSED) begin
            if (open) begin
                state <= dut_pkg::STATE_OPENING;
            end
        end
        else if (state == dut_pkg::STATE_CLOSING) begin
            state <= dut_pkg::STATE_CLOSED;
        end
    end: update

endmodule: lock
