/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.Core
import io.verik.compiler.test.CoreDeclarationTest
import org.junit.jupiter.api.Test

internal class CoreVkControlTest : CoreDeclarationTest() {

    @Test
    fun `serialize posedge negedge on`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_posedge_Boolean,
                Core.Vk.F_negedge_Boolean,
                Core.Vk.F_on_Event_Event_Function,
                Core.Vk.F_onr_Event_Event_Function
            ),
            """
                @Suppress("MemberVisibilityCanBePrivate")
                class M : Module() {
                    var x: Boolean = nc()
                    var y: Boolean = nc()
                    @Seq
                    fun f() { on (posedge(x)) { y = !y } }
                    @Seq
                    fun g() { on (posedge(x), negedge(x)) { y = !y } }
                    @Seq
                    var z = onr(negedge(x)) { !y }
                }
            """.trimIndent(),
            """
                module M;

                    always_ff @(posedge x) begin : f
                        y <= !y;
                    end : f
                
                    always_ff @(posedge x, negedge x) begin : g
                        y <= !y;
                    end : g

                    always_ff @(negedge x) begin : __0
                        z <= !y;
                    end : __0

                endmodule : M
            """.trimIndent()
        )
    }

    @Test
    fun `serialize forever`() {
        driveCoreDeclarationTest(
            listOf(Core.Vk.F_forever_Function),
            """
                class M : Module() {
                    @Run
                    fun f() { forever {} }
                }
            """.trimIndent(),
            """
                module M;

                    initial begin : f
                        forever begin
                        end
                    end : f

                endmodule : M
            """.trimIndent()
        )
    }

    @Test
    fun `serialize delay wait`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_delay_Int,
                Core.Vk.F_wait_Boolean,
                Core.Vk.F_wait_Event,
                Core.Vk.F_wait_ClockingBlock
            ),
            """
                class CB(override val event:  Event) : ClockingBlock()
                @Suppress("MemberVisibilityCanBePrivate")
                class M : Module() {
                    var clk: Boolean = nc()
                    @Make
                    val cb = CB(posedge(clk))
                    @Run
                    fun f() {
                        delay(0)
                        wait(clk)
                        wait(posedge(clk))
                        wait(cb)
                    }
                }
            """.trimIndent(),
            """
                module M;

                    initial begin : f
                        #0;
                        wait(clk);
                        @(posedge clk);
                        @(cb);
                    end : f

                endmodule : M
            """.trimIndent()
        )
    }

    @Test
    fun `serialize fork join`() {
        driveCoreDeclarationTest(
            listOf(
                Core.Vk.F_fork_Function,
                Core.Vk.F_join
            ),
            """
                @Task
                fun f() {
                    fork { delay(10) }
                    join()
                }
            """.trimIndent(),
            """
                task automatic f();
                    fork
                        begin
                            #10;
                        end
                    join_none
                    wait fork;
                endtask : f
            """.trimIndent()
        )
    }
}
