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

// M.sv ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

`ifndef VERIK
`define VERIK
`timescale 1ns / 1ns
`endif

module M;

    logic       x0;
    logic [7:0] x1;
    logic [7:0] x2;

    initial begin : f0
        $display();
    end : f0

    always_ff @(posedge x0) begin : f1
        x1 <= x2;
    end : f1

    logic x3;

    always_comb begin : __0
        x3 = ~x0;
    end : __0

    logic x4;

    always_comb begin : f2
        x4 = ~x0;
    end : f2

endmodule : M
