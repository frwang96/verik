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
