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
import io.verik.compiler.cast.SmartCastReducerStage
import io.verik.compiler.check.post.CardinalPositiveCheckerStage
import io.verik.compiler.check.post.FileCheckerStage
import io.verik.compiler.check.post.KeywordCheckerStage
import io.verik.compiler.check.post.NameCheckerStage
import io.verik.compiler.check.post.NameRedeclarationCheckerStage
import io.verik.compiler.check.post.PortInstantiationCheckerStage
import io.verik.compiler.check.post.StatementCheckerStage
import io.verik.compiler.check.post.UntransformedElementCheckerStage
import io.verik.compiler.check.post.UntransformedReferenceCheckerStage
import io.verik.compiler.check.pre.ImportDirectiveCheckerStage
import io.verik.compiler.check.pre.UnsupportedElementCheckerStage
import io.verik.compiler.check.pre.UnsupportedModifierCheckerStage
import io.verik.compiler.common.StageSequence
import io.verik.compiler.compile.KotlinCompilerAnalyzerStage
import io.verik.compiler.compile.KotlinCompilerParserStage
import io.verik.compiler.compile.KotlinEnvironmentBuilderStage
import io.verik.compiler.interpret.AnnotationCheckerStage
import io.verik.compiler.interpret.BasicClassInterpreterStage
import io.verik.compiler.interpret.ComponentInstantiationCheckerStage
import io.verik.compiler.interpret.ComponentInterpreterStage
import io.verik.compiler.interpret.ConstructorDesugarTransformerStage
import io.verik.compiler.interpret.EnumInterpreterStage
import io.verik.compiler.interpret.FileSplitterStage
import io.verik.compiler.interpret.FunctionInterpreterStage
import io.verik.compiler.interpret.ModulePortParentResolverStage
import io.verik.compiler.interpret.PropertyInterpreterStage
import io.verik.compiler.interpret.StructInterpreterStage
import io.verik.compiler.interpret.ValueParameterInterpreterStage
import io.verik.compiler.resolve.TypeCheckerStage
import io.verik.compiler.resolve.TypeParameterTypeCheckerStage
import io.verik.compiler.resolve.TypeResolvedCheckerStage
import io.verik.compiler.resolve.TypeResolverStage
import io.verik.compiler.serialize.general.ConfigFileSerializerStage
import io.verik.compiler.serialize.general.PackageFileSerializerStage
import io.verik.compiler.serialize.general.SourcesFileSerializerStage
import io.verik.compiler.serialize.source.SourceSerializerStage
import io.verik.compiler.serialize.target.TargetSerializerStage
import io.verik.compiler.specialize.DeclarationSpecializerStage
import io.verik.compiler.transform.mid.AssignmentTransformerStage
import io.verik.compiler.transform.mid.CaseStatementTransformerStage
import io.verik.compiler.transform.mid.CastTransformerStage
import io.verik.compiler.transform.mid.CombinationalAssignmentTransformerStage
import io.verik.compiler.transform.mid.ConstantExpressionEvaluatorStage
import io.verik.compiler.transform.mid.ConstantPropagatorStage
import io.verik.compiler.transform.mid.DeadDeclarationEliminatorStage
import io.verik.compiler.transform.mid.EnumNameTransformerStage
import io.verik.compiler.transform.mid.ForStatementTransformerStage
import io.verik.compiler.transform.mid.FunctionTransformerStage
import io.verik.compiler.transform.mid.IfAndWhenExpressionUnlifterStage
import io.verik.compiler.transform.mid.InjectedStatementReducerStage
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
import io.verik.compiler.transform.post.TemporaryDeclarationRenameStage
import io.verik.compiler.transform.post.TypeReferenceTransformerStage
import io.verik.compiler.transform.post.UnaryExpressionTransformerStage
import io.verik.compiler.transform.post.UnpackedTypeDefinitionTransformerStage
import io.verik.compiler.transform.pre.ArrayAccessExpressionReducerStage
import io.verik.compiler.transform.pre.AssignmentOperatorReducerStage
import io.verik.compiler.transform.pre.BinaryExpressionReducerStage
import io.verik.compiler.transform.pre.BitConstantTransformerStage
import io.verik.compiler.transform.pre.ConstantExpressionTransformerStage
import io.verik.compiler.transform.pre.DeclarationRenameStage
import io.verik.compiler.transform.pre.ForStatementReducerStage
import io.verik.compiler.transform.pre.FunctionOverloadingTransformerStage
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
        stageSequence.addFlush()

        // Cast
        stageSequence.add(DeclarationCastIndexerStage)
        stageSequence.add(CasterStage)
        stageSequence.add(SmartCastReducerStage)
        stageSequence.addFlush()

        // PreTransform
        stageSequence.add(FunctionOverloadingTransformerStage)
        stageSequence.add(DeclarationRenameStage)
        stageSequence.add(TypeAliasReducerStage)
        stageSequence.add(AssignmentOperatorReducerStage)
        stageSequence.add(UnaryExpressionReducerStage)
        stageSequence.add(BinaryExpressionReducerStage)
        stageSequence.add(ArrayAccessExpressionReducerStage)
        stageSequence.add(ForStatementReducerStage)
        stageSequence.add(BitConstantTransformerStage)
        stageSequence.add(ConstantExpressionTransformerStage)
        stageSequence.addFlush()

        // Resolve
        stageSequence.add(TypeParameterTypeCheckerStage)
        stageSequence.add(TypeResolverStage)
        stageSequence.add(TypeResolvedCheckerStage)
        stageSequence.add(DeclarationSpecializerStage)
        stageSequence.add(TypeCheckerStage)
        stageSequence.addFlush()

        // Interpret
        stageSequence.add(AnnotationCheckerStage)
        stageSequence.add(ComponentInstantiationCheckerStage)
        stageSequence.add(EnumInterpreterStage)
        stageSequence.add(StructInterpreterStage)
        stageSequence.add(ComponentInterpreterStage)
        stageSequence.add(ConstructorDesugarTransformerStage)
        stageSequence.add(BasicClassInterpreterStage)
        stageSequence.add(FunctionInterpreterStage)
        stageSequence.add(PropertyInterpreterStage)
        stageSequence.add(ValueParameterInterpreterStage)
        stageSequence.add(ModulePortParentResolverStage)
        stageSequence.add(FileSplitterStage)
        stageSequence.addFlush()

        // MidTransform
        stageSequence.add(EnumNameTransformerStage)
        stageSequence.add(ConstantPropagatorStage)
        stageSequence.add(InjectedStatementReducerStage)
        stageSequence.add(StringTemplateExpressionReducerStage)
        stageSequence.add(CastTransformerStage)
        stageSequence.add(UninitializedPropertyTransformerStage)
        stageSequence.add(CombinationalAssignmentTransformerStage)
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
        stageSequence.add(DeadDeclarationEliminatorStage)
        stageSequence.addFlush()

        // PostTransform
        stageSequence.add(TypeReferenceTransformerStage)
        stageSequence.add(UnpackedTypeDefinitionTransformerStage)
        stageSequence.add(TemporaryDeclarationRenameStage)
        stageSequence.add(UnaryExpressionTransformerStage)
        stageSequence.add(BinaryExpressionTransformerStage)
        stageSequence.add(PackageNameTransformerStage)
        stageSequence.add(ScopeExpressionInsertionTransformerStage)
        stageSequence.add(CallExpressionTransformerStage)
        stageSequence.add(BlockExpressionTransformerStage)
        stageSequence.add(ParenthesisInsertionTransformerStage)
        stageSequence.addFlush()

        // PostCheck
        stageSequence.add(UntransformedElementCheckerStage)
        stageSequence.add(UntransformedReferenceCheckerStage)
        stageSequence.add(FileCheckerStage)
        stageSequence.add(CardinalPositiveCheckerStage)
        stageSequence.add(NameCheckerStage)
        stageSequence.add(KeywordCheckerStage)
        stageSequence.add(NameRedeclarationCheckerStage)
        stageSequence.add(StatementCheckerStage)
        stageSequence.add(PortInstantiationCheckerStage)
        stageSequence.addFlush()

        // Serialize
        stageSequence.add(ConfigFileSerializerStage)
        stageSequence.add(TargetSerializerStage)
        stageSequence.add(SourceSerializerStage)
        stageSequence.add(PackageFileSerializerStage)
        stageSequence.add(SourcesFileSerializerStage)
        stageSequence.addFlush()

        return stageSequence
    }
}
