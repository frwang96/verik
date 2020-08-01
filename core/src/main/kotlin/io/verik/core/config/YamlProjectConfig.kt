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

package io.verik.core.config

import kotlinx.serialization.Serializable

@Serializable
data class YamlProjectConfig(
        val project: String,
        val buildDir: String? = null,
        val srcRoot: String? = null,
        val srcPkgs: List<String>? = null,
        val top: String? = null,
        val labelLines: Boolean? = null,
        val gradle: YamlGradleConfig? = null,
        val stubsMain: String? = null
)

@Serializable
data class YamlGradleConfig(
        val wrapper: String? = null,
        val jar: String? = null
)
