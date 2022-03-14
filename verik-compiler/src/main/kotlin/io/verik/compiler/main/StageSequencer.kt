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

import io.verik.compiler.cast.CastIndexerStage
import io.verik.compiler.cast.CasterStage
import io.verik.compiler.cast.SmartCastReducerStage
import io.verik.compiler.check.mid.AnnotationEntryCheckerStage
import io.verik.compiler.check.mid.ArrayAccessMutabilityChecker
import io.verik.compiler.check.mid.ClassDeclarationCheckerStage
import io.verik.compiler.check.mid.ComponentInstantiationCheckerStage
import io.verik.compiler.check.mid.ConstructorCheckerStage
import io.verik.compiler.check.mid.EntryPointCheckerStage
import io.verik.compiler.check.mid.ObjectCheckerStage
import io.verik.compiler.check.mid.OverrideCheckerStage
import io.verik.compiler.check.mid.PortCheckerStage
import io.verik.compiler.check.mid.PortInstantiationCheckerStage
import io.verik.compiler.check.mid.ProceduralBlockReferenceCheckerStage
import io.verik.compiler.check.mid.SuperTypeCheckerStage
import io.verik.compiler.check.mid.TypeArgumentTypeCheckerStage
import io.verik.compiler.check.mid.TypeParameterCheckerStage
import io.verik.compiler.check.mid.ValueParameterCheckerStage
import io.verik.compiler.check.post.FileCheckerStage
import io.verik.compiler.check.post.KeywordCheckerStage
import io.verik.compiler.check.post.NameRedeclarationCheckerStage
import io.verik.compiler.check.post.PostNameCheckerStage
import io.verik.compiler.check.post.StatementCheckerStage
import io.verik.compiler.check.post.UntransformedElementCheckerStage
import io.verik.compiler.check.post.UntransformedReferenceCheckerStage
import io.verik.compiler.check.pre.ConfigCheckerStage
import io.verik.compiler.check.pre.FileAnnotationCheckerStage
import io.verik.compiler.check.pre.PreNameCheckerStage
import io.verik.compiler.check.pre.UnsupportedElementCheckerStage
import io.verik.compiler.check.pre.UnsupportedModifierCheckerStage
import io.verik.compiler.evaluate.ConstantPropagatorStage
import io.verik.compiler.evaluate.ConstantPropertyEliminatorStage
import io.verik.compiler.evaluate.ExpressionEvaluatorStage
import io.verik.compiler.interpret.ClassInterpreterStage
import io.verik.compiler.interpret.CompanionObjectReducerStage
import io.verik.compiler.interpret.ComponentInstantiationInterpreterStage
import io.verik.compiler.interpret.ComponentInterpreterStage
import io.verik.compiler.interpret.ConstructorInterpreterStage
import io.verik.compiler.interpret.CoverGroupInterpreterStage
import io.verik.compiler.interpret.CoverPropertyInterpreterStage
import io.verik.compiler.interpret.EnumInterpreterStage
import io.verik.compiler.interpret.FileSplitterStage
import io.verik.compiler.interpret.FunctionInterpreterStage
import io.verik.compiler.interpret.FunctionLiteralInterpreterStage
import io.verik.compiler.interpret.GenerateForBlockInterpreterStage
import io.verik.compiler.interpret.InitializerBlockReducerStage
import io.verik.compiler.interpret.InjectedPropertyInterpreterStage
import io.verik.compiler.interpret.ModulePortParentResolverStage
import io.verik.compiler.interpret.PackageInjectedPropertyReducerStage
import io.verik.compiler.interpret.PrimaryConstructorReducerStage
import io.verik.compiler.interpret.PropertyInterpreterStage
import io.verik.compiler.interpret.StructUnionInterpreterStage
import io.verik.compiler.kotlin.KotlinCompilerAnalyzerStage
import io.verik.compiler.kotlin.KotlinCompilerParserStage
import io.verik.compiler.kotlin.KotlinEnvironmentBuilderStage
import io.verik.compiler.reorder.DependencyReordererStage
import io.verik.compiler.reorder.PropertyStatementReordererStage
import io.verik.compiler.resolve.SliceResolverStage
import io.verik.compiler.resolve.TypeReferenceForwarderStage
import io.verik.compiler.resolve.TypeResolvedCheckerStage
import io.verik.compiler.resolve.TypeResolverStage
import io.verik.compiler.serialize.general.DeclarationCounterStage
import io.verik.compiler.serialize.general.ReportFileSerializerStage
import io.verik.compiler.serialize.general.WrapperSerializerStage
import io.verik.compiler.serialize.source.SourceSerializerStage
import io.verik.compiler.serialize.target.CompositeTargetSerializerStage
import io.verik.compiler.specialize.SpecializerStage
import io.verik.compiler.transform.lower.BlockExpressionReducerStage
import io.verik.compiler.transform.lower.ExpressionExtractorStage
import io.verik.compiler.transform.lower.FunctionTransformerStage
import io.verik.compiler.transform.lower.ProceduralBlockEliminatorStage
import io.verik.compiler.transform.lower.PropertyTransformerStage
import io.verik.compiler.transform.post.AssignmentTransformerStage
import io.verik.compiler.transform.post.BinaryExpressionTransformerStage
import io.verik.compiler.transform.post.PackageNameTransformerStage
import io.verik.compiler.transform.post.ParenthesisInsertionTransformerStage
import io.verik.compiler.transform.post.ScopeExpressionInsertionTransformerStage
import io.verik.compiler.transform.post.StringTemplateExpressionTransformerStage
import io.verik.compiler.transform.post.StructLiteralTransformerStage
import io.verik.compiler.transform.post.TemporaryDeclarationRenameStage
import io.verik.compiler.transform.post.TypeReferenceTransformerStage
import io.verik.compiler.transform.post.UnaryExpressionTransformerStage
import io.verik.compiler.transform.post.VariableDimensionEliminatorStage
import io.verik.compiler.transform.pre.ArrayAccessExpressionReducerStage
import io.verik.compiler.transform.pre.AssignmentOperatorReducerStage
import io.verik.compiler.transform.pre.BinaryExpressionReducerStage
import io.verik.compiler.transform.pre.BitConstantReducerStage
import io.verik.compiler.transform.pre.ConstantExpressionReducerStage
import io.verik.compiler.transform.pre.ForStatementReducerStage
import io.verik.compiler.transform.pre.FunctionOverloadingTransformerStage
import io.verik.compiler.transform.pre.TypeAliasReducerStage
import io.verik.compiler.transform.pre.UnaryExpressionReducerStage
import io.verik.compiler.transform.upper.CaseStatementTransformerStage
import io.verik.compiler.transform.upper.CastTransformerStage
import io.verik.compiler.transform.upper.CoreFunctionOverrideTransformerStage
import io.verik.compiler.transform.upper.EnumPropertyReferenceTransformerStage
import io.verik.compiler.transform.upper.ForEachUnrollTransformerStage
import io.verik.compiler.transform.upper.ForStatementTransformerStage
import io.verik.compiler.transform.upper.IfAndWhenExpressionUnlifterStage
import io.verik.compiler.transform.upper.InjectedExpressionTransformerStage
import io.verik.compiler.transform.upper.InlineIfExpressionTransformerStage
import io.verik.compiler.transform.upper.ProceduralAssignmentTransformerStage
import io.verik.compiler.transform.upper.SafeAccessReducerStage
import io.verik.compiler.transform.upper.TaskReturnTransformerStage
import io.verik.compiler.transform.upper.ToStringTransformerStage
import io.verik.compiler.transform.upper.UninitializedPropertyTransformerStage

object StageSequencer {

    fun getStageSequence(): StageSequence {
        val stageSequence = StageSequence()

        stageSequence.add(StageType.PARSE, KotlinEnvironmentBuilderStage)
        stageSequence.add(StageType.PARSE, KotlinCompilerParserStage)

        stageSequence.add(StageType.PRE_CHECK, ConfigCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, FileAnnotationCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, UnsupportedElementCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, UnsupportedModifierCheckerStage)
        stageSequence.add(StageType.PRE_CHECK, PreNameCheckerStage)

        stageSequence.add(StageType.COMPILE, KotlinCompilerAnalyzerStage)

        stageSequence.add(StageType.CAST, CastIndexerStage)
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

        stageSequence.add(StageType.MID_CHECK, EntryPointCheckerStage)
        stageSequence.add(StageType.MID_CHECK, SuperTypeCheckerStage)
        stageSequence.add(StageType.MID_CHECK, AnnotationEntryCheckerStage)
        stageSequence.add(StageType.MID_CHECK, OverrideCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ComponentInstantiationCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ClassDeclarationCheckerStage)
        stageSequence.add(StageType.MID_CHECK, TypeParameterCheckerStage)
        stageSequence.add(StageType.MID_CHECK, TypeArgumentTypeCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ConstructorCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ObjectCheckerStage)
        stageSequence.add(StageType.MID_CHECK, PortCheckerStage)
        stageSequence.add(StageType.MID_CHECK, PortInstantiationCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ValueParameterCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ProceduralBlockReferenceCheckerStage)
        stageSequence.add(StageType.MID_CHECK, ArrayAccessMutabilityChecker)

        stageSequence.add(StageType.SPECIALIZE, SpecializerStage)

        stageSequence.add(StageType.RESOLVE, SliceResolverStage)
        stageSequence.add(StageType.RESOLVE, TypeResolverStage)
        stageSequence.add(StageType.RESOLVE, TypeResolvedCheckerStage)
        stageSequence.add(StageType.RESOLVE, TypeReferenceForwarderStage)

        stageSequence.add(StageType.EVALUATE, ConstantPropagatorStage)
        stageSequence.add(StageType.EVALUATE, ExpressionEvaluatorStage)
        stageSequence.add(StageType.EVALUATE, ConstantPropertyEliminatorStage)

        stageSequence.add(StageType.INTERPRET, GenerateForBlockInterpreterStage)
        stageSequence.add(StageType.INTERPRET, EnumInterpreterStage)
        stageSequence.add(StageType.INTERPRET, StructUnionInterpreterStage)
        stageSequence.add(StageType.INTERPRET, CoverGroupInterpreterStage)
        stageSequence.add(StageType.INTERPRET, ComponentInterpreterStage)
        stageSequence.add(StageType.INTERPRET, PrimaryConstructorReducerStage)
        stageSequence.add(StageType.INTERPRET, ClassInterpreterStage)
        stageSequence.add(StageType.INTERPRET, FunctionInterpreterStage)
        stageSequence.add(StageType.INTERPRET, ConstructorInterpreterStage)
        stageSequence.add(StageType.INTERPRET, InitializerBlockReducerStage)
        stageSequence.add(StageType.INTERPRET, InjectedPropertyInterpreterStage)
        stageSequence.add(StageType.INTERPRET, CoverPropertyInterpreterStage)
        stageSequence.add(StageType.INTERPRET, ComponentInstantiationInterpreterStage)
        stageSequence.add(StageType.INTERPRET, PropertyInterpreterStage)
        stageSequence.add(StageType.INTERPRET, FunctionLiteralInterpreterStage)
        stageSequence.add(StageType.INTERPRET, CompanionObjectReducerStage)
        stageSequence.add(StageType.INTERPRET, PackageInjectedPropertyReducerStage)
        stageSequence.add(StageType.INTERPRET, ModulePortParentResolverStage)
        stageSequence.add(StageType.INTERPRET, FileSplitterStage)

        stageSequence.add(StageType.UPPER_TRANSFORM, CoreFunctionOverrideTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, InjectedExpressionTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, ToStringTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, EnumPropertyReferenceTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, CastTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, TaskReturnTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, UninitializedPropertyTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, ProceduralAssignmentTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, SafeAccessReducerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, InlineIfExpressionTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, IfAndWhenExpressionUnlifterStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, CaseStatementTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, ForEachUnrollTransformerStage)
        stageSequence.add(StageType.UPPER_TRANSFORM, ForStatementTransformerStage)

        stageSequence.add(StageType.LOWER_TRANSFORM, FunctionTransformerStage)
        stageSequence.add(StageType.LOWER_TRANSFORM, PropertyTransformerStage)
        stageSequence.add(StageType.LOWER_TRANSFORM, ExpressionExtractorStage)
        stageSequence.add(StageType.LOWER_TRANSFORM, BlockExpressionReducerStage)
        stageSequence.add(StageType.LOWER_TRANSFORM, ProceduralBlockEliminatorStage)
        stageSequence.add(StageType.LOWER_TRANSFORM, VariableDimensionEliminatorStage)

        stageSequence.add(StageType.REORDER, PropertyStatementReordererStage)
        stageSequence.add(StageType.REORDER, DependencyReordererStage)

        stageSequence.add(StageType.POST_TRANSFORM, TypeReferenceTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, AssignmentTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, TemporaryDeclarationRenameStage)
        stageSequence.add(StageType.POST_TRANSFORM, StringTemplateExpressionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, UnaryExpressionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, BinaryExpressionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, StructLiteralTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, PackageNameTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, ScopeExpressionInsertionTransformerStage)
        stageSequence.add(StageType.POST_TRANSFORM, ParenthesisInsertionTransformerStage)

        stageSequence.add(StageType.POST_CHECK, UntransformedElementCheckerStage)
        stageSequence.add(StageType.POST_CHECK, UntransformedReferenceCheckerStage)
        stageSequence.add(StageType.POST_CHECK, FileCheckerStage)
        stageSequence.add(StageType.POST_CHECK, PostNameCheckerStage)
        stageSequence.add(StageType.POST_CHECK, KeywordCheckerStage)
        stageSequence.add(StageType.POST_CHECK, NameRedeclarationCheckerStage)
        stageSequence.add(StageType.POST_CHECK, StatementCheckerStage)

        stageSequence.add(StageType.SERIALIZE, DeclarationCounterStage)
        stageSequence.add(StageType.SERIALIZE, ReportFileSerializerStage)
        stageSequence.add(StageType.SERIALIZE, CompositeTargetSerializerStage)
        stageSequence.add(StageType.SERIALIZE, SourceSerializerStage)
        stageSequence.add(StageType.SERIALIZE, WrapperSerializerStage)

        return stageSequence
    }
}
