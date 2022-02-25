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

import io.verik.importer.cast.common.CasterStage
import io.verik.importer.cast.common.DeclarationCounterStage
import io.verik.importer.check.UninterpretedDeclarationCheckerStage
import io.verik.importer.interpret.ClassInterpreterStage
import io.verik.importer.interpret.CompanionObjectInterpreterStage
import io.verik.importer.interpret.FunctionInterpreterStage
import io.verik.importer.interpret.PackageInterpreterStage
import io.verik.importer.parse.ParserStage
import io.verik.importer.preprocess.PreprocessorFilterStage
import io.verik.importer.preprocess.PreprocessorSerializerStage
import io.verik.importer.preprocess.PreprocessorStage
import io.verik.importer.serialize.general.ReportFileSerializerStage
import io.verik.importer.serialize.source.SourceSerializerStage
import io.verik.importer.transform.post.OverrideTransformerStage
import io.verik.importer.transform.post.SuperDescriptorTransformerStage
import io.verik.importer.transform.post.UnresolvedDeclarationEliminatorStage
import io.verik.importer.transform.pre.CardinalTypeSimplifierStage
import io.verik.importer.transform.pre.DescriptorResolverStage
import io.verik.importer.transform.pre.LocalTypeAliasEliminatorStage
import io.verik.importer.transform.pre.ReferenceResolverStage

object StageSequencer {

    fun getStageSequence(): StageSequence {
        val stageSequence = StageSequence()

        stageSequence.add(StageType.PREPROCESS, PreprocessorStage)
        stageSequence.add(StageType.PREPROCESS, PreprocessorSerializerStage)
        stageSequence.add(StageType.PREPROCESS, PreprocessorFilterStage)

        stageSequence.add(StageType.PARSE, ParserStage)

        stageSequence.add(StageType.CAST, CasterStage)
        stageSequence.add(StageType.CAST, DeclarationCounterStage)

        stageSequence.add(StageType.PRE_TRANSFORM, ReferenceResolverStage)
        stageSequence.add(StageType.PRE_TRANSFORM, LocalTypeAliasEliminatorStage)
        stageSequence.add(StageType.PRE_TRANSFORM, DescriptorResolverStage)
        stageSequence.add(StageType.PRE_TRANSFORM, CardinalTypeSimplifierStage)

        stageSequence.add(StageType.INTERPRET, PackageInterpreterStage)
        stageSequence.add(StageType.INTERPRET, ClassInterpreterStage)
        stageSequence.add(StageType.INTERPRET, CompanionObjectInterpreterStage)
        stageSequence.add(StageType.INTERPRET, FunctionInterpreterStage)

        stageSequence.add(StageType.POST_TRANSFORM, UnresolvedDeclarationEliminatorStage)
        stageSequence.add(StageType.POST_TRANSFORM, SuperDescriptorTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, OverrideTransformerStage)

        stageSequence.add(StageType.CHECK, UninterpretedDeclarationCheckerStage)

        stageSequence.add(StageType.SERIALIZE, ReportFileSerializerStage)
        stageSequence.add(StageType.SERIALIZE, SourceSerializerStage)

        return stageSequence
    }
}
