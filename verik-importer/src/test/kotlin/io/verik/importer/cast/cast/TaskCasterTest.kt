/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TaskCasterTest : BaseTest() {

    @Test
    fun `cast task from classMethodTask`() {
        driveCasterTest(
            SystemVerilogParser.ClassMethodTaskContext::class,
            """
                class c;
                    static task t();
                    endtask
                endclass
            """.trimIndent(),
            "Task(t, [], 1)"
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `cast task from taskBodyDeclarationNoPortList`() {
        driveCasterTest(
            SystemVerilogParser.TaskBodyDeclarationNoPortListContext::class,
            """
                task t;
                    input x;
                endtask
            """.trimIndent(),
            "Task(t, [SvValueParameter(x, SimpleDescriptor(Boolean), 0)], 0)"
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `cast task from taskBodyDeclarationPortList`() {
        driveCasterTest(
            SystemVerilogParser.TaskBodyDeclarationPortListContext::class,
            """
                task t(logic x);
                endtask
            """.trimIndent(),
            "Task(t, [SvValueParameter(x, SimpleDescriptor(Boolean), 0)], 0)"
        ) { it.findDeclaration("t") }
    }

    @Test
    fun `cast task from taskPrototype`() {
        driveCasterTest(
            SystemVerilogParser.TaskPrototypeContext::class,
            """
                class c;
                    extern task t(logic x);
                endclass
            """.trimIndent(),
            "Task(t, [SvValueParameter(x, SimpleDescriptor(Boolean), 0)], 0)"
        ) { it.findDeclaration("t") }
    }
}
