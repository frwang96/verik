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

package io.verik.compiler.core.lang.vk

import io.verik.compiler.core.common.C
import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

object CoreVkClass : CoreScope(CorePackage.VK) {

    val UBIT = CoreClassDeclaration(parent, "Ubit", C.Kt.ANY)
    val SBIT = CoreClassDeclaration(parent, "Sbit", C.Kt.ANY)
    val CLASS = CoreClassDeclaration(parent, "Class", C.Kt.ANY)
    val EVENT = CoreClassDeclaration(parent, "Event", C.Kt.ANY)
    val COMPONENT = CoreClassDeclaration(parent, "Component", C.Kt.ANY)
    val MODULE = CoreClassDeclaration(parent, "Module", COMPONENT)
}
