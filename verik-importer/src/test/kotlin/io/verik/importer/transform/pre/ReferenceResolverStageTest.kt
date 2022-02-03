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

package io.verik.importer.transform.pre

import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ReferenceResolverStageTest : BaseTest() {

    @Test
    fun `resolve reference class`() {
        driveElementTest(
            """
                class c;
                endclass
                c x;
            """.trimIndent(),
            ReferenceResolverStage::class,
            "Property(x, ReferenceDescriptor(Nothing, c, c, []))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve reference type alias`() {
        driveElementTest(
            """
                typedef logic t;
                t x;
            """.trimIndent(),
            ReferenceResolverStage::class,
            "Property(x, ReferenceDescriptor(Nothing, t, t, []))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve reference type parameter`() {
        driveElementTest(
            """
                class c #(type T);
                    T x;
                endclass
            """.trimIndent(),
            ReferenceResolverStage::class,
            "Property(x, ReferenceDescriptor(Nothing, T, T, []))"
        ) { it.findDeclaration("x") }
    }
}
