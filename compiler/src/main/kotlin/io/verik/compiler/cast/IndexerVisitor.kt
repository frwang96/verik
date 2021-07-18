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

import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.property.Name
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.location
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class IndexerVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
) : KtTreeVisitorVoid() {

    private val bindingContext = projectContext.bindingContext
    private val nameRegex = Regex("[_a-zA-Z][_a-zA-Z0-9]*")

    private fun checkDeclarationName(name: Name, element: KtElement) {
        if (!name.name.matches(nameRegex))
            m.error("Illegal name: $name", element)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        val descriptor = bindingContext.getSliceContents(BindingContext.CLASS)[classOrObject]!!
        val location = classOrObject.location()
        val name = Name(classOrObject.name!!)
        checkDeclarationName(name, classOrObject)
        val basicClass = EKtBasicClass(location, name, Type.NULL, arrayListOf(), arrayListOf())
        declarationMap[descriptor] = basicClass
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val descriptor = bindingContext.getSliceContents(BindingContext.FUNCTION)[function]!!
        val location = function.location()
        val name = Name(function.name!!)
        checkDeclarationName(name, function)
        val ktFunction = EKtFunction(location, name, Type.NULL, null, null)
        declarationMap[descriptor] = ktFunction
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        val descriptor = bindingContext.getSliceContents(BindingContext.VARIABLE)[property]!!
        val location = property.location()
        val name = Name(property.name!!)
        checkDeclarationName(name, property)
        val ktProperty = EKtProperty(location, name, Type.NULL, null)
        declarationMap[descriptor] = ktProperty
    }

    override fun visitTypeParameter(parameter: KtTypeParameter) {
        super.visitTypeParameter(parameter)
        val descriptor = bindingContext.getSliceContents(BindingContext.TYPE_PARAMETER)[parameter]!!
        val location = parameter.location()
        val name = Name(parameter.name!!)
        checkDeclarationName(name, parameter)
        val typeParameter = ETypeParameter(location, name, Type.NULL)
        declarationMap[descriptor] = typeParameter
    }
}