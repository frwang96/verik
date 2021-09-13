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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.core.common.Core

object BasicClassInterpreter {

    fun interpretBasicClass(basicClass: EKtBasicClass, referenceUpdater: ReferenceUpdater) {
        val primaryConstructor = basicClass.primaryConstructor
        val members = if (primaryConstructor != null) {
            val function = interpretConstructor(primaryConstructor, referenceUpdater)
            listOf(function) + basicClass.members
        } else {
            basicClass.members
        }
        referenceUpdater.replace(
            basicClass,
            ESvBasicClass(
                basicClass.location,
                basicClass.name,
                basicClass.supertype,
                basicClass.typeParameters,
                ArrayList(members)
            )
        )
    }

    private fun interpretConstructor(
        primaryConstructor: EPrimaryConstructor,
        referenceUpdater: ReferenceUpdater
    ): ESvFunction {
        val location = primaryConstructor.location
        val type = primaryConstructor.type
        val property = ESvProperty(
            location,
            "<tmp>",
            type.copy(),
            ESvCallExpression(location, type.copy(), Core.Sv.F_NEW, null, arrayListOf(), false)
        )
        val statements = listOf(
            property,
            EReturnStatement(
                location,
                Core.Kt.C_NOTHING.toType(),
                ESvReferenceExpression(location, type.copy(), property, null, false)
            )
        )
        val function = ESvFunction(
            location,
            "vknew",
            type.copy(),
            ESvBlockExpression(location, ArrayList(statements), false, null),
            true,
            arrayListOf()
        )
        referenceUpdater.update(primaryConstructor, function)
        return function
    }
}
