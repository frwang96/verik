/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

import io.verik.compiler.message.MessageCollector
import io.verik.compiler.normalize.NormalizationChecker
import kotlin.reflect.KClass

/**
 * Stage sequence that drives the execution of each of the compiler [stages][ProjectStage].
 */
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
                processStage(projectContext, stageType, it)
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
                processStage(projectContext, stageType, it)
                if (it::class == stageClass)
                    return
            }
        }
    }

    fun processUntil(projectContext: ProjectContext, stageType: StageType) {
        StageType.values().forEach { currentStageType ->
            stages[currentStageType]!!.forEach {
                processStage(projectContext, currentStageType, it)
            }
            if (currentStageType == stageType)
                return
        }
    }

    private fun <S : ProjectStage> contains(stageClass: KClass<S>): Boolean {
        return StageType.values().any { stageType ->
            stages[stageType]!!.any { it::class == stageClass }
        }
    }

    private fun processStage(projectContext: ProjectContext, stageType: StageType, stage: ProjectStage) {
        if (stage !in projectContext.processedProjectStages) {
            stage.process(projectContext)
            if (stageType.checkNormalization() && projectContext.config.debug) {
                NormalizationChecker.check(projectContext, stage)
            }
            projectContext.processedProjectStages.add(stage)
        }
    }
}
