`timescale 1ns / 1ns

module Lock (
    input logic rst,
    input logic clk,
    input logic open,
    input logic close
);

    pkg::State state;

    always_ff @(posedge clk) begin: update
        if (rst) begin
            state <= pkg::State_CLOSED;
        end
        else if (state == pkg::State_OPENED) begin
            if (close) begin
                state <= pkg::State_CLOSING;
            end
        end
        else if (state == pkg::State_OPENING) begin
            state <= pkg::State_OPENED;
        end
        else if (state == pkg::State_CLOSED) begin
            if (open) begin
                state <= pkg::State_OPENING;
            end
        end
        else if (state == pkg::State_CLOSING) begin
            state <= pkg::State_CLOSED;
        end
    end: update

endmodule: Lock
