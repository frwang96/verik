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
