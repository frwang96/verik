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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EInitializerBlock
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ETypeAlias
import io.verik.compiler.ast.element.declaration.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.declaration.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.declaration.sv.EClockingBlock
import io.verik.compiler.ast.element.declaration.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.declaration.sv.EComponentInstantiation
import io.verik.compiler.ast.element.declaration.sv.EConstraint
import io.verik.compiler.ast.element.declaration.sv.ECoverCross
import io.verik.compiler.ast.element.declaration.sv.ECoverGroup
import io.verik.compiler.ast.element.declaration.sv.ECoverPoint
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.ast.element.declaration.sv.EInitialBlock
import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.element.declaration.sv.EModule
import io.verik.compiler.ast.element.declaration.sv.EModuleInterface
import io.verik.compiler.ast.element.declaration.sv.EModulePort
import io.verik.compiler.ast.element.declaration.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.declaration.sv.EPort
import io.verik.compiler.ast.element.declaration.sv.EStruct
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.declaration.sv.ETask
import io.verik.compiler.ast.element.declaration.sv.ETypeDefinition
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.ast.element.expression.common.EParenthesizedExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EReturnStatement
import io.verik.compiler.ast.element.expression.common.ESuperExpression
import io.verik.compiler.ast.element.expression.common.EThisExpression
import io.verik.compiler.ast.element.expression.common.EWhileStatement
import io.verik.compiler.ast.element.expression.kt.EAsExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EIsExpression
import io.verik.compiler.ast.element.expression.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.kt.EKtForStatement
import io.verik.compiler.ast.element.expression.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.expression.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.expression.kt.EWhenExpression
import io.verik.compiler.ast.element.expression.sv.ECaseStatement
import io.verik.compiler.ast.element.expression.sv.EConcatenationExpression
import io.verik.compiler.ast.element.expression.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.expression.sv.EDelayExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.element.expression.sv.EEventExpression
import io.verik.compiler.ast.element.expression.sv.EForeverStatement
import io.verik.compiler.ast.element.expression.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.element.expression.sv.EInlineIfExpression
import io.verik.compiler.ast.element.expression.sv.ERepeatStatement
import io.verik.compiler.ast.element.expression.sv.EReplicationExpression
import io.verik.compiler.ast.element.expression.sv.EScopeExpression
import io.verik.compiler.ast.element.expression.sv.EStreamingExpression
import io.verik.compiler.ast.element.expression.sv.EStringExpression
import io.verik.compiler.ast.element.expression.sv.EStructLiteralExpression
import io.verik.compiler.ast.element.expression.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.expression.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.expression.sv.ESvForStatement
import io.verik.compiler.ast.element.expression.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.expression.sv.EWidthCastExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

class ElementPrinter : Visitor() {

    private val builder = StringBuilder()
    private var first = true

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to print element: $element")
    }

    override fun visitProject(project: EProject) {
        build("Project") {
            build(project.regularNonRootPackages)
            build(project.regularRootPackage)
            build(project.importedNonRootPackages)
            build(project.importedRootPackage)
        }
    }

    override fun visitPackage(pkg: EPackage) {
        build("Package") {
            build(pkg.name)
            build(pkg.files)
            build(pkg.injectedProperties)
            build(pkg.kind.toString())
        }
    }

    override fun visitFile(file: EFile) {
        build("File") {
            build(file.declarations)
        }
    }

    override fun visitTypeAlias(typeAlias: ETypeAlias) {
        build("TypeAlias") {
            build(typeAlias.name)
            build(typeAlias.type.toString())
            build(typeAlias.typeParameters)
        }
    }

    override fun visitTypeDefinition(typeDefinition: ETypeDefinition) {
        build("TypeDefinition") {
            build(typeDefinition.name)
            build(typeDefinition.type.toString())
        }
    }

    override fun visitTypeParameter(typeParameter: ETypeParameter) {
        build("TypeParameter") {
            build(typeParameter.name)
            build(typeParameter.type.toString())
        }
    }

//  Class  /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitKtClass(cls: EKtClass) {
        build("KtClass") {
            build(cls.name)
            build(cls.type.toString())
            build(cls.superType.toString())
            build(cls.typeParameters)
            build(cls.declarations)
            build(cls.primaryConstructor)
            build(cls.isEnum)
            build(cls.isObject)
        }
    }

    override fun visitCompanionObject(companionObject: ECompanionObject) {
        build("CompanionObject") {
            build(companionObject.type.toString())
            build(companionObject.declarations)
        }
    }

    override fun visitSvClass(cls: ESvClass) {
        build("SvClass") {
            build(cls.name)
            build(cls.type.toString())
            build(cls.superType.toString())
            build(cls.typeParameters)
            build(cls.declarations)
            build(cls.isObject)
        }
    }

    override fun visitCoverGroup(coverGroup: ECoverGroup) {
        build("CoverGroup") {
            build(coverGroup.name)
            build(coverGroup.type.toString())
            build(coverGroup.typeParameters)
            build(coverGroup.declarations)
            build(coverGroup.constructor)
        }
    }

    override fun visitModule(module: EModule) {
        build("Module") {
            build(module.name)
            build(module.type.toString())
            build(module.declarations)
            build(module.ports)
        }
    }

    override fun visitModuleInterface(moduleInterface: EModuleInterface) {
        build("ModuleInterface") {
            build(moduleInterface.name)
            build(moduleInterface.type.toString())
            build(moduleInterface.declarations)
            build(moduleInterface.ports)
        }
    }

    override fun visitModulePort(modulePort: EModulePort) {
        build("ModulePort") {
            build(modulePort.name)
            build(modulePort.type.toString())
            build(modulePort.ports)
            build(modulePort.parentModuleInterface?.name)
        }
    }

    override fun visitClockingBlock(clockingBlock: EClockingBlock) {
        build("ClockingBlock") {
            build(clockingBlock.name)
            build(clockingBlock.type.toString())
            build(clockingBlock.ports)
            build(clockingBlock.eventValueParameterIndex)
        }
    }

    override fun visitEnum(enum: EEnum) {
        build("Enum") {
            build(enum.name)
            build(enum.type.toString())
            build(enum.property)
            build(enum.enumEntries.map { it.name })
        }
    }

    override fun visitStruct(struct: EStruct) {
        build("Struct") {
            build(struct.name)
            build(struct.type.toString())
            build(struct.properties)
        }
    }

//  Function  //////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitKtFunction(function: EKtFunction) {
        build("KtFunction") {
            build(function.name)
            build(function.type.toString())
            build(function.body)
            build(function.valueParameters)
            build(function.typeParameters)
            build(function.overriddenFunction != null)
        }
    }

    override fun visitPrimaryConstructor(primaryConstructor: EPrimaryConstructor) {
        build("PrimaryConstructor") {
            build(primaryConstructor.name)
            build(primaryConstructor.type.toString())
            build(primaryConstructor.valueParameters)
            build(primaryConstructor.superTypeCallExpression)
        }
    }

    override fun visitSecondaryConstructor(secondaryConstructor: ESecondaryConstructor) {
        build("SecondaryConstructor") {
            build(secondaryConstructor.name)
            build(secondaryConstructor.type.toString())
            build(secondaryConstructor.body)
            build(secondaryConstructor.valueParameters)
            build(secondaryConstructor.superTypeCallExpression)
        }
    }

    override fun visitSvFunction(function: ESvFunction) {
        build("SvFunction") {
            build(function.name)
            build(function.type.toString())
            build(function.body)
            build(function.typeParameters)
            build(function.valueParameters)
            build(function.isStatic)
            build(function.isVirtual)
        }
    }

    override fun visitTask(task: ETask) {
        build("Task") {
            build(task.name)
            build(task.body)
            build(task.typeParameters)
            build(task.valueParameters)
            build(task.isStatic)
        }
    }

    override fun visitInitializerBlock(initializerBlock: EInitializerBlock) {
        build("InitializerBlock") {
            build(initializerBlock.body)
        }
    }

    override fun visitSvConstructor(constructor: ESvConstructor) {
        build("SvConstructor") {
            build(constructor.type.toString())
            build(constructor.body)
            build(constructor.valueParameters)
        }
    }

    override fun visitInitialBlock(initialBlock: EInitialBlock) {
        build("InitialBlock") {
            build(initialBlock.name)
            build(initialBlock.body)
        }
    }

    override fun visitAlwaysComBlock(alwaysComBlock: EAlwaysComBlock) {
        build("AlwaysComBlock") {
            build(alwaysComBlock.name)
            build(alwaysComBlock.body)
        }
    }

    override fun visitAlwaysSeqBlock(alwaysSeqBlock: EAlwaysSeqBlock) {
        build("AlwaysSeqBlock") {
            build(alwaysSeqBlock.name)
            build(alwaysSeqBlock.eventControlExpression)
            build(alwaysSeqBlock.body)
        }
    }

//  Property  //////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitProperty(property: EProperty) {
        build("Property") {
            build(property.name)
            build(property.type.toString())
            build(property.initializer)
            build(property.isMutable)
            build(property.isStatic)
        }
    }

    override fun visitInjectedProperty(injectedProperty: EInjectedProperty) {
        build("InjectedProperty") {
            build(injectedProperty.name)
            build(injectedProperty.injectedExpression)
        }
    }

    override fun visitEnumEntry(enumEntry: EEnumEntry) {
        build("EnumEntry") {
            build(enumEntry.name)
            build(enumEntry.type.toString())
            build(enumEntry.expression)
        }
    }

    override fun visitCoverPoint(coverPoint: ECoverPoint) {
        build("CoverPoint") {
            build(coverPoint.name)
            build(coverPoint.expression)
        }
    }

    override fun visitCoverCross(coverCross: ECoverCross) {
        build("CoverCross") {
            build(coverCross.name)
            build(coverCross.coverPoints.map { it.name })
        }
    }

    override fun visitComponentInstantiation(componentInstantiation: EComponentInstantiation) {
        build("ComponentInstantiation") {
            build(componentInstantiation.name)
            build(componentInstantiation.type.toString())
            build(componentInstantiation.valueArguments)
        }
    }

    override fun visitModulePortInstantiation(modulePortInstantiation: EModulePortInstantiation) {
        build("ModulePortInstantiation") {
            build(modulePortInstantiation.name)
            build(modulePortInstantiation.type.toString())
            build(modulePortInstantiation.valueArguments)
        }
    }

    override fun visitClockingBlockInstantiation(clockingBlockInstantiation: EClockingBlockInstantiation) {
        build("ClockingBlockInstantiation") {
            build(clockingBlockInstantiation.name)
            build(clockingBlockInstantiation.type.toString())
            build(clockingBlockInstantiation.valueArguments)
            build(clockingBlockInstantiation.eventControlExpression)
        }
    }

    override fun visitConstraint(constraint: EConstraint) {
        build("Constraint") {
            build(constraint.name)
            build(constraint.body)
        }
    }

    override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
        build("KtValueParameter") {
            build(valueParameter.name)
            build(valueParameter.type.toString())
            build(valueParameter.expression)
            build(valueParameter.isPrimaryConstructorProperty)
            build(valueParameter.isMutable)
        }
    }

    override fun visitSvValueParameter(valueParameter: ESvValueParameter) {
        build("SvValueParameter") {
            build(valueParameter.name)
            build(valueParameter.type.toString())
            build(valueParameter.expression)
            build(valueParameter.kind.toString())
        }
    }

    override fun visitPort(port: EPort) {
        build("Port") {
            build(port.name)
            build(port.type.toString())
            build(port.kind.toString())
        }
    }

//  Expression  ////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitNothingExpression(nothingExpression: ENothingExpression) {
        build("NothingExpression") {}
    }

    override fun visitBlockExpression(blockExpression: EBlockExpression) {
        build("BlockExpression") {
            build(blockExpression.type.toString())
            build(blockExpression.statements)
        }
    }

    override fun visitPropertyStatement(propertyStatement: EPropertyStatement) {
        build("PropertyStatement") {
            build(propertyStatement.type.toString())
            build(propertyStatement.property)
        }
    }

    override fun visitParenthesizedExpression(parenthesizedExpression: EParenthesizedExpression) {
        build("ParenthesizedExpression") {
            build(parenthesizedExpression.type.toString())
            build(parenthesizedExpression.expression)
        }
    }

    override fun visitKtUnaryExpression(unaryExpression: EKtUnaryExpression) {
        build("KtUnaryExpression") {
            build(unaryExpression.type.toString())
            build(unaryExpression.expression)
            build(unaryExpression.kind.toString())
        }
    }

    override fun visitSvUnaryExpression(unaryExpression: ESvUnaryExpression) {
        build("SvUnaryExpression") {
            build(unaryExpression.type.toString())
            build(unaryExpression.expression)
            build(unaryExpression.kind.toString())
        }
    }

    override fun visitKtBinaryExpression(binaryExpression: EKtBinaryExpression) {
        build("KtBinaryExpression") {
            build(binaryExpression.type.toString())
            build(binaryExpression.left)
            build(binaryExpression.right)
            build(binaryExpression.kind.toString())
        }
    }

    override fun visitSvBinaryExpression(binaryExpression: ESvBinaryExpression) {
        build("SvBinaryExpression") {
            build(binaryExpression.type.toString())
            build(binaryExpression.left)
            build(binaryExpression.right)
            build(binaryExpression.kind.toString())
        }
    }

    override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
        build("ReferenceExpression") {
            build(referenceExpression.type.toString())
            build(referenceExpression.reference.name)
            build(referenceExpression.receiver)
            build(referenceExpression.isSafeAccess)
        }
    }

    override fun visitCallExpression(callExpression: ECallExpression) {
        build("CallExpression") {
            build(callExpression.type.toString())
            build(callExpression.reference.name)
            build(callExpression.receiver)
            build(callExpression.isSafeAccess)
            build(callExpression.valueArguments)
            build(callExpression.typeArguments.map { it.toString() })
        }
    }

    override fun visitScopeExpression(scopeExpression: EScopeExpression) {
        build("ScopeExpression") {
            build(scopeExpression.type.toString())
            build(scopeExpression.scope.toString())
            build(scopeExpression.typeParameters.map { it.type.toString() })
        }
    }

    override fun visitConstantExpression(constantExpression: EConstantExpression) {
        build("ConstantExpression") {
            build(constantExpression.type.toString())
            build(constantExpression.value)
        }
    }

    override fun visitStructLiteralExpression(structLiteralExpression: EStructLiteralExpression) {
        build("StructLiteralExpression") {
            build(structLiteralExpression.type.toString())
            buildList(structLiteralExpression.entries) {
                build("StructLiteralEntry") {
                    build(it.reference.name)
                    build(it.expression)
                }
            }
        }
    }

    override fun visitThisExpression(thisExpression: EThisExpression) {
        build("ThisExpression") {
            build(thisExpression.type.toString())
        }
    }

    override fun visitSuperExpression(superExpression: ESuperExpression) {
        build("SuperExpression") {
            build(superExpression.type.toString())
        }
    }

    override fun visitReturnStatement(returnStatement: EReturnStatement) {
        build("ReturnStatement") {
            build(returnStatement.type.toString())
            build(returnStatement.expression)
        }
    }

    override fun visitFunctionLiteralExpression(functionLiteralExpression: EFunctionLiteralExpression) {
        build("FunctionLiteralExpression") {
            build(functionLiteralExpression.type.toString())
            build(functionLiteralExpression.valueParameters)
            build(functionLiteralExpression.body)
        }
    }

    override fun visitStringTemplateExpression(stringTemplateExpression: EStringTemplateExpression) {
        build("StringTemplateExpression") {
            build(stringTemplateExpression.type.toString())
            build(stringTemplateExpression.entries)
        }
    }

    override fun visitInjectedExpression(injectedExpression: EInjectedExpression) {
        build("InjectedExpression") {
            build(injectedExpression.type.toString())
            build(injectedExpression.entries)
        }
    }

    override fun visitStringExpression(stringExpression: EStringExpression) {
        build("StringExpression") {
            build(stringExpression.type.toString())
            build(stringExpression.text)
        }
    }

    override fun visitKtArrayAccessExpression(arrayAccessExpression: EKtArrayAccessExpression) {
        build("KtArrayAccessExpression") {
            build(arrayAccessExpression.type.toString())
            build(arrayAccessExpression.array)
            build(arrayAccessExpression.indices)
        }
    }

    override fun visitSvArrayAccessExpression(arrayAccessExpression: ESvArrayAccessExpression) {
        build("SvArrayAccessExpression") {
            build(arrayAccessExpression.type.toString())
            build(arrayAccessExpression.array)
            build(arrayAccessExpression.index)
        }
    }

    override fun visitConstantPartSelectExpression(constantPartSelectExpression: EConstantPartSelectExpression) {
        build("ConstantPartSelectExpression") {
            build(constantPartSelectExpression.type.toString())
            build(constantPartSelectExpression.array)
            build(constantPartSelectExpression.startIndex)
            build(constantPartSelectExpression.endIndex)
        }
    }

    override fun visitConcatenationExpression(concatenationExpression: EConcatenationExpression) {
        build("ConcatenationExpression") {
            build(concatenationExpression.type.toString())
            build(concatenationExpression.expressions)
        }
    }

    override fun visitReplicationExpression(replicationExpression: EReplicationExpression) {
        build("ReplicationExpression") {
            build(replicationExpression.type.toString())
            build(replicationExpression.expression)
            build(replicationExpression.value)
        }
    }

    override fun visitStreamingExpression(streamingExpression: EStreamingExpression) {
        build("StreamingExpression") {
            build(streamingExpression.type.toString())
            build(streamingExpression.expression)
        }
    }

    override fun visitIsExpression(isExpression: EIsExpression) {
        build("IsExpression") {
            build(isExpression.type.toString())
            build(isExpression.expression)
            build(isExpression.property)
            build(isExpression.isNegated)
            build(isExpression.castType.toString())
        }
    }

    override fun visitAsExpression(asExpression: EAsExpression) {
        build("AsExpression") {
            build(asExpression.type.toString())
            build(asExpression.expression)
        }
    }

    override fun visitWidthCastExpression(widthCastExpression: EWidthCastExpression) {
        build("WidthCastExpression") {
            build(widthCastExpression.type.toString())
            build(widthCastExpression.expression)
            build(widthCastExpression.value)
        }
    }

    override fun visitIfExpression(ifExpression: EIfExpression) {
        build("IfExpression") {
            build(ifExpression.type.toString())
            build(ifExpression.condition)
            build(ifExpression.thenExpression)
            build(ifExpression.elseExpression)
        }
    }

    override fun visitInlineIfExpression(inlineIfExpression: EInlineIfExpression) {
        build("InlineIfExpression") {
            build(inlineIfExpression.type.toString())
            build(inlineIfExpression.condition)
            build(inlineIfExpression.thenExpression)
            build(inlineIfExpression.elseExpression)
        }
    }

    override fun visitImmediateAssertStatement(immediateAssertStatement: EImmediateAssertStatement) {
        build("ImmediateAssertStatement") {
            build(immediateAssertStatement.condition)
            build(immediateAssertStatement.elseExpression)
        }
    }

    override fun visitWhenExpression(whenExpression: EWhenExpression) {
        build("WhenExpression") {
            build(whenExpression.type.toString())
            build(whenExpression.subject)
            buildList(whenExpression.entries) {
                build("WhenEntry") {
                    build(it.conditions)
                    build(it.body)
                }
            }
        }
    }

    override fun visitCaseStatement(caseStatement: ECaseStatement) {
        build("CaseStatement") {
            build(caseStatement.type.toString())
            build(caseStatement.subject)
            buildList(caseStatement.entries) {
                build("CaseEntry") {
                    build(it.conditions)
                    build(it.body)
                }
            }
        }
    }

    override fun visitWhileStatement(whileStatement: EWhileStatement) {
        build("WhileStatement") {
            build(whileStatement.type.toString())
            build(whileStatement.condition)
            build(whileStatement.body)
            build(whileStatement.isDoWhile)
        }
    }

    override fun visitKtForStatement(forStatement: EKtForStatement) {
        build("KtForStatement") {
            build(forStatement.type.toString())
            build(forStatement.valueParameter)
            build(forStatement.range)
            build(forStatement.body)
        }
    }

    override fun visitSvForStatement(forStatement: ESvForStatement) {
        build("SvForStatement") {
            build(forStatement.type.toString())
            build(forStatement.property)
            build(forStatement.condition)
            build(forStatement.iteration)
            build(forStatement.body)
        }
    }

    override fun visitForeverStatement(foreverStatement: EForeverStatement) {
        build("ForeverStatement") {
            build(foreverStatement.type.toString())
            build(foreverStatement.body)
        }
    }

    override fun visitRepeatStatement(repeatStatement: ERepeatStatement) {
        build("RepeatStatement") {
            build(repeatStatement.type.toString())
            build(repeatStatement.condition)
            build(repeatStatement.body)
        }
    }

    override fun visitEventExpression(eventExpression: EEventExpression) {
        build("EdgeExpression") {
            build(eventExpression.type.toString())
            build(eventExpression.kind.toString())
            build(eventExpression.expression)
        }
    }

    override fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        build("EventControlExpression") {
            build(eventControlExpression.type.toString())
            build(eventControlExpression.expressions)
        }
    }

    override fun visitDelayExpression(delayExpression: EDelayExpression) {
        build("DelayExpression") {
            build(delayExpression.type.toString())
            build(delayExpression.expression)
        }
    }

    private fun build(content: Boolean?) {
        if (!first) builder.append(", ")
        when (content) {
            true -> builder.append("1")
            false -> builder.append("0")
            else -> builder.append("null")
        }
        first = false
    }

    private fun build(content: Int?) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(content: String?) {
        if (!first) builder.append(", ")
        builder.append(content)
        first = false
    }

    private fun build(element: EElement?) {
        if (element != null)
            element.accept(this)
        else {
            if (!first) builder.append(", ")
            builder.append("null")
            first = false
        }
    }

    private fun build(name: String, content: () -> Unit) {
        if (!first) builder.append(", ")
        builder.append("$name(")
        first = true
        content()
        builder.append(")")
        first = false
    }

    private fun build(entries: List<Any>) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        entries.forEach {
            when (it) {
                is EElement -> it.accept(this)
                is String -> build(it)
                is AnnotationEntry -> build(it.name)
                is LiteralStringEntry -> build(it.text)
                is ExpressionStringEntry -> it.expression.accept(this)
                else -> Messages.INTERNAL_ERROR.on(
                    SourceLocation.NULL,
                    "Unrecognized entry type: ${it::class.simpleName}"
                )
            }
        }
        builder.append("]")
        first = false
    }

    private fun <T> buildList(entries: List<T>, builder: (T) -> Unit) {
        if (!first) this.builder.append(", ")
        this.builder.append("[")
        first = true
        entries.forEach { builder(it) }
        this.builder.append("]")
        first = false
    }

    companion object {

        fun dump(element: EElement): String {
            val elementPrinter = ElementPrinter()
            element.accept(elementPrinter)
            return elementPrinter.builder.toString()
        }
    }
}
