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

package io.verik.compiler.transform.upper

import io.verik.compiler.test.BaseTest
import org.junit.jupiter.api.Test

internal class IfAndWhenExpressionUnlifterStageTest : BaseTest() {

    @Test
    fun `unlift if expression`() {
        driveTextFileTest(
            """
                var x = true
                fun f() {
                    val y = if (x) {
                        println()
                        0
                    } else 1
                }
            """.trimIndent(),
            """
                logic x = 1'b1;

                function automatic void f();
                    int __0;
                    int y;
                    if (x) begin
                        ${'$'}display();
                        __0 = 0;
                    end
                    else begin
                        __0 = 1;
                    end
                    y = __0;
                endfunction : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `unlift when expression`() {
        driveTextFileTest(
            """
                var x = true
                fun f() {
                    val y = when {
                        x -> 1
                        else -> 0
                    }
                }
            """.trimIndent(),
            """
                logic x = 1'b1;

                function automatic void f();
                    int __0;
                    int y;
                    case (1'b1)
                        x : begin
                            __0 = 1;
                        end
                        default : begin
                            __0 = 0;
                        end
                    endcase
                    y = __0;
                endfunction : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }

    @Test
    fun `unlift when expression Nothing type`() {
        driveTextFileTest(
            """
                var x = true
                fun f() {
                    @Suppress("SimplifyWhenWithBooleanConstantCondition")
                    val y = when {
                        x -> 0
                        else -> fatal()
                    }
                }
            """.trimIndent(),
            """
                logic x = 1'b1;

                function automatic void f();
                    int __0;
                    int y;
                    case (1'b1)
                        x : begin
                            __0 = 0;
                        end
                        default : begin
                            ${'$'}fatal();
                        end
                    endcase
                    y = __0;
                endfunction : f
            """.trimIndent()
        ) { it.regularPackageTextFiles[0] }
    }
}
