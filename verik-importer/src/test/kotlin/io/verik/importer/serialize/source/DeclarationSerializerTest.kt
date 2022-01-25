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

package io.verik.importer.serialize.source

import io.verik.importer.test.BaseTest
import org.junit.jupiter.api.Test

internal class DeclarationSerializerTest : BaseTest() {

    @Test
    fun `serialize class simple`() {
        driveTextFileTest(
            """
                class c;
                endclass
            """.trimIndent(),
            """
                open class c
            """.trimIndent()
        )
    }

    @Test
    fun `serialize class with property`() {
        driveTextFileTest(
            """
                class c;
                    logic x;
                endclass
            """.trimIndent(),
            """
                open class c {
                
                    var x: Boolean = imported()
                }
            """.trimIndent()
        )
    }

    @Test
    fun `serialize class with superType`() {
        driveTextFileTest(
            """
                class d;
                endclass
                class c extends d;
                endclass
            """.trimIndent(),
            """
                open class d

                open class c : d()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize class module`() {
        driveTextFileTest(
            """
                module m;
                endmodule
            """.trimIndent(),
            """
                class m : Module()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize class module with valueParameter`() {
        driveTextFileTest(
            """
                module m(input x);
                endmodule
            """.trimIndent(),
            """
                class m(
                    @In var x: Boolean
                ) : Module()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize class struct`() {
        driveTextFileTest(
            """
                typedef struct {
                    logic x;
                } s;
            """.trimIndent(),
            """
                class s(
                    var x: Boolean
                ) : Struct()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize enum`() {
        driveTextFileTest(
            """
                typedef enum { A, B } e;
            """.trimIndent(),
            """
                enum class e {
                    A,
                    B
                }
            """.trimIndent()
        )
    }

    @Test
    fun `serialize typeAlias`() {
        driveTextFileTest(
            """
                typedef logic t;
            """.trimIndent(),
            """
                typealias t = Boolean
            """.trimIndent()
        )
    }

    @Test
    fun `serialize function simple`() {
        driveTextFileTest(
            """
                function int f();
                endfunction
            """.trimIndent(),
            """
                fun f(): Int = imported()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize function with valueParameter`() {
        driveTextFileTest(
            """
                function void f(int x);
                endfunction
            """.trimIndent(),
            """
                fun f(
                    x: Int
                ): Unit = imported()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize function task`() {
        driveTextFileTest(
            """
                task t;
                endtask
            """.trimIndent(),
            """
                @Task
                fun t(): Unit = imported()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize constructor`() {
        driveTextFileTest(
            """
                class c;
                    function new(logic x);
                    endfunction
                endclass
            """.trimIndent(),
            """
                open class c {
                
                    constructor(
                        x: Boolean
                    )
                }
            """.trimIndent()
        )
    }

    @Test
    fun `serialize property mutable`() {
        driveTextFileTest(
            """
                logic x;
            """.trimIndent(),
            """
                var x: Boolean = imported()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize property immutable`() {
        driveTextFileTest(
            """
                const string x;
            """.trimIndent(),
            """
                val x: String = imported()
            """.trimIndent()
        )
    }
}
