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

package io.verik.importer.interpret

import io.verik.importer.ast.kt.element.KtClass
import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtProperty
import io.verik.importer.ast.kt.element.KtValueParameter
import io.verik.importer.ast.sv.element.declaration.SvClass
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.declaration.SvModule
import io.verik.importer.ast.sv.element.declaration.SvPackage
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.core.Core
import io.verik.importer.message.Messages

object DeclarationInterpreter {

    fun interpretDeclaration(declaration: SvDeclaration): KtDeclaration? {
        return when (declaration) {
            is SvPackage -> null
            is SvClass -> interpretClassFromClass(declaration)
            is SvModule -> interpretClassFromModule(declaration)
            is SvProperty -> interpretPropertyFromProperty(declaration)
            else -> {
                Messages.INTERNAL_ERROR.on(
                    declaration,
                    "Unexpected declaration type: ${declaration::class.simpleName}"
                )
            }
        }
    }

    private fun interpretClassFromClass(`class`: SvClass): KtClass {
        val declarations = `class`.declarations.mapNotNull { interpretDeclaration(it) }
        return KtClass(
            `class`.location,
            `class`.name,
            `class`.signature,
            Core.C_Any.toType(),
            ArrayList(),
            ArrayList(declarations)
        )
    }

    private fun interpretClassFromModule(module: SvModule): KtClass {
        val valueParameters = module.ports.map { interpretValueParameterFromPort(it) }
        val declarations = module.declarations.mapNotNull { interpretDeclaration(it) }
        return KtClass(
            module.location,
            module.name,
            module.signature,
            Core.C_Module.toType(),
            ArrayList(valueParameters),
            ArrayList(declarations)
        )
    }

    private fun interpretPropertyFromProperty(property: SvProperty): KtProperty {
        return KtProperty(
            property.location,
            property.name,
            property.signature,
            property.type
        )
    }

    private fun interpretValueParameterFromPort(port: SvPort): KtValueParameter {
        val annotationEntry = port.portType.getAnnotationEntry()
        return KtValueParameter(
            port.location,
            port.name,
            port.type,
            listOf(annotationEntry),
            true
        )
    }
}
