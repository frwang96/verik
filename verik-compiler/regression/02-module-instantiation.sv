/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Test0.sv ////////////////////////////////////////////////////////////////////////////////////////////////////////////

`ifndef VERIK
`define VERIK
`timescale 1ns / 1ns
`endif

module M0;

    M1 m0 (
        .C  ( 8'h00 ),
        .x0 ( 1'b0 ),
        .x1 ( 4'b0000 ),
        .x2 (  )
    );

    M1 m1 (
        .C  ( 8'h00 ),
        .x0 ( 1'b0 ),
        .x1 ( 4'b0000 ),
        .x2 (  )
    );

endmodule : M0

// Test1.sv ////////////////////////////////////////////////////////////////////////////////////////////////////////////

`ifndef VERIK
`define VERIK
`timescale 1ns / 1ns
`endif

module M1(
    input  logic [7:0] C,
    input  logic       x0,
    input  logic [3:0] x1,
    output logic       x2
);

endmodule : M1
