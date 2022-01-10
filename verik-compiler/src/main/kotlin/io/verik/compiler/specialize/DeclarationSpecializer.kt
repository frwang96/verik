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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.core.common.CardinalConstantDeclaration

object DeclarationSpecializer {

    fun specialize(
        typeParameterBinding: TypeParameterBinding,
        specializeContext: SpecializeContext,
    ): List<TypeParameterBinding> {
        val declaration = SpecializerCopier.copy(
            typeParameterBinding.declaration,
            typeParameterBinding.typeArguments,
            specializeContext
        )
        TypeParameterSubstitutor.substitute(typeParameterBinding, declaration)
        if (declaration is TypeParameterized && declaration.typeParameters.isNotEmpty()) {
            val typeParameterStrings = declaration.typeParameters.map { getTypeParameterString(it) }
            declaration.name = "${declaration.name}_${typeParameterStrings.joinToString(separator = "_")}"
        }
        CardinalTypeEvaluator.evaluate(declaration)
        OptionalReducer.reduce(declaration)
        return SpecializerIndexer.index(declaration)
    }

    private fun getTypeParameterString(typeParameter: ETypeParameter): String {
        val reference = typeParameter.type.reference
        return if (reference is CardinalConstantDeclaration) {
            "${typeParameter.name}_${reference.value}"
        } else {
            "${typeParameter.name}_${reference.name}"
        }
    }
}
