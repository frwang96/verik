`timescale 1ns / 1ns

module Count;

    logic       clk;
    logic       rst;
    logic [7:0] count;

    always_ff @(posedge clk) begin: count
        $display($sformatf("count=%0d", count));
        if (rst) begin
            count <= 8'h00;
        end
        else begin
            count <= count + 8'h01;
        end
    end: count

    initial begin: toggle_clk
        clk = 1'b0;
        forever begin
            #1;
            clk = !clk;
        end
    end: toggle_clk

    initial begin: toggle_rst
        rst = 1'b1;
        #2;
        rst = 1'b0;
        #16;
        $finish();
    end: toggle_rst

endmodule: Count
