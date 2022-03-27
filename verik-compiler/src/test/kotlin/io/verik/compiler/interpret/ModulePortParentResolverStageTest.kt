/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.test.BaseTest
import io.verik.compiler.test.findDeclaration
import org.junit.jupiter.api.Test

internal class ModulePortParentResolverStageTest : BaseTest() {

    @Test
    fun `module port resolve parent`() {
        driveElementTest(
            """
                class MP : ModulePort()
                class MI : ModuleInterface() {
                    @Make
                    val mp = MP()
                }
            """.trimIndent(),
            ModulePortParentResolverStage::class,
            "ModulePort(MP, MP, [], MI)"
        ) { it.findDeclaration("MP") }
    }

    @Test
    fun `module port resolve parent illegal out of context`() {
        driveMessageTest(
            """
                class MP : ModulePort()
                class M : Module() {
                    @Make
                    val mp = MP()
                }
            """.trimIndent(),
            true,
            "Module port instantiation out of context"
        )
    }

    @Test
    fun `module port resolve parent illegal multiple parents`() {
        driveMessageTest(
            """
                class MP : ModulePort()
                class MI0 : ModuleInterface() {
                    @Make
                    val mp = MP()
                }
                class MI1 : ModuleInterface() {
                    @Make
                    val mp = MP()
                }
            """.trimIndent(),
            true,
            "Module port has multiple parent module interfaces: MI0"
        )
    }
}
