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

package io.verik.importer.cast.common

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class SignatureBuilderTest : BaseTest() {

    @Test
    fun `signature moduleDeclarationNonAnsi`() {
        driveSignatureTest(
            SystemVerilogParser.ModuleDeclarationNonAnsiContext::class,
            """
                module m #(type T)(x, y);
                    input x;
                    output y;
                    logic z;
                endmodule
            """.trimIndent(),
            """
                module m #(
                    type T
                )(
                    x,
                    y
                );
                    input x;
                    output y;
                endmodule
            """.trimIndent()
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `signature moduleDeclarationAnsi`() {
        driveSignatureTest(
            SystemVerilogParser.ModuleDeclarationAnsiContext::class,
            """
                module m #(type T)(input x, output y);
                    logic z;
                endmodule
            """.trimIndent(),
            """
                module m #(
                    type T
                )(
                    input x,
                    output y
                );
            """.trimIndent()
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `signature classDeclaration`() {
        driveSignatureTest(
            SystemVerilogParser.ClassDeclarationContext::class,
            """
                class c #(type T)extends d #(e);
                    logic x;
                endclass
            """.trimIndent(),
            """
                class c #(
                    type T
                ) extends d #(e);
            """.trimIndent()
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `signature classMethodTask`() {
        driveSignatureTest(
            SystemVerilogParser.ClassMethodTaskContext::class,
            """
                class c;
                    virtual task t();
                    endtask
                endclass
            """.trimIndent(),
            """
                virtual task t();
            """.trimIndent()
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `signature classConstructorPrototype`() {
        driveSignatureTest(
            SystemVerilogParser.ClassConstructorPrototypeContext::class,
            """
                class c;
                    extern function new(logic x);
                endclass
            """.trimIndent(),
            """
                extern function new(
                    logic x
                );
            """.trimIndent()
        ) { it.findDeclaration("new") }
    }

    @Test
    fun `signature classConstructorDeclaration`() {
        driveSignatureTest(
            SystemVerilogParser.ClassConstructorDeclarationContext::class,
            """
                class c;
                    function new(logic x);
                        ${'$'}display();
                    endfunction
                endclass
            """.trimIndent(),
            """
                function new(
                    logic x
                );
            """.trimIndent()
        ) { it.findDeclaration("new") }
    }

    @Test
    fun `signature dataDeclarationData`() {
        driveSignatureTest(
            SystemVerilogParser.DataDeclarationDataContext::class,
            """
                const var automatic logic x = 0;
            """.trimIndent(),
            "const var automatic logic x;"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `signature dataTypeVector`() {
        driveSignatureTest(
            SystemVerilogParser.DataTypeVectorContext::class,
            """
                logic [3:0][1:0] x;
            """.trimIndent(),
            "logic [3:0][1:0] x;"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `signature dataTypeStruct`() {
        driveSignatureTest(
            SystemVerilogParser.DataTypeStructContext::class,
            """
                typedef struct { logic x; logic y; } s;
            """.trimIndent(),
            """
                typedef struct {
                    logic x;
                    logic y;
                } s;
            """.trimIndent()
        ) { it.findDeclaration("s") }
    }

    @Test
    fun `signature dataTypeEnum`() {
        driveSignatureTest(
            SystemVerilogParser.DataTypeEnumContext::class,
            """
                typedef enum { A, B } e;
            """.trimIndent(),
            """
                typedef enum {
                    A,
                    B
                } e;
            """.trimIndent()
        ) { it.findDeclaration("e") }
    }

    @Test
    @Disabled
    // TODO handle variable dimensions
    fun `signature variableDimension`() {
        driveSignatureTest(
            SystemVerilogParser.VariableDimensionContext::class,
            """
                logic x [$];
            """.trimIndent(),
            """
                logic x [$];
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `signature functionBodyDeclarationNoPortList`() {
        driveSignatureTest(
            SystemVerilogParser.FunctionBodyDeclarationNoPortListContext::class,
            """
                function int f;
                    input int x;
                    return x;
                endfunction
            """.trimIndent(),
            """
                function int f;
                    input int x;
                endfunction
            """.trimIndent()
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `signature functionBodyDeclarationPortList`() {
        driveSignatureTest(
            SystemVerilogParser.FunctionBodyDeclarationPortListContext::class,
            """
                function int f(int x);
                    return x;
                endfunction
            """.trimIndent(),
            """
                function int f(
                    int x
                );
            """.trimIndent()
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `signature functionPrototype`() {
        driveSignatureTest(
            SystemVerilogParser.FunctionPrototypeContext::class,
            """
                class c;
                    extern function void f(logic x);
                endclass
            """.trimIndent(),
            """
                extern function void f(
                    logic x
                );
            """.trimIndent()
        ) { it.findDeclaration("f") }
    }

    @Test
    fun `signature taskBodyDeclarationNoPortList`() {
        driveSignatureTest(
            SystemVerilogParser.TaskBodyDeclarationNoPortListContext::class,
            """
                task t;
                    input x;
                    ${'$'}display();
                endtask
            """.trimIndent(),
            """
                task t;
                    input x;
                endtask
            """.trimIndent()
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `signature taskBodyDeclarationPortList`() {
        driveSignatureTest(
            SystemVerilogParser.TaskBodyDeclarationPortListContext::class,
            """
                task t(logic x);
                    ${'$'}display();
                endtask
            """.trimIndent(),
            """
                task t(
                    logic x
                );
            """.trimIndent()
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `signature taskPrototype`() {
        driveSignatureTest(
            SystemVerilogParser.TaskPrototypeContext::class,
            """
                class c;
                    extern task t(logic x);
                endclass
            """.trimIndent(),
            """
                extern task t(
                    logic x
                );
            """.trimIndent()
        ) { it.findDeclaration("t") }
    }
}
