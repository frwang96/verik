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

package verik.core.main.config

import kotlinx.serialization.Serializable

@Serializable
data class YamlProjectConfig(
        val project: String,
        val buildDir: String? = null,
        val gradleDir: String? = null,
        val src: YamlSourceConfig? = null,
        val compile: YamlCompileConfig? = null,
        val stubs: YamlStubsConfig? = null
)

@Serializable
data class YamlSourceConfig(
        val root: String? = null,
        val pkgs: List<String>? = null
)

@Serializable
data class YamlCompileConfig(
        val top: String? = null,
        val scope: String? = null,
        val labelLines: Boolean? = null
)

@Serializable
data class YamlStubsConfig(
        val main: String? = null,
        val jar: String? = null
)