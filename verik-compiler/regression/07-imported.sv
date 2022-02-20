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

// Test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

module M;

    initial begin : f
        logic                   x1;
        C0                      c0;
        test_pkg::C1 #(.T(int)) c1;
        logic                   x2;
        x1 = x0;
        c0 = C0::new();
        c1 = test_pkg::C1 #(.T(int))::new();
        x2 = test_pkg::C1 #(.T(int))::f0();
    end : f

endmodule : M
