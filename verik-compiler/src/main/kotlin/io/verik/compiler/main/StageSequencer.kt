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

package io.verik.compiler.main

import io.verik.compiler.cast.ProjectCaster
import io.verik.compiler.cast.ProjectIndexer
import io.verik.compiler.check.cast.ImportDirectiveChecker
import io.verik.compiler.check.cast.UnsupportedElementChecker
import io.verik.compiler.check.post.NameRedeclarationChecker
import io.verik.compiler.check.post.UntransformedElementChecker
import io.verik.compiler.check.pre.FileChecker
import io.verik.compiler.check.pre.KeywordChecker
import io.verik.compiler.common.StageSequence
import io.verik.compiler.compile.KotlinCompilerAnalyzer
import io.verik.compiler.compile.KotlinCompilerParser
import io.verik.compiler.compile.KotlinEnvironmentBuilder
import io.verik.compiler.interpret.EnumInterpreter
import io.verik.compiler.interpret.FileSplitter
import io.verik.compiler.interpret.MemberInterpreter
import io.verik.compiler.resolve.TypeBackFiller
import io.verik.compiler.resolve.TypeResolvedChecker
import io.verik.compiler.resolve.TypeResolver
import io.verik.compiler.serialize.ConfigFileSerializer
import io.verik.compiler.serialize.OrderFileSerializer
import io.verik.compiler.serialize.PackageFileSerializer
import io.verik.compiler.serialize.SourceSerializer
import io.verik.compiler.specialize.DeclarationSpecializer
import io.verik.compiler.specialize.TypeSpecializedChecker
import io.verik.compiler.transform.mid.AssignmentTransformer
import io.verik.compiler.transform.mid.BitConstantTransformer
import io.verik.compiler.transform.mid.InjectedExpressionReducer
import io.verik.compiler.transform.mid.StringTemplateExpressionReducer
import io.verik.compiler.transform.post.*
import io.verik.compiler.transform.pre.AssignmentOperatorReducer
import io.verik.compiler.transform.pre.BinaryExpressionReducer
import io.verik.compiler.transform.pre.UnaryExpressionReducer

object StageSequencer {

    fun getStageSequence(): StageSequence {
        val stageSequence = StageSequence()

        // Compile
        stageSequence.add(KotlinEnvironmentBuilder)
        stageSequence.add(KotlinCompilerParser)
        stageSequence.add(KotlinCompilerAnalyzer)

        // CastCheck
        stageSequence.add(UnsupportedElementChecker)
        stageSequence.add(ImportDirectiveChecker)

        // Cast
        stageSequence.add(ProjectIndexer)
        stageSequence.add(ProjectCaster)

        // PreCheck
        stageSequence.add(FileChecker)
        stageSequence.add(KeywordChecker)

        // PreTransform
        stageSequence.add(AssignmentOperatorReducer)
        stageSequence.add(UnaryExpressionReducer)
        stageSequence.add(BinaryExpressionReducer)

        // Resolve
        stageSequence.add(TypeResolver)
        stageSequence.add(TypeBackFiller)
        stageSequence.add(TypeResolvedChecker)

        // Specialize
        stageSequence.add(DeclarationSpecializer)
        stageSequence.add(TypeSpecializedChecker)

        // Interpret
        stageSequence.add(EnumInterpreter)
        stageSequence.add(MemberInterpreter)
        stageSequence.add(FileSplitter)

        // MidTransform
        stageSequence.add(InjectedExpressionReducer)
        stageSequence.add(StringTemplateExpressionReducer)
        stageSequence.add(BitConstantTransformer)
        stageSequence.add(AssignmentTransformer)

        // PostTransform
        stageSequence.add(LoopExpressionTransformer)
        stageSequence.add(InlineIfExpressionTransformer)
        stageSequence.add(FunctionReferenceTransformer)
        stageSequence.add(FunctionSpecialTransformer)
        stageSequence.add(UnaryExpressionTransformer)
        stageSequence.add(BinaryExpressionTransformer)
        stageSequence.add(PackageNameTransformer)
        stageSequence.add(PackageReferenceInsertionTransformer)
        stageSequence.add(ReferenceAndCallExpressionTransformer)
        stageSequence.add(BlockExpressionTransformer)
        stageSequence.add(ParenthesisInsertionTransformer)

        // PostCheck
        stageSequence.add(NameRedeclarationChecker)
        stageSequence.add(UntransformedElementChecker)

        // Serialize
        stageSequence.add(ConfigFileSerializer)
        stageSequence.add(OrderFileSerializer)
        stageSequence.add(PackageFileSerializer)
        stageSequence.add(SourceSerializer)

        return stageSequence
    }
}