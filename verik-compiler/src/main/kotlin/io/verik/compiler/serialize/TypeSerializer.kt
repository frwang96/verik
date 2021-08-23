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

package io.verik.compiler.serialize

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.property.Type
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.m
import org.jetbrains.kotlin.utils.addToStdlib.cast

object TypeSerializer {

    fun serialize(type: Type, element: EElement): String {
        return when (val reference = type.reference) {
            is EEnum -> {
                // TODO more general handling of scope resolution
                val file = reference.parent.cast<EFile>()
                val basicPackage = file.parent.cast<EBasicPackage>()
                "${basicPackage.name}::${reference.name}"
            }
            Core.Kt.UNIT -> "void"
            Core.Kt.INT -> "int"
            Core.Kt.BOOLEAN -> "logic"
            Core.Kt.STRING -> "string"
            Core.Vk.UBIT -> "logic [${type.asBitWidth(element) - 1}:0]"
            else -> {
                m.error("Unable to serialize type: $type", element)
                "void"
            }
        }
    }
}
