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

package io.verik.compiler.common

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import kotlin.reflect.KClass

class StageSequence {

    val stages = ArrayList<ProjectStage>()

    fun add(stage: ProjectStage) {
        if (contains(stage::class))
            m.fatal("Stage has already been added to stage sequence: ${stage::class.simpleName}", null)
        stages.add(stage)
    }

    fun <T: ProjectStage> contains(stageClass: KClass<T>): Boolean {
        return stages.any { it::class == stageClass }
    }

    fun process(projectContext: ProjectContext) {
        stages.forEach {
            it.accept(projectContext)
        }
    }
}