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

package io.verik.importer.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.antlr.SystemVerilogParserBaseVisitor
import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.ast.element.EElement
import io.verik.importer.message.Messages
import org.antlr.v4.runtime.tree.ParseTree

class CasterVisitor(
    private val castContext: CastContext
) : SystemVerilogParserBaseVisitor<EElement>() {

    fun getDeclaration(declaration: ParseTree): EDeclaration? {
        if (CasterUtil.hasErrorNode(declaration))
            return null
        return when (val element = declaration.accept(this)) {
            null -> null
            is EDeclaration -> element
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Declaration expected but got: ${element::class.simpleName}")
            }
        }
    }

    override fun visitModuleDeclaration(ctx: SystemVerilogParser.ModuleDeclarationContext?): EElement? {
        return ModuleCaster.castModule(ctx!!, castContext)
    }

    override fun visitDataDeclaration(ctx: SystemVerilogParser.DataDeclarationContext?): EElement? {
        return PropertyCaster.castProperty(ctx!!, castContext)
    }
}
