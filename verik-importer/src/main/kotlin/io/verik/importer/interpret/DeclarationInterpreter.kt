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
import io.verik.importer.ast.kt.element.KtConstructor
import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtEnum
import io.verik.importer.ast.kt.element.KtEnumEntry
import io.verik.importer.ast.kt.element.KtFunction
import io.verik.importer.ast.kt.element.KtProperty
import io.verik.importer.ast.kt.element.KtTypeAlias
import io.verik.importer.ast.kt.element.KtValueParameter
import io.verik.importer.ast.kt.property.AnnotationEntry
import io.verik.importer.ast.sv.element.declaration.SvClass
import io.verik.importer.ast.sv.element.declaration.SvConstructor
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.declaration.SvEnum
import io.verik.importer.ast.sv.element.declaration.SvEnumEntry
import io.verik.importer.ast.sv.element.declaration.SvFunction
import io.verik.importer.ast.sv.element.declaration.SvModule
import io.verik.importer.ast.sv.element.declaration.SvPackage
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.ast.sv.element.declaration.SvStruct
import io.verik.importer.ast.sv.element.declaration.SvTask
import io.verik.importer.ast.sv.element.declaration.SvTypeAlias
import io.verik.importer.ast.sv.element.declaration.SvValueParameter
import io.verik.importer.core.Core
import io.verik.importer.message.Messages

object DeclarationInterpreter {

    fun interpretDeclaration(declaration: SvDeclaration): KtDeclaration? {
        return when (declaration) {
            is SvPackage -> null
            is SvClass -> interpretClassFromClass(declaration)
            is SvModule -> interpretClassFromModule(declaration)
            is SvStruct -> interpretClassFromStruct(declaration)
            is SvEnum -> interpretEnumFromEnum(declaration)
            is SvTypeAlias -> interpretTypeAliasFromTypeAlias(declaration)
            is SvFunction -> interpretFunctionFromFunction(declaration)
            is SvTask -> interpretFunctionFromTask(declaration)
            is SvConstructor -> interpretConstructorFromConstructor(declaration)
            is SvProperty -> interpretPropertyFromProperty(declaration)
            else -> {
                Messages.INTERNAL_ERROR.on(
                    declaration,
                    "Unable to interpret declaration: ${declaration::class.simpleName}"
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
            `class`.superType,
            listOf(),
            declarations,
            true
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
            valueParameters,
            declarations,
            false
        )
    }

    private fun interpretClassFromStruct(struct: SvStruct): KtClass {
        val valueParameters = struct.properties.map { interpretValueParameterFromProperty(it) }
        return KtClass(
            struct.location,
            struct.name,
            struct.signature,
            Core.C_Struct.toType(),
            valueParameters,
            listOf(),
            false
        )
    }

    private fun interpretEnumFromEnum(enum: SvEnum): KtEnum {
        val entries = enum.entries.map { interpretEnumEntryFromEnumEntry(it) }
        return KtEnum(
            enum.location,
            enum.name,
            enum.signature,
            entries
        )
    }

    private fun interpretTypeAliasFromTypeAlias(typeAlias: SvTypeAlias): KtTypeAlias {
        return KtTypeAlias(
            typeAlias.location,
            typeAlias.name,
            typeAlias.signature,
            typeAlias.type
        )
    }

    private fun interpretFunctionFromFunction(function: SvFunction): KtFunction {
        val valueParameters = function.valueParameters.map { interpretValueParameterFromValueParameter(it) }
        val isOpen = function.parent is SvClass
        return KtFunction(
            function.location,
            function.name,
            function.signature,
            function.type,
            listOf(),
            valueParameters,
            isOpen
        )
    }

    private fun interpretFunctionFromTask(task: SvTask): KtFunction {
        val valueParameters = task.valueParameters.map { interpretValueParameterFromValueParameter(it) }
        val isOpen = task.parent is SvClass
        return KtFunction(
            task.location,
            task.name,
            task.signature,
            task.type,
            listOf(AnnotationEntry("Task")),
            valueParameters,
            isOpen
        )
    }

    private fun interpretConstructorFromConstructor(constructor: SvConstructor): KtConstructor {
        val valueParameters = constructor.valueParameters.map { interpretValueParameterFromValueParameter(it) }
        return KtConstructor(
            constructor.location,
            constructor.signature,
            valueParameters
        )
    }

    private fun interpretPropertyFromProperty(property: SvProperty): KtProperty {
        return KtProperty(
            property.location,
            property.name,
            property.signature,
            property.type,
            property.isMutable
        )
    }

    private fun interpretValueParameterFromProperty(property: SvProperty): KtValueParameter {
        return KtValueParameter(
            property.location,
            property.name,
            property.type,
            listOf(),
            property.isMutable
        )
    }

    private fun interpretValueParameterFromValueParameter(valueParameter: SvValueParameter): KtValueParameter {
        return KtValueParameter(
            valueParameter.location,
            valueParameter.name,
            valueParameter.type,
            listOf(),
            null
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

    private fun interpretEnumEntryFromEnumEntry(enumEntry: SvEnumEntry): KtEnumEntry {
        return KtEnumEntry(
            enumEntry.location,
            enumEntry.name
        )
    }
}
