module print;
    timeunit 1ns / 1ns;

    logic       clk;
    logic       reset;
    logic [7:0] count;

    always_ff @(posedge clk) begin
        $display($sformatf("count=%0d", count));
        if (reset) begin
            count <= 8'h00;
        end
        else begin
            count <= count + 8'h01;
        end
    end

    initial begin
        clk = 1'b0;
        forever begin
            #1;
            clk = !clk;
        end
    end

    initial begin
        reset = 1'b1;
        #2;
        reset = 1'b0;
        #16;
        $finish();
    end

endmodule: print
