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
                open class c : Class()
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
                open class c : Class() {
                
                    var x: Boolean = imp()
                }
            """.trimIndent()
        )
    }

    @Test
    fun `serialize class with parent`() {
        driveTextFileTest(
            """
                class d;
                endclass
                class c extends d;
                endclass
            """.trimIndent(),
            """
                open class d : Class()

                open class c : d()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize class with type parameter`() {
        driveTextFileTest(
            """
                module m #(type T=int);
                endmodule
            """.trimIndent(),
            """
                class m<T> : Module()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize class with value parameter`() {
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
    fun `serialize class with companion object`() {
        driveTextFileTest(
            """
                class c;
                    static function void f();
                    endfunction
                endclass
            """.trimIndent(),
            """
                open class c : Class() {

                    companion object {
                
                        fun f(): Unit = imp()
                    }
                }
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
                fun f(): Int = imp()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize function with value parameter`() {
        driveTextFileTest(
            """
                function void f(int x = 0);
                endfunction
            """.trimIndent(),
            """
                fun f(
                    x: Int = imp()
                ): Unit = imp()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize function with annotation`() {
        driveTextFileTest(
            """
                task t;
                endtask
            """.trimIndent(),
            """
                @Task
                fun t(): Unit = imp()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize function nullable`() {
        driveTextFileTest(
            """
                class c;
                endclass
                function c f(c x);
                endfunction
            """.trimIndent(),
            """
                open class c : Class()

                fun f(
                    x: c?
                ): c? = imp()
            """.trimIndent()
        )
    }

    @Test
    fun `serialize function parameterized`() {
        driveTextFileTest(
            """
                class c #(type T);
                    static function void f();
                    endfunction
                endclass
            """.trimIndent(),
            """
                open class c<T> : Class() {

                    companion object {

                        fun <T> f(): Unit = imp()
                    }
                }
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
                open class c : Class {
                
                    constructor(
                        x: Boolean
                    ) : super()
                }
            """.trimIndent()
        )
    }

    @Test
    fun `serialize constructor with parent`() {
        driveTextFileTest(
            """
                class c;
                    function new(logic x);
                    endfunction
                endclass
                class d extends c;
                    function new(logic x);
                    endfunction
                endclass
            """.trimIndent(),
            """
                open class c : Class {
                
                    constructor(
                        x: Boolean
                    ) : super()
                }

                open class d : c {

                    constructor(
                        x: Boolean
                    ) : super(imp())
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
                var x: Boolean = imp()
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
                val x: String = imp()
            """.trimIndent()
        )
    }
}
