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

package io.verik.importer.main

import io.verik.importer.message.MessageCollector
import kotlin.reflect.KClass

class StageSequence {

    @Suppress("MemberVisibilityCanBePrivate")
    val stages = HashMap<StageType, ArrayList<ProjectStage>>()

    init {
        StageType.values().forEach {
            stages[it] = ArrayList()
        }
    }

    fun add(stageType: StageType, stage: ProjectStage) {
        if (contains(stage::class)) {
            val stageName = stage::class.simpleName
            throw IllegalArgumentException("Stage has already been added to the stage sequence: $stageName")
        }
        stages[stageType]!!.add(stage)
    }

    fun processAll(projectContext: ProjectContext) {
        StageType.values().forEach { stageType ->
            stages[stageType]!!.forEach {
                processStage(projectContext, it)
            }
            if (stageType.flushAfter()) {
                MessageCollector.messageCollector.flush()
            }
        }
    }

    fun <S : ProjectStage> processUntil(projectContext: ProjectContext, stageClass: KClass<S>) {
        assert(contains(stageClass))
        StageType.values().forEach { stageType ->
            stages[stageType]!!.forEach {
                processStage(projectContext, it)
                if (it::class == stageClass)
                    return
            }
        }
    }

    private fun <S : ProjectStage> contains(stageClass: KClass<S>): Boolean {
        return StageType.values().any { stageType ->
            stages[stageType]!!.any { it::class == stageClass }
        }
    }

    private fun processStage(projectContext: ProjectContext, stage: ProjectStage) {
        if (stage !in projectContext.processedProjectStages) {
            stage.process(projectContext)
            projectContext.processedProjectStages.add(stage)
        }
    }
}
