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

// test.sv /////////////////////////////////////////////////////////////////////////////////////////////////////////////

task t0;
    input x0;
endtask

class c;

    task t1;
        input int x1;
    endtask

    task t2(logic [3:0] x2, logic x3);
    endtask

    extern task t3(int x4, int x5);

endclass