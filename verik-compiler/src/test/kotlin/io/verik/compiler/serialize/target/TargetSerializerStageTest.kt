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

package io.verik.compiler.serialize.target

import io.verik.compiler.serialize.source.SourceSerializerStage
import io.verik.compiler.util.BaseTest
import org.junit.jupiter.api.Test

internal class TargetSerializerStageTest : BaseTest() {

    @Test
    fun `target class`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                val a: ArrayList<Boolean> = nc()
            """.trimIndent()
        )
        val expected = """
            package verik_pkg;
            
                class ArrayList #(type E = int);
            
                    E queue [${'$'}];
            
                endclass : ArrayList
            
            endpackage : verik_pkg
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.targetPackageTextFile!!
        )
    }

    @Test
    fun `target function`() {
        val projectContext = driveTest(
            SourceSerializerStage::class,
            """
                val a: ArrayList<Boolean> = nc()
                fun f() {
                    a.add(false)
                }
            """.trimIndent()
        )
        val expected = """
            package verik_pkg;
            
                class ArrayList #(type E = int);
            
                    E queue [${'$'}];
            
                    function automatic void add(E e);
                        queue.push_back(e);
                    endfunction : add
            
                endclass : ArrayList
            
            endpackage : verik_pkg
        """.trimIndent()
        assertOutputTextEquals(
            expected,
            projectContext.outputContext.targetPackageTextFile!!
        )
    }
}