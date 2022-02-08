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

internal class DescriptorResolverStageTest : BaseTest() {

    @Test
    fun `resolve literal descriptor`() {
        driveElementTest(
            """
                class c #(N);
                endclass
                c #(1) x;
            """.trimIndent(),
            DescriptorResolverStage::class,
            "Property(x, ReferenceDescriptor(c<`1`>, c, c, [TypeArgument(null, LiteralDescriptor(`1`, 1))]))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve bit descriptor`() {
        driveElementTest(
            """
                logic [1:0] x;
            """.trimIndent(),
            DescriptorResolverStage::class,
            "Property(x, BitDescriptor(Ubit<ADD<SUB<`1`, `0`>, `1`>>, *, *, *))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve reference descriptor`() {
        driveElementTest(
            """
                class c #(type T);
                endclass
                c#(int) x;
            """.trimIndent(),
            DescriptorResolverStage::class,
            """
                Property(x, ReferenceDescriptor(c<Int>, c, c, [TypeArgument(null, SimpleDescriptor(Int))]))
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve range dimension descriptor`() {
        driveElementTest(
            """
                logic [1:0][3:0] x;
            """.trimIndent(),
            DescriptorResolverStage::class,
            """
                Property(
                    x,
                    RangeDimensionDescriptor(Packed<ADD<SUB<`3`, `0`>, `1`>, Ubit<ADD<SUB<`1`, `0`>, `1`>>>, *, *, *, 1)
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve index dimension descriptor`() {
        driveElementTest(
            """
                logic x [int];
            """.trimIndent(),
            DescriptorResolverStage::class,
            "Property(x, IndexDimensionDescriptor(HashMap<Int, Boolean>, SimpleDescriptor(*), SimpleDescriptor(*)))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve queue descriptor`() {
        driveElementTest(
            """
                logic x [$];
            """.trimIndent(),
            DescriptorResolverStage::class,
            "Property(x, QueueDescriptor(ArrayList<Boolean>, SimpleDescriptor(*)))"
        ) { it.findDeclaration("x") }
    }
}
