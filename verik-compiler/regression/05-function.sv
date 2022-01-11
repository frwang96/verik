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

    function automatic logic f();
        return 1'b0;
    endfunction : f

    function automatic logic [7:0] g(
        input logic [7:0] x
    );
        return ~x;
    endfunction : g

    initial begin : h
        $display(f());
        $display(g(.x(8'h00)));
    end : h

endmodule : M
