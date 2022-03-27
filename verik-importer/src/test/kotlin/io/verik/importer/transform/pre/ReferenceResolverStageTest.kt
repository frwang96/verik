/*
 * SPDX-License-Identifier: Apache-2.0
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
            "Property(x, ReferenceDescriptor(Nothing, c, c, []), 0, 1)"
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
            "Property(x, ReferenceDescriptor(Nothing, t, t, []), 0, 1)"
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
            "Property(x, ReferenceDescriptor(Nothing, T, T, []), 0, 1)"
        ) { it.findDeclaration("x") }
    }
}
