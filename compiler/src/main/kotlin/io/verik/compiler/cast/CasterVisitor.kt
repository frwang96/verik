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

package io.verik.compiler.cast

import io.verik.compiler.ast.common.FunctionAnnotationType
import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.PackageName
import io.verik.compiler.ast.common.SourceSetType
import io.verik.compiler.ast.element.*
import io.verik.compiler.common.CastUtil
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.getMessageLocation
import io.verik.compiler.main.messageCollector
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Paths

class CasterVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
): KtVisitor<VkElement, Unit>() {

    private val mainPath = projectContext.config.projectDir.resolve("src/main/kotlin")
    private val testPath = projectContext.config.projectDir.resolve("src/test/kotlin")
    private val bindingContext = projectContext.bindingContext

    override fun visitKtFile(file: KtFile, data: Unit?): VkElement? {
        val location = file.getMessageLocation()
        val inputPath = Paths.get(file.virtualFilePath)
        val (sourceSetType, relativePath) = when {
            inputPath.startsWith(mainPath) -> Pair(SourceSetType.MAIN, mainPath.relativize(inputPath))
            inputPath.startsWith(testPath) -> Pair(SourceSetType.TEST, testPath.relativize(inputPath))
            else -> {
                messageCollector.error("Unable to identify as main or test source", file)
                return null
            }
        }
        val packageName = PackageName(file.packageFqName.toString())
        val declarations = file.declarations.mapNotNull {
            CastUtil.cast<VkDeclaration>(it.accept(this, Unit))
        }
        val importDirectives = file.importDirectives.mapNotNull {
            CastUtil.cast<VkImportDirective>(it.accept(this, Unit))
        }

        return VkFile(
            location,
            inputPath,
            relativePath,
            sourceSetType,
            packageName,
            ArrayList(declarations),
            importDirectives
        )
    }

    override fun visitImportDirective(importDirective: KtImportDirective, data: Unit?): VkElement {
        val location = importDirective.getMessageLocation()
        val (name, packageName) = if (importDirective.isAllUnder) {
            Pair(null, PackageName(importDirective.importedFqName!!.toString()))
        } else {
            Pair(
                Name(importDirective.importedName!!.toString()),
                PackageName(importDirective.importedFqName!!.parent().toString())
            )
        }
        return VkImportDirective(location, name, packageName)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): VkElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.CLASS)[classOrObject]!!
        val name = Name(descriptor.name.toString())
        val type = TypeCaster.castType(declarationMap, descriptor.defaultType, classOrObject)
        val body = classOrObject.body
        val declarations = body?.declarations?.mapNotNull {
            CastUtil.cast<VkDeclaration>(it.accept(this, Unit))
        } ?: listOf()

        return VkBaseClass(
            classOrObject.getMessageLocation(),
            name,
            type,
            ArrayList(declarations)
        )
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): VkElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val name = Name(descriptor.name.toString())
        val annotationTypes = descriptor.annotations.mapNotNull {
            FunctionAnnotationType(it.fqName, function)
        }
        val type = TypeCaster.castType(declarationMap, descriptor.returnType!!, function)
        val annotationType = when (annotationTypes.size) {
            0 -> null
            1 -> annotationTypes.first()
            else -> {
                messageCollector.error("Conflicting annotations: ${annotationTypes.joinToString()}", function)
                null
            }
        }
        return VkBaseFunction(
            function.getMessageLocation(),
            name,
            type,
            annotationType
        )
    }

    override fun visitProperty(property: KtProperty, data: Unit?): VkElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val name = Name(descriptor.name.toString())
        val typeReference = property.typeReference
        val type = if (typeReference != null) {
            TypeCaster.castType(bindingContext, declarationMap, typeReference)
        } else {
            TypeCaster.castType(declarationMap, descriptor.type, property)
        }
        return VkBaseProperty(property.getMessageLocation(), name, type)
    }
}