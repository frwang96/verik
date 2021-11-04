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
import io.verik.compiler.cast.DeclarationCastIndexerStage
import io.verik.compiler.check.post.CardinalPositiveCheckerStage
import io.verik.compiler.check.post.FileCheckerStage
import io.verik.compiler.check.post.KeywordCheckerStage
import io.verik.compiler.check.post.NameCheckerStage
import io.verik.compiler.check.post.NameRedeclarationCheckerStage
import io.verik.compiler.check.post.UntransformedElementCheckerStage
import io.verik.compiler.check.post.UntransformedReferenceCheckerStage
import io.verik.compiler.check.pre.ImportDirectiveCheckerStage
import io.verik.compiler.check.pre.UnsupportedElementCheckerStage
import io.verik.compiler.check.pre.UnsupportedModifierCheckerStage
import io.verik.compiler.common.StageSequence
import io.verik.compiler.compile.KotlinCompilerAnalyzerStage
import io.verik.compiler.compile.KotlinCompilerParserStage
import io.verik.compiler.compile.KotlinEnvironmentBuilderStage
import io.verik.compiler.interpret.AnnotationConflictCheckerStage
import io.verik.compiler.interpret.BasicClassInterpreterStage
import io.verik.compiler.interpret.ConstructorDesugarTransformerStage
import io.verik.compiler.interpret.FileSplitterStage
import io.verik.compiler.interpret.FunctionInterpreterStage
import io.verik.compiler.interpret.ModulePortParentResolverStage
import io.verik.compiler.interpret.NonBasicClassInterpreterStage
import io.verik.compiler.interpret.PropertyInterpreterStage
import io.verik.compiler.interpret.ValueParameterInterpreterStage
import io.verik.compiler.resolve.TypeCheckerStage
import io.verik.compiler.resolve.TypeParameterTypeCheckerStage
import io.verik.compiler.resolve.TypeResolvedCheckerStage
import io.verik.compiler.resolve.TypeResolverStage
import io.verik.compiler.serialize.general.ConfigFileSerializerStage
import io.verik.compiler.serialize.general.OrderFileSerializerStage
import io.verik.compiler.serialize.general.PackageFileSerializerStage
import io.verik.compiler.serialize.source.SourceSerializerStage
import io.verik.compiler.serialize.target.TargetSerializerStage
import io.verik.compiler.specialize.DeclarationSpecializerStage
import io.verik.compiler.transform.mid.AssignmentTransformerStage
import io.verik.compiler.transform.mid.CaseStatementTransformerStage
import io.verik.compiler.transform.mid.CastTransformerStage
import io.verik.compiler.transform.mid.ConstantExpressionEvaluatorStage
import io.verik.compiler.transform.mid.EnumNameTransformerStage
import io.verik.compiler.transform.mid.ForStatementTransformerStage
import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.transform.mid.IfAndWhenExpressionUnlifterStage
import io.verik.compiler.transform.mid.InjectedExpressionReducerStage
import io.verik.compiler.transform.mid.InlineIfExpressionTransformerStage
import io.verik.compiler.transform.mid.PropertyStatementReorderStage
import io.verik.compiler.transform.mid.PropertyTransformerStage
import io.verik.compiler.transform.mid.StringTemplateExpressionReducerStage
import io.verik.compiler.transform.mid.StructLiteralTransformerStage
import io.verik.compiler.transform.mid.SubexpressionExtractorStage
import io.verik.compiler.transform.mid.UninitializedPropertyTransformerStage
import io.verik.compiler.transform.post.BinaryExpressionTransformerStage
import io.verik.compiler.transform.post.BlockExpressionTransformerStage
import io.verik.compiler.transform.post.CallExpressionTransformerStage
import io.verik.compiler.transform.post.PackageNameTransformerStage
import io.verik.compiler.transform.post.ParenthesisInsertionTransformerStage
import io.verik.compiler.transform.post.ScopeExpressionInsertionTransformerStage
import io.verik.compiler.transform.post.TemporaryPropertyTransformerStage
import io.verik.compiler.transform.post.TypeReferenceTransformerStage
import io.verik.compiler.transform.post.UnaryExpressionTransformerStage
import io.verik.compiler.transform.post.UnpackedTypeDefinitionTransformerStage
import io.verik.compiler.transform.pre.ArrayAccessExpressionReducerStage
import io.verik.compiler.transform.pre.AssignmentOperatorReducerStage
import io.verik.compiler.transform.pre.BinaryExpressionReducerStage
import io.verik.compiler.transform.pre.BitConstantTransformerStage
import io.verik.compiler.transform.pre.ConstantExpressionTransformerStage
import io.verik.compiler.transform.pre.ForExpressionReducerStage
import io.verik.compiler.transform.pre.FunctionOverloadingTransformerStage
import io.verik.compiler.transform.pre.NameRelabelerStage
import io.verik.compiler.transform.pre.TypeAliasReducerStage
import io.verik.compiler.transform.pre.UnaryExpressionReducerStage

object StageSequencer {

    fun getStageSequence(): StageSequence {
        val stageSequence = StageSequence()

        // Compile
        stageSequence.add(KotlinEnvironmentBuilderStage)
        stageSequence.add(KotlinCompilerParserStage)
        stageSequence.add(UnsupportedElementCheckerStage)
        stageSequence.add(UnsupportedModifierCheckerStage)
        stageSequence.add(ImportDirectiveCheckerStage)
        stageSequence.add(KotlinCompilerAnalyzerStage)

        // Cast
        stageSequence.add(DeclarationCastIndexerStage)
        stageSequence.add(CasterStage)

        // PreTransform
        stageSequence.add(FunctionOverloadingTransformerStage)
        stageSequence.add(NameRelabelerStage)
        stageSequence.add(TypeAliasReducerStage)
        stageSequence.add(AssignmentOperatorReducerStage)
        stageSequence.add(UnaryExpressionReducerStage)
        stageSequence.add(BinaryExpressionReducerStage)
        stageSequence.add(ArrayAccessExpressionReducerStage)
        stageSequence.add(ForExpressionReducerStage)
        stageSequence.add(BitConstantTransformerStage)
        stageSequence.add(ConstantExpressionTransformerStage)

        // Resolve
        stageSequence.add(TypeParameterTypeCheckerStage)
        stageSequence.add(TypeResolverStage)
        stageSequence.add(TypeResolvedCheckerStage)
        stageSequence.add(DeclarationSpecializerStage)
        stageSequence.add(TypeCheckerStage)

        // Interpret
        stageSequence.add(AnnotationConflictCheckerStage)
        stageSequence.add(NonBasicClassInterpreterStage)
        stageSequence.add(ConstructorDesugarTransformerStage)
        stageSequence.add(BasicClassInterpreterStage)
        stageSequence.add(FunctionInterpreterStage)
        stageSequence.add(PropertyInterpreterStage)
        stageSequence.add(ValueParameterInterpreterStage)
        stageSequence.add(ModulePortParentResolverStage)
        stageSequence.add(FileSplitterStage)

        // MidTransform
        stageSequence.add(EnumNameTransformerStage)
        stageSequence.add(InjectedExpressionReducerStage)
        stageSequence.add(StringTemplateExpressionReducerStage)
        stageSequence.add(CastTransformerStage)
        stageSequence.add(UninitializedPropertyTransformerStage)
        stageSequence.add(ForStatementTransformerStage)
        stageSequence.add(FunctionTransformerStage)
        stageSequence.add(PropertyTransformerStage)
        stageSequence.add(InlineIfExpressionTransformerStage)
        stageSequence.add(IfAndWhenExpressionUnlifterStage)
        stageSequence.add(CaseStatementTransformerStage)
        stageSequence.add(StructLiteralTransformerStage)
        stageSequence.add(ConstantExpressionEvaluatorStage)
        stageSequence.add(SubexpressionExtractorStage)
        stageSequence.add(AssignmentTransformerStage)
        stageSequence.add(PropertyStatementReorderStage)

        // PostTransform
        stageSequence.add(TypeReferenceTransformerStage)
        stageSequence.add(UnpackedTypeDefinitionTransformerStage)
        stageSequence.add(TemporaryPropertyTransformerStage)
        stageSequence.add(UnaryExpressionTransformerStage)
        stageSequence.add(BinaryExpressionTransformerStage)
        stageSequence.add(PackageNameTransformerStage)
        stageSequence.add(ScopeExpressionInsertionTransformerStage)
        stageSequence.add(CallExpressionTransformerStage)
        stageSequence.add(BlockExpressionTransformerStage)
        stageSequence.add(ParenthesisInsertionTransformerStage)

        // PostCheck
        stageSequence.add(UntransformedElementCheckerStage)
        stageSequence.add(UntransformedReferenceCheckerStage)
        stageSequence.add(FileCheckerStage)
        stageSequence.add(CardinalPositiveCheckerStage)
        stageSequence.add(NameCheckerStage)
        stageSequence.add(KeywordCheckerStage)
        stageSequence.add(NameRedeclarationCheckerStage)

        // Serialize
        stageSequence.add(ConfigFileSerializerStage)
        stageSequence.add(TargetSerializerStage)
        stageSequence.add(SourceSerializerStage)
        stageSequence.add(PackageFileSerializerStage)
        stageSequence.add(OrderFileSerializerStage)

        return stageSequence
    }
}
