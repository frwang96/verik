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

import io.verik.importer.cast.CasterStage
import io.verik.importer.interpret.InterpreterStage
import io.verik.importer.parse.ParserStage
import io.verik.importer.preprocess.PreprocessorFilterStage
import io.verik.importer.preprocess.PreprocessorSerializerStage
import io.verik.importer.preprocess.PreprocessorStage
import io.verik.importer.serialize.general.ConfigFileSerializerStage
import io.verik.importer.serialize.source.SourceSerializerStage

object StageSequencer {

    fun getStageSequence(): StageSequence {
        val stageSequence = StageSequence()

        stageSequence.add(StageType.PREPROCESS, PreprocessorStage)
        stageSequence.add(StageType.PREPROCESS, PreprocessorSerializerStage)
        stageSequence.add(StageType.PREPROCESS, PreprocessorFilterStage)

        stageSequence.add(StageType.PARSE, ParserStage)

        stageSequence.add(StageType.CAST, CasterStage)

        stageSequence.add(StageType.INTERPRET, InterpreterStage)

        stageSequence.add(StageType.SERIALIZE, ConfigFileSerializerStage)
        stageSequence.add(StageType.SERIALIZE, SourceSerializerStage)

        return stageSequence
    }
}
