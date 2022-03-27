/*
 * SPDX-License-Identifier: Apache-2.0
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
            "Property(x, ReferenceDescriptor(c<`1`>, c, c, [TypeArgument(null, LiteralDescriptor(`1`, 1))]), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve bit descriptor`() {
        driveElementTest(
            """
                logic [1:0] x;
            """.trimIndent(),
            DescriptorResolverStage::class,
            "Property(x, BitDescriptor(Ubit<ADD<SUB<`1`, `0`>, `1`>>, *, *, *), 0, 1)"
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
                Property(x, ReferenceDescriptor(c<Int>, c, c, [TypeArgument(null, SimpleDescriptor(Int))]), 0, 1)
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve reference descriptor default arguments`() {
        driveElementTest(
            """
                class c #(type T = int, type U = T);
                endclass
                c x;
            """.trimIndent(),
            DescriptorResolverStage::class,
            """
                Property(x, ReferenceDescriptor(c<Int, Int>, c, c, []), 0, 1)
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve array dimension descriptor`() {
        driveElementTest(
            """
                logic x [$];
            """.trimIndent(),
            DescriptorResolverStage::class,
            "Property(x, ArrayDimensionDescriptor(Queue<Boolean>, SimpleDescriptor(*), 1), 0, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `resolve index dimension descriptor`() {
        driveElementTest(
            """
                logic x [int];
            """.trimIndent(),
            DescriptorResolverStage::class,
            """
                Property(
                    x,
                    IndexDimensionDescriptor(AssociativeArray<Int, Boolean>, SimpleDescriptor(*), SimpleDescriptor(*)),
                    0, 1
                )
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
                    x, RangeDimensionDescriptor(
                        Packed<ADD<SUB<`3`, `0`>, `1`>, Ubit<ADD<SUB<`1`, `0`>, `1`>>>, *, *, *, 1
                    ), 0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }
}
