module buffer_outer (
    input  logic [15:0] sw,
    output logic [15:0] led
);
    timeunit 1ns / 1ns;

    buffer_inner buffer_inner (
        .sw  (sw),
        .led (led)
    );

endmodule: buffer_outer
