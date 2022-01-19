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
import io.verik.importer.ast.sv.element.SvDeclaration
import io.verik.importer.ast.sv.element.SvModule
import io.verik.importer.ast.sv.element.SvPackage
import io.verik.importer.ast.sv.element.SvPort
import io.verik.importer.ast.sv.element.SvProperty
import io.verik.importer.core.Core
import io.verik.importer.message.Messages

object DeclarationInterpreter {

    fun interpretDeclaration(declaration: SvDeclaration): KtDeclaration? {
        return when (declaration) {
            is SvPackage -> null
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

    private fun interpretClassFromModule(module: SvModule): KtClass {
        val valueParameters = module.ports.map { interpretValueParameterFromPort(it) }
        return KtClass(
            module.location,
            module.name,
            Core.C_Module.toType(),
            ArrayList(valueParameters),
            ArrayList()
        )
    }

    private fun interpretPropertyFromProperty(property: SvProperty): KtProperty {
        return KtProperty(property.location, property.name, property.type)
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
