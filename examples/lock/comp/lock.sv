`timescale 1ns / 1ns

module lock (
    input logic rst,
    input logic clk,
    input logic open,
    input logic close
);

    pkg::state state;

    always_ff @(posedge clk) begin: update
        if (rst) begin
            state <= pkg::STATE_CLOSED;
        end
        else if (state == pkg::STATE_OPENED) begin
            if (close) begin
                state <= pkg::STATE_CLOSING;
            end
        end
        else if (state == pkg::STATE_OPENING) begin
            state <= pkg::STATE_OPENED;
        end
        else if (state == pkg::STATE_CLOSED) begin
            if (open) begin
                state <= pkg::STATE_OPENING;
            end
        end
        else if (state == pkg::STATE_CLOSING) begin
            state <= pkg::STATE_CLOSED;
        end
    end: update

endmodule: lock
