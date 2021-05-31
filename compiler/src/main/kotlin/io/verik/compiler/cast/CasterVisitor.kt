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
import io.verik.compiler.ast.common.SourceSetType
import io.verik.compiler.ast.descriptor.PackageDescriptor
import io.verik.compiler.ast.element.*
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector
import io.verik.compiler.util.ElementUtil
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Paths

class CasterVisitor(projectContext: ProjectContext): KtVisitor<VkElement, Unit>() {

    private val mainPath = projectContext.config.projectDir.resolve("src/main/kotlin")
    private val testPath = projectContext.config.projectDir.resolve("src/test/kotlin")
    private val bindingContext = projectContext.bindingContext

    override fun visitKtFile(file: KtFile, data: Unit?): VkElement? {
        val location = CasterUtil.getMessageLocation(file)
        val inputPath = Paths.get(file.virtualFilePath)
        val (sourceSetType, relativePath) = when {
            inputPath.startsWith(mainPath) -> Pair(SourceSetType.MAIN, mainPath.relativize(inputPath))
            inputPath.startsWith(testPath) -> Pair(SourceSetType.TEST, testPath.relativize(inputPath))
            else -> {
                messageCollector.error("Unable to identify as main or test source", location)
                return null
            }
        }
        val packageDescriptor = PackageDescriptor(Name(file.packageFqName.toString()))
        val declarations = file.declarations.mapNotNull {
            ElementUtil.cast<VkDeclaration>(it.accept(this, Unit))
        }
        val importDirectives = file.importDirectives.mapNotNull {
            ElementUtil.cast<VkImportDirective>(it.accept(this, Unit))
        }

        return VkFile(
            location,
            inputPath,
            relativePath,
            sourceSetType,
            packageDescriptor,
            ArrayList(declarations),
            importDirectives
        )
    }

    override fun visitImportDirective(importDirective: KtImportDirective, data: Unit?): VkElement {
        val location = CasterUtil.getMessageLocation(importDirective)
        val (name, packageDescriptor) = if (importDirective.isAllUnder) {
            Pair(null, PackageDescriptor(Name(importDirective.importedFqName!!.toString())))
        } else {
            Pair(
                Name(importDirective.importedName!!.toString()),
                PackageDescriptor(Name(importDirective.importedFqName!!.parent().toString()))
            )
        }
        return VkImportDirective(location, name, packageDescriptor)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): VkElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.CLASS)[classOrObject]!!
        val location = CasterUtil.getMessageLocation(classOrObject)
        val name = Name(descriptor.name.identifier)
        val type = CasterUtil.getType(descriptor.defaultType)
        val body = classOrObject.body

        return if (body != null) {
            val declarations = body.declarations.mapNotNull {
                ElementUtil.cast<VkDeclaration>(it.accept(this, Unit))
            }
            VkBaseClass(name, type, location, ArrayList(declarations))
        } else {
            VkBaseClass(name, type, location, arrayListOf())
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction, data: Unit?): VkElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val location = CasterUtil.getMessageLocation(function)
        val name = Name(descriptor.name.identifier)
        val annotationTypes = descriptor.annotations.mapNotNull {
            FunctionAnnotationType(it.fqName, location)
        }
        val annotationType = when (annotationTypes.size) {
            0 -> null
            1 -> annotationTypes.first()
            else -> {
                messageCollector.error("Conflicting annotations: ${annotationTypes.joinToString()}", location)
                null
            }
        }
        return VkBaseFunction(name, CoreClass.UNIT.getDefaultType(), location, annotationType)
    }

    override fun visitProperty(property: KtProperty, data: Unit?): VkElement {
        val descriptor = bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val location = CasterUtil.getMessageLocation(property)
        val name = Name(descriptor.name.identifier)
        val type = CasterUtil.getType(descriptor.type)
        return VkBaseProperty(name, type, location)
    }
}