/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.vkx

import io.verik.core.al.AlRule
import io.verik.core.kt.parseDeclaration
import io.verik.core.kt.resolve.KtExpressionResolver

fun parseComponent(rule: AlRule): VkxComponent {
    return parseDeclaration(rule)
            .also { KtExpressionResolver.resolveDeclaration(it) }
            .let { VkxComponent(it) }
}

fun parsePort(rule: AlRule): VkxPort {
    return parseDeclaration(rule)
            .also { KtExpressionResolver.resolveDeclaration(it) }
            .let { VkxPort(it) }
}

fun parseProperty(rule: AlRule): VkxProperty {
    return parseDeclaration(rule)
            .also { KtExpressionResolver.resolveDeclaration(it) }
            .let { VkxProperty(it) }
}
