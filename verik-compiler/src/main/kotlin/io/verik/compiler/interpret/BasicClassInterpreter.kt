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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.core.common.Core

object BasicClassInterpreter {

    fun interpretBasicClass(basicClass: EKtBasicClass, referenceUpdater: ReferenceUpdater) {
        val constructor = interpretConstructor(basicClass)
        val members = listOf(constructor) + basicClass.members
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

    private fun interpretConstructor(basicClass: EKtBasicClass): ESvFunction {
        val property = ESvProperty(
            basicClass.location,
            "<tmp>",
            Core.Kt.C_INT.toType(),
            EConstantExpression(basicClass.location, Core.Kt.C_INT.toType(), "0")
        )
        val statements = listOf(
            property,
            EReturnStatement(
                basicClass.location,
                Core.Kt.C_NOTHING.toType(),
                ESvReferenceExpression(basicClass.location, Core.Kt.C_INT.toType(), property, null, false)
            )
        )
        return ESvFunction(
            basicClass.location,
            "vknew",
            Core.Kt.C_INT.toType(),
            ESvBlockExpression(basicClass.location, ArrayList(statements), false, null),
            true,
            arrayListOf()
        )
    }
}
