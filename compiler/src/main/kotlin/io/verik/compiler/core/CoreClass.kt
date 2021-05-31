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

package io.verik.compiler.core

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.descriptor.ClassDescriptor

object CoreClass {

    val ANY = ClassDescriptor(Name("Any"), CorePackage.KOTLIN.resolve("Any"), null)
    val UNIT = ClassDescriptor(Name("Unit"), CorePackage.KOTLIN.resolve("Unit"), ANY)
    val BOOLEAN = ClassDescriptor(Name("Boolean"), CorePackage.KOTLIN.resolve("Boolean"), ANY)
    val MODULE = ClassDescriptor(Name("Module"), CorePackage.CORE.resolve("Module"), ANY)
}