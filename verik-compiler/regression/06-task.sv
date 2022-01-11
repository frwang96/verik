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

    task automatic f();
        #1;
    endtask : f

    task automatic g(
        input logic [7:0]  x,
        output logic [7:0] __return
    );
        #1;
        __return = ~x;
        return;
    endtask : g

    initial begin : h
        automatic logic [7:0] __0;
        automatic logic [7:0] x;
        f();
        g(.x(8'h00), .__return(__0));
        x = __0;
        $display(x);
    end : h

endmodule : M
