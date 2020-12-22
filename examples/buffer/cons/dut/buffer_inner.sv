module buffer_inner (
    input  logic [15:0] sw,
    output logic [15:0] led
);
    timeunit 1ns / 1ns;

    always_comb begin
        led = sw;
    end

endmodule: buffer_inner