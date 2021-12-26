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
import io.verik.compiler.check.mid.AnnotationEntryCheckerStage
import io.verik.compiler.check.mid.ComponentInstantiationCheckerStage
import io.verik.compiler.check.mid.ObjectCheckerStage
import io.verik.compiler.check.mid.PortCheckerStage
import io.verik.compiler.check.mid.PortInstantiationCheckerStage
import io.verik.compiler.check.mid.TypeParameterTypeCheckerStage
import io.verik.compiler.check.post.CardinalNegativeCheckerStage
import io.verik.compiler.check.post.FileCheckerStage
import io.verik.compiler.check.post.KeywordCheckerStage
import io.verik.compiler.check.post.NameRedeclarationCheckerStage
import io.verik.compiler.check.post.PostNameCheckerStage
import io.verik.compiler.check.post.StatementCheckerStage
import io.verik.compiler.check.post.UntransformedElementCheckerStage
import io.verik.compiler.check.post.UntransformedReferenceCheckerStage
import io.verik.compiler.check.pre.ConfigCheckerStage
import io.verik.compiler.check.pre.FileAnnotationCheckerStage
import io.verik.compiler.check.pre.ImportDirectiveCheckerStage
import io.verik.compiler.check.pre.PreNameCheckerStage
import io.verik.compiler.check.pre.UnsupportedElementCheckerStage
import io.verik.compiler.check.pre.UnsupportedModifierCheckerStage
import io.verik.compiler.interpret.ClassInterpreterStage
import io.verik.compiler.interpret.ComponentInterpreterStage
import io.verik.compiler.interpret.ConstructorDesugarTransformerStage
import io.verik.compiler.interpret.EnumInterpreterStage
import io.verik.compiler.interpret.FileSplitterStage
import io.verik.compiler.interpret.FunctionInterpreterStage
import io.verik.compiler.interpret.FunctionLiteralInterpreterStage
import io.verik.compiler.interpret.ModulePortParentResolverStage
import io.verik.compiler.interpret.PropertyInterpreterStage
import io.verik.compiler.interpret.StructInterpreterStage
import io.verik.compiler.kotlin.KotlinCompilerAnalyzerStage
import io.verik.compiler.kotlin.KotlinCompilerParserStage
import io.verik.compiler.kotlin.KotlinEnvironmentBuilderStage
import io.verik.compiler.reorder.DeadDeclarationEliminatorStage
import io.verik.compiler.reorder.DependencyReordererStage
import io.verik.compiler.reorder.PropertyStatementReordererStage
import io.verik.compiler.resolve.TypeCheckerStage
import io.verik.compiler.resolve.TypeResolvedCheckerStage
import io.verik.compiler.resolve.TypeResolverStage
import io.verik.compiler.serialize.general.ConfigFileSerializerStage
import io.verik.compiler.serialize.general.PackageWrapperSerializerStage
import io.verik.compiler.serialize.general.SourcesFileSerializerStage
import io.verik.compiler.serialize.source.SourceSerializerStage
import io.verik.compiler.serialize.target.CompositeTargetSerializerStage
import io.verik.compiler.specialize.DeclarationSpecializerStage
import io.verik.compiler.transform.mid.CaseStatementTransformerStage
import io.verik.compiler.transform.mid.CastTransformerStage
import io.verik.compiler.transform.mid.ComAssignmentTransformerStage
import io.verik.compiler.transform.mid.ConstantPropagatorStage
import io.verik.compiler.transform.mid.ExpressionEvaluatorStage
import io.verik.compiler.transform.mid.ExpressionExtractorStage
import io.verik.compiler.transform.mid.ForStatementTransformerStage
import io.verik.compiler.transform.mid.IfAndWhenExpressionUnlifterStage
import io.verik.compiler.transform.mid.InjectedStatementTransformerStage
import io.verik.compiler.transform.mid.InlineIfExpressionTransformerStage
import io.verik.compiler.transform.mid.StringTemplateExpressionTransformerStage
import io.verik.compiler.transform.mid.StructLiteralTransformerStage
import io.verik.compiler.transform.mid.ToStringTransformerStage
import io.verik.compiler.transform.mid.UninitializedPropertyTransformerStage
import io.verik.compiler.transform.post.AssignmentTransformerStage
import io.verik.compiler.transform.post.BinaryExpressionTransformerStage
import io.verik.compiler.transform.post.BlockExpressionTransformerStage
import io.verik.compiler.transform.post.CallExpressionTransformerStage
import io.verik.compiler.transform.post.FunctionTransformerStage
import io.verik.compiler.transform.post.PackageNameTransformerStage
import io.verik.compiler.transform.post.ParenthesisInsertionTransformerStage
import io.verik.compiler.transform.post.PropertyTransformerStage
import io.verik.compiler.transform.post.ScopeExpressionInsertionTransformerStage
import io.verik.compiler.transform.post.TemporaryDeclarationRenameStage
import io.verik.compiler.transform.post.TypeReferenceTransformerStage
import io.verik.compiler.transform.post.UnaryExpressionTransformerStage
import io.verik.compiler.transform.post.UnpackedTypeDefinitionTransformerStage
import io.verik.compiler.transform.pre.ArrayAccessExpressionReducerStage
import io.verik.compiler.transform.pre.AssignmentOperatorReducerStage
import io.verik.compiler.transform.pre.BinaryExpressionReducerStage
import io.verik.compiler.transform.pre.BitConstantReducerStage
import io.verik.compiler.transform.pre.ConstantExpressionReducerStage
import io.verik.compiler.transform.pre.ForStatementReducerStage
import io.verik.compiler.transform.pre.FunctionOverloadingTransformerStage
import io.verik.compiler.transform.pre.TypeAliasReducerStage
import io.verik.compiler.transform.pre.UnaryExpressionReducerStage

object StageSequencer {

    fun getStageSequence(): StageSequence {
        val stageSequence = StageSequence()

        stageSequence.add(StageType.PARSE, KotlinEnvironmentBuilderStage)
        stageSequence.add(StageType.PARSE, KotlinCompilerParserStage)

        stageSequence.add(StageType.PRE_CHECK, ConfigCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, FileAnnotationCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, UnsupportedElementCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, UnsupportedModifierCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, ImportDirectiveCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, PreNameCheckerStage)

        stageSequence.add(StageType.COMPILE, KotlinCompilerAnalyzerStage)

        stageSequence.add(StageType.CAST, DeclarationCastIndexerStage)
        stageSequence.add(StageType.CAST, CasterStage)
        stageSequence.add(StageType.CAST, SmartCastReducerStage)

        stageSequence.add(StageType.PRE_TRANSFORM, FunctionOverloadingTransformerStage)
        stageSequence.add(StageType.PRE_TRANSFORM, TypeAliasReducerStage)
        stageSequence.add(StageType.PRE_TRANSFORM, AssignmentOperatorReducerStage)
        stageSequence.add(StageType.PRE_TRANSFORM, UnaryExpressionReducerStage)
        stageSequence.add(StageType.PRE_TRANSFORM, BinaryExpressionReducerStage)
        stageSequence.add(StageType.PRE_TRANSFORM, ArrayAccessExpressionReducerStage)
        stageSequence.add(StageType.PRE_TRANSFORM, ForStatementReducerStage)
        stageSequence.add(StageType.PRE_TRANSFORM, BitConstantReducerStage)
        stageSequence.add(StageType.PRE_TRANSFORM, ConstantExpressionReducerStage)

        stageSequence.add(StageType.MID_CHECK, AnnotationEntryCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ComponentInstantiationCheckerStage)
        stageSequence.add(StageType.MID_CHECK, TypeParameterTypeCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ObjectCheckerStage)
        stageSequence.add(StageType.MID_CHECK, PortCheckerStage)
        stageSequence.add(StageType.MID_CHECK, PortInstantiationCheckerStage)

        stageSequence.add(StageType.RESOLVE, TypeResolverStage)
        stageSequence.add(StageType.RESOLVE, TypeResolvedCheckerStage)
        stageSequence.add(StageType.RESOLVE, DeclarationSpecializerStage)
        stageSequence.add(StageType.RESOLVE, TypeCheckerStage)

        stageSequence.add(StageType.INTERPRET, EnumInterpreterStage)
        stageSequence.add(StageType.INTERPRET, StructInterpreterStage)
        stageSequence.add(StageType.INTERPRET, ComponentInterpreterStage)
        stageSequence.add(StageType.INTERPRET, ConstructorDesugarTransformerStage)
        stageSequence.add(StageType.INTERPRET, ClassInterpreterStage)
        stageSequence.add(StageType.INTERPRET, FunctionInterpreterStage)
        stageSequence.add(StageType.INTERPRET, PropertyInterpreterStage)
        stageSequence.add(StageType.INTERPRET, FunctionLiteralInterpreterStage)
        stageSequence.add(StageType.INTERPRET, ModulePortParentResolverStage)
        stageSequence.add(StageType.INTERPRET, FileSplitterStage)

        stageSequence.add(StageType.MID_TRANSFORM, ConstantPropagatorStage)
        stageSequence.add(StageType.MID_TRANSFORM, ToStringTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, InjectedStatementTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, StringTemplateExpressionTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, CastTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, UninitializedPropertyTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, ComAssignmentTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, ForStatementTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, InlineIfExpressionTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, IfAndWhenExpressionUnlifterStage)
        stageSequence.add(StageType.MID_TRANSFORM, CaseStatementTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, StructLiteralTransformerStage)
        stageSequence.add(StageType.MID_TRANSFORM, ExpressionEvaluatorStage)
        stageSequence.add(StageType.MID_TRANSFORM, ExpressionExtractorStage)

        stageSequence.add(StageType.REORDER, PropertyStatementReordererStage)
        stageSequence.add(StageType.REORDER, DeadDeclarationEliminatorStage)
        stageSequence.add(StageType.REORDER, DependencyReordererStage)

        stageSequence.add(StageType.POST_TRANSFORM, FunctionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, PropertyTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, TypeReferenceTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, AssignmentTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, UnpackedTypeDefinitionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, TemporaryDeclarationRenameStage)
        stageSequence.add(StageType.POST_TRANSFORM, UnaryExpressionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, BinaryExpressionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, PackageNameTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, ScopeExpressionInsertionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, CallExpressionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, BlockExpressionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, ParenthesisInsertionTransformerStage)

        stageSequence.add(StageType.POST_CHECK, UntransformedElementCheckerStage)
        stageSequence.add(StageType.POST_CHECK, UntransformedReferenceCheckerStage)
        stageSequence.add(StageType.POST_CHECK, FileCheckerStage)
        stageSequence.add(StageType.POST_CHECK, CardinalNegativeCheckerStage)
        stageSequence.add(StageType.POST_CHECK, PostNameCheckerStage)
        stageSequence.add(StageType.POST_CHECK, KeywordCheckerStage)
        stageSequence.add(StageType.POST_CHECK, NameRedeclarationCheckerStage)
        stageSequence.add(StageType.POST_CHECK, StatementCheckerStage)

        stageSequence.add(StageType.SERIALIZE, ConfigFileSerializerStage)
        stageSequence.add(StageType.SERIALIZE, CompositeTargetSerializerStage)
        stageSequence.add(StageType.SERIALIZE, SourceSerializerStage)
        stageSequence.add(StageType.SERIALIZE, PackageWrapperSerializerStage)
        stageSequence.add(StageType.SERIALIZE, SourcesFileSerializerStage)

        return stageSequence
    }
}
