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
                class c
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
                class c {
                
                    var x: Boolean = imported()
                }
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
    fun `serialize class module with value parameter`() {
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
    fun `serialize function with value parameter`() {
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
    fun `serialize property simple`() {
        driveTextFileTest(
            """
                logic x;
            """.trimIndent(),
            """
                var x: Boolean = imported()
            """.trimIndent()
        )
    }
}
