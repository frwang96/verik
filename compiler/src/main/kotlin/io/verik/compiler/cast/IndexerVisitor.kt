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

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.VkBaseClass
import io.verik.compiler.ast.element.VkBaseFunction
import io.verik.compiler.ast.element.VkBaseProperty
import io.verik.compiler.ast.element.VkTypeParameter
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.getMessageLocation
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class IndexerVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
): KtTreeVisitorVoid() {

    private val bindingContext = projectContext.bindingContext

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        val descriptor = bindingContext.getSliceContents(BindingContext.CLASS)[classOrObject]!!
        val location = classOrObject.getMessageLocation()
        val name = Name(classOrObject.name!!)
        val baseClass = VkBaseClass(location, name, Type.NULL, arrayListOf())
        declarationMap[descriptor] = baseClass
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val descriptor = bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val location = function.getMessageLocation()
        val name = Name(function.name!!)
        val baseFunction = VkBaseFunction(location, name, Type.NULL, null)
        declarationMap[descriptor] = baseFunction
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        val descriptor = bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val location = property.getMessageLocation()
        val name = Name(property.name!!)
        val baseProperty = VkBaseProperty(location, name, Type.NULL)
        declarationMap[descriptor] = baseProperty
    }

    override fun visitTypeParameter(parameter: KtTypeParameter) {
        super.visitTypeParameter(parameter)
        val descriptor = bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)[parameter]!!
        val location = parameter.getMessageLocation()
        val name = Name(parameter.name!!)
        val typeParameter = VkTypeParameter(location, name, Type.NULL)
        declarationMap[descriptor] = typeParameter
    }
}