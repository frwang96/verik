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

    logic       x;
    logic [7:0] y;
    logic [7:0] z;

    initial begin : f
        $display();
    end : f

    always_ff @(posedge x) begin : g
        y <= z;
    end : g

    logic a;

    always_comb begin : __0
        a = ~x;
    end : __0

    logic b;

    always_comb begin : h
        b = ~a;
    end : h

endmodule : M
