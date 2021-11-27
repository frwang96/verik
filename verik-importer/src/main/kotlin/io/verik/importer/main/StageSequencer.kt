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

import io.verik.importer.parse.LexerStage
import io.verik.importer.parse.ParserStage
import io.verik.importer.preprocess.PreprocessorParserStage
import io.verik.importer.preprocess.PreprocessorStage

object StageSequencer {

    fun getStageSequence(): StageSequence {
        val stageSequence = StageSequence()

        // Preprocess
        stageSequence.add(PreprocessorParserStage)
        stageSequence.add(PreprocessorStage)

        // Parse
        stageSequence.add(LexerStage)
        stageSequence.add(ParserStage)

        return stageSequence
    }
}
