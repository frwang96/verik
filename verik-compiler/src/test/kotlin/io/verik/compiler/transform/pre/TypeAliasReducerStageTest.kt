/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TypeAliasReducerStageTest : BaseTest() {

    @Test
    fun `reduce type alias simple`() {
        driveElementTest(
            """
                typealias U = Ubit<`8`>
                var x: U = nc()
            """.trimIndent(),
            TypeAliasReducerStage::class,
            "Property(x, Ubit<`8`>, *, 1, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `reduce type alias nested`() {
        driveElementTest(
            """
                typealias X = `8`
                typealias Y = X
                var x: Ubit<Y> = nc()
            """.trimIndent(),
            TypeAliasReducerStage::class,
            "Property(x, Ubit<`8`>, *, 1, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `reduce type alias with type parameter`() {
        driveElementTest(
            """
                typealias X<Y> = INC<Y>
                var x: Ubit<X<`1`>> = nc()
            """.trimIndent(),
            TypeAliasReducerStage::class,
            "Property(x, Ubit<INC<`1`>>, *, 1, 0)"
        ) { it.findDeclaration("x") }
    }
}
