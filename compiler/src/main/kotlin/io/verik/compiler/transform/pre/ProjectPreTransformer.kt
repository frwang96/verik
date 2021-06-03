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

package io.verik.compiler.transform.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import io.verik.compiler.normalize.ProjectNormalizationChecker

object ProjectPreTransformer {

    fun transform(projectContext: ProjectContext) {
        m.info("Pre-transform: Rename keyword conflicts", null)
        KeywordTransformer.transform(projectContext)
        m.flush()
        ProjectNormalizationChecker.check(projectContext)

        m.info("Pre-transform: Separate nested classes", null)
        NestedClassTransformer.transform(projectContext)
        m.flush()
        ProjectNormalizationChecker.check(projectContext)
    }
}