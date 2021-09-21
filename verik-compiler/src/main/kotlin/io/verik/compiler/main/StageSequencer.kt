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

import io.verik.compiler.cast.CasterStage
import io.verik.compiler.cast.IndexerStage
import io.verik.compiler.check.post.CardinalPositiveCheckerStage
import io.verik.compiler.check.post.KeywordCheckerStage
import io.verik.compiler.check.post.NameCheckerStage
import io.verik.compiler.check.post.NameRedeclarationCheckerStage
import io.verik.compiler.check.post.UntransformedElementCheckerStage
import io.verik.compiler.check.pre.FileCheckerStage
import io.verik.compiler.check.pre.ImportDirectiveCheckerStage
import io.verik.compiler.check.pre.UnsupportedElementCheckerStage
import io.verik.compiler.check.pre.UnsupportedModifierCheckerStage
import io.verik.compiler.common.StageSequence
import io.verik.compiler.compile.KotlinCompilerAnalyzerStage
import io.verik.compiler.compile.KotlinCompilerParserStage
import io.verik.compiler.compile.KotlinEnvironmentBuilderStage
import io.verik.compiler.interpret.AnnotationConflictCheckerStage
import io.verik.compiler.interpret.ClassInterpreterStage
import io.verik.compiler.interpret.FileSplitterStage
import io.verik.compiler.interpret.FunctionInterpreterStage
import io.verik.compiler.interpret.PropertyInterpreterStage
import io.verik.compiler.serialize.general.ConfigFileSerializerStage
import io.verik.compiler.serialize.general.OrderFileSerializerStage
import io.verik.compiler.serialize.general.PackageFileSerializerStage
import io.verik.compiler.serialize.source.SourceSerializerStage
import io.verik.compiler.specialize.DeclarationSpecializerStage
import io.verik.compiler.specialize.TypeCheckerStage
import io.verik.compiler.specialize.TypeResolvedCheckerStage
import io.verik.compiler.specialize.TypeResolverStage
import io.verik.compiler.specialize.TypeSpecializerStage
import io.verik.compiler.transform.mid.AssignmentTransformerStage
import io.verik.compiler.transform.mid.CaseStatementTransformerStage
import io.verik.compiler.transform.mid.ComparisonTransformerStage
import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.transform.mid.InjectedExpressionReducerStage
import io.verik.compiler.transform.mid.InlineIfExpressionTransformerStage
import io.verik.compiler.transform.mid.StringTemplateExpressionReducerStage
import io.verik.compiler.transform.mid.StructLiteralTransformerStage
import io.verik.compiler.transform.mid.UninitializedPropertyTransformerStage
import io.verik.compiler.transform.post.BinaryExpressionTransformerStage
import io.verik.compiler.transform.post.BlockExpressionTransformerStage
import io.verik.compiler.transform.post.ConstantExpressionTransformerStage
import io.verik.compiler.transform.post.LoopExpressionTransformerStage
import io.verik.compiler.transform.post.PackageNameTransformerStage
import io.verik.compiler.transform.post.ParenthesisInsertionTransformerStage
import io.verik.compiler.transform.post.ReferenceAndCallExpressionTransformerStage
import io.verik.compiler.transform.post.ScopeReferenceInsertionTransformerStage
import io.verik.compiler.transform.post.TemporaryPropertyRelabelerStage
import io.verik.compiler.transform.post.UnaryExpressionTransformerStage
import io.verik.compiler.transform.pre.ArrayAccessExpressionReducerStage
import io.verik.compiler.transform.pre.AssignmentOperatorReducerStage
import io.verik.compiler.transform.pre.BinaryExpressionReducerStage
import io.verik.compiler.transform.pre.BitConstantTransformerStage
import io.verik.compiler.transform.pre.FunctionOverloadingTransformerStage
import io.verik.compiler.transform.pre.NameRelabelerStage
import io.verik.compiler.transform.pre.UnaryExpressionReducerStage

object StageSequencer {

    fun getStageSequence(): StageSequence {
        val stageSequence = StageSequence()

        // Compile
        stageSequence.add(KotlinEnvironmentBuilderStage)
        stageSequence.add(KotlinCompilerParserStage)
        stageSequence.add(KotlinCompilerAnalyzerStage)

        // PreCheck
        stageSequence.add(UnsupportedElementCheckerStage)
        stageSequence.add(UnsupportedModifierCheckerStage)
        stageSequence.add(FileCheckerStage)
        stageSequence.add(ImportDirectiveCheckerStage)

        // Cast
        stageSequence.add(IndexerStage)
        stageSequence.add(CasterStage)

        // PreTransform
        stageSequence.add(FunctionOverloadingTransformerStage)
        stageSequence.add(NameRelabelerStage)
        stageSequence.add(AssignmentOperatorReducerStage)
        stageSequence.add(UnaryExpressionReducerStage)
        stageSequence.add(BinaryExpressionReducerStage)
        stageSequence.add(ArrayAccessExpressionReducerStage)
        stageSequence.add(BitConstantTransformerStage)

        // Specialize
        stageSequence.add(TypeResolverStage)
        stageSequence.add(TypeResolvedCheckerStage)
        stageSequence.add(DeclarationSpecializerStage)
        stageSequence.add(TypeSpecializerStage)
        stageSequence.add(TypeCheckerStage)

        // Interpret
        stageSequence.add(AnnotationConflictCheckerStage)
        stageSequence.add(ClassInterpreterStage)
        stageSequence.add(FunctionInterpreterStage)
        stageSequence.add(PropertyInterpreterStage)
        stageSequence.add(FileSplitterStage)

        // MidTransform
        stageSequence.add(InjectedExpressionReducerStage)
        stageSequence.add(StringTemplateExpressionReducerStage)
        stageSequence.add(UninitializedPropertyTransformerStage)
        stageSequence.add(AssignmentTransformerStage)
        stageSequence.add(ComparisonTransformerStage)
        stageSequence.add(FunctionTransformerStage)
        stageSequence.add(InlineIfExpressionTransformerStage)
        stageSequence.add(CaseStatementTransformerStage)
        stageSequence.add(StructLiteralTransformerStage)

        // PostTransform
        stageSequence.add(LoopExpressionTransformerStage)
        stageSequence.add(UnaryExpressionTransformerStage)
        stageSequence.add(BinaryExpressionTransformerStage)
        stageSequence.add(ConstantExpressionTransformerStage)
        stageSequence.add(PackageNameTransformerStage)
        stageSequence.add(ScopeReferenceInsertionTransformerStage)
        stageSequence.add(ReferenceAndCallExpressionTransformerStage)
        stageSequence.add(BlockExpressionTransformerStage)
        stageSequence.add(ParenthesisInsertionTransformerStage)
        stageSequence.add(TemporaryPropertyRelabelerStage)

        // PostCheck
        stageSequence.add(CardinalPositiveCheckerStage)
        stageSequence.add(NameCheckerStage)
        stageSequence.add(KeywordCheckerStage)
        stageSequence.add(NameRedeclarationCheckerStage)
        stageSequence.add(UntransformedElementCheckerStage)

        // Serialize
        stageSequence.add(ConfigFileSerializerStage)
        stageSequence.add(OrderFileSerializerStage)
        stageSequence.add(PackageFileSerializerStage)
        stageSequence.add(SourceSerializerStage)

        return stageSequence
    }
}
