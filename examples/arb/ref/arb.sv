`timescale 1ns/1ns

module top;
  bit  clk;
  always #5 clk = !clk;

  arb_if arbif(clk);
  arb a1 (arbif.DUT);
  test t1(arbif.TEST);

endmodule

interface arb_if(input bit clk);
  logic [1:0] grant;
  logic [1:0] request;
  logic reset;

  clocking cb @(posedge clk);
    output request;
    input grant;
  endclocking

  modport TEST (
    clocking cb,
    output reset
  );

  modport DUT (
    input request,
    input reset,
    input clk,
    output grant
  );

endinterface

module arb (arb_if arbif);

  always @(posedge arbif.clk or posedge arbif.reset) begin
     if (arbif.reset) begin
        arbif.grant <= 2'b00;
     end else if (arbif.request[0]) begin
        arbif.grant <= 2'b01;
     end else if (arbif.request[1]) begin
        arbif.grant <= 2'b10;
     end else begin
        arbif.grant <= 2'b00;
     end
  end

endmodule

module test (arb_if arbif);

    initial begin
        @arbif.cb;
        arbif.cb.request <= 2'b01;
        $display("@%0t: Drove req=01", $time());
        repeat (2) @arbif.cb;
        if (arbif.cb.grant == 2'b01) begin
            $display("@%0t: Success", $time());
        end else begin
            $display("@%0t: Error", $time());
        end
        $finish();
    end

endmodule
