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

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.ENullElement
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.ast.element.common.EParenthesizedExpression
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ERootPackage
import io.verik.compiler.ast.element.common.ESuperExpression
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.EWhileExpression
import io.verik.compiler.ast.element.kt.EAnnotation
import io.verik.compiler.ast.element.kt.EForExpression
import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtArrayAccessExpression
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtUnaryExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.kt.EStringTemplateExpression
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.sv.EBasicComponentInstantiation
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.element.sv.EClockingBlock
import io.verik.compiler.ast.element.sv.EClockingBlockInstantiation
import io.verik.compiler.ast.element.sv.EConcatenationExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.element.sv.EForStatement
import io.verik.compiler.ast.element.sv.EForeverStatement
import io.verik.compiler.ast.element.sv.EImmediateAssertStatement
import io.verik.compiler.ast.element.sv.EInitialBlock
import io.verik.compiler.ast.element.sv.EInjectedExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInterface
import io.verik.compiler.ast.element.sv.EModulePort
import io.verik.compiler.ast.element.sv.EModulePortInstantiation
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.ERepeatStatement
import io.verik.compiler.ast.element.sv.EReplicationExpression
import io.verik.compiler.ast.element.sv.EScopeExpression
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.EStructLiteralExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ESvUnaryExpression
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.ast.element.sv.ETypeDefinition
import io.verik.compiler.ast.element.sv.EWidthCastExpression
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.LiteralStringEntry
import io.verik.compiler.ast.property.PortInstantiation
import io.verik.compiler.ast.property.SuperTypeCallEntry

class ElementPrinter : Visitor() {

    private val builder = StringBuilder()
    private var first = true

    override fun visitNullElement(nullElement: ENullElement) {
        build("NullElement") {}
    }

    override fun visitNullExpression(nullExpression: ENullExpression) {
        build("NullExpression") {}
    }

    override fun visitAnnotation(annotation: EAnnotation) {
        build("Annotation") {
            build(annotation.name)
            build(annotation.arguments)
        }
    }

    override fun visitProject(project: EProject) {
        build("Project") {
            build(project.basicPackages)
            build(project.rootPackage)
        }
    }

    override fun visitBasicPackage(basicPackage: EBasicPackage) {
        build("BasicPackage") {
            build(basicPackage.name)
            build(basicPackage.files)
        }
    }

    override fun visitRootPackage(rootPackage: ERootPackage) {
        build("RootPackage") {
            build(rootPackage.name)
            build(rootPackage.files)
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

    override fun visitKtBasicClass(basicClass: EKtBasicClass) {
        build("KtBasicClass") {
            build(basicClass.name)
            build(basicClass.declarations)
            build(basicClass.typeParameters)
            build(basicClass.annotations)
            build(basicClass.isEnum)
            build(basicClass.isAbstract)
            build(basicClass.isObject)
            build(basicClass.primaryConstructor)
            buildSuperTypeCallEntry(basicClass.superTypeCallEntry)
        }
    }

    override fun visitSvBasicClass(basicClass: ESvBasicClass) {
        build("SvBasicClass") {
            build(basicClass.name)
            build(basicClass.declarations)
            build(basicClass.isVirtual)
            build(basicClass.isDeclarationsStatic)
        }
    }

    override fun visitModule(module: EModule) {
        build("Module") {
            build(module.name)
            build(module.declarations)
            build(module.ports)
        }
    }

    override fun visitModuleInterface(moduleInterface: EModuleInterface) {
        build("ModuleInterface") {
            build(moduleInterface.name)
            build(moduleInterface.declarations)
            build(moduleInterface.ports)
        }
    }

    override fun visitModulePort(modulePort: EModulePort) {
        build("ModulePort") {
            build(modulePort.name)
            build(modulePort.ports)
            build(modulePort.parentModuleInterface?.name)
        }
    }

    override fun visitClockingBlock(clockingBlock: EClockingBlock) {
        build("ClockingBlock") {
            build(clockingBlock.name)
            build(clockingBlock.ports)
            build(clockingBlock.eventValueParameterIndex)
        }
    }

    override fun visitEnum(enum: EEnum) {
        build("Enum") {
            build(enum.name)
            build(enum.enumEntries.map { it.name })
        }
    }

    override fun visitStruct(struct: EStruct) {
        build("Struct") {
            build(struct.name)
            build(struct.properties)
        }
    }

    override fun visitKtFunction(function: EKtFunction) {
        build("KtFunction") {
            build(function.name)
            build(function.type.toString())
            build(function.body)
            build(function.valueParameters)
            build(function.typeParameters)
            build(function.annotations)
            build(function.isAbstract)
        }
    }

    override fun visitSvFunction(function: ESvFunction) {
        build("SvFunction") {
            build(function.name)
            build(function.type.toString())
            build(function.body)
            build(function.isStatic)
            build(function.qualifierType.toString())
            build(function.valueParameters)
        }
    }

    override fun visitTask(task: ETask) {
        build("Task") {
            build(task.name)
            build(task.body)
            build(task.valueParameters)
        }
    }

    override fun visitPrimaryConstructor(primaryConstructor: EPrimaryConstructor) {
        build("PrimaryConstructor") {
            build(primaryConstructor.type.toString())
            build(primaryConstructor.valueParameters)
            build(primaryConstructor.typeParameters.map { it.name })
        }
    }

    override fun visitKtConstructor(constructor: EKtConstructor) {
        build("KtConstructor") {
            build(constructor.type.toString())
            build(constructor.body)
            build(constructor.valueParameters)
            build(constructor.typeParameters.map { it.name })
            buildSuperTypeCallEntry(constructor.superTypeCallEntry)
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

    override fun visitKtProperty(property: EKtProperty) {
        build("KtProperty") {
            build(property.name)
            build(property.type.toString())
            build(property.initializer)
            build(property.annotations)
        }
    }

    override fun visitSvProperty(property: ESvProperty) {
        build("SvProperty") {
            build(property.name)
            build(property.type.toString())
            build(property.initializer)
            build(property.isStatic)
        }
    }

    override fun visitKtEnumEntry(enumEntry: EKtEnumEntry) {
        build("KtEnumEntry") {
            build(enumEntry.name)
            build(enumEntry.type.toString())
            build(enumEntry.annotations)
        }
    }

    override fun visitSvEnumEntry(enumEntry: ESvEnumEntry) {
        build("SvEnumEntry") {
            build(enumEntry.name)
            build(enumEntry.type.toString())
        }
    }

    override fun visitBasicComponentInstantiation(basicComponentInstantiation: EBasicComponentInstantiation) {
        build("BasicComponentInstantiation") {
            build(basicComponentInstantiation.name)
            build(basicComponentInstantiation.type.toString())
            buildPortInstantiations(basicComponentInstantiation.portInstantiations)
        }
    }

    override fun visitModulePortInstantiation(modulePortInstantiation: EModulePortInstantiation) {
        build("ModulePortInstantiation") {
            build(modulePortInstantiation.name)
            build(modulePortInstantiation.type.toString())
            buildPortInstantiations(modulePortInstantiation.portInstantiations)
        }
    }

    override fun visitClockingBlockInstantiation(clockingBlockInstantiation: EClockingBlockInstantiation) {
        build("ClockingBlockInstantiation") {
            build(clockingBlockInstantiation.name)
            build(clockingBlockInstantiation.type.toString())
            buildPortInstantiations(clockingBlockInstantiation.portInstantiations)
            build(clockingBlockInstantiation.eventControlExpression)
        }
    }

    override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
        build("KtValueParameter") {
            build(valueParameter.name)
            build(valueParameter.type.toString())
            build(valueParameter.annotations)
            build(valueParameter.isPrimaryConstructorProperty)
        }
    }

    override fun visitSvValueParameter(valueParameter: ESvValueParameter) {
        build("SvValueParameter") {
            build(valueParameter.name)
            build(valueParameter.type.toString())
        }
    }

    override fun visitPort(port: EPort) {
        build("Port") {
            build(port.name)
            build(port.type.toString())
            build(port.portType.toString())
        }
    }

    override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
        build("KtBlockExpression") {
            build(blockExpression.type.toString())
            build(blockExpression.statements)
        }
    }

    override fun visitSvBlockExpression(blockExpression: ESvBlockExpression) {
        build("SvBlockExpression") {
            build(blockExpression.type.toString())
            build(blockExpression.decorated.toString())
            build(blockExpression.name.toString())
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
        }
    }

    override fun visitKtCallExpression(callExpression: EKtCallExpression) {
        build("KtCallExpression") {
            build(callExpression.type.toString())
            build(callExpression.reference.name)
            build(callExpression.receiver)
            build(callExpression.valueArguments)
            build(callExpression.typeArguments.map { it.toString() })
        }
    }

    override fun visitSvCallExpression(callExpression: ESvCallExpression) {
        build("SvCallExpression") {
            build(callExpression.type.toString())
            build(callExpression.reference.name)
            build(callExpression.receiver)
            build(callExpression.valueArguments)
        }
    }

    override fun visitScopeExpression(scopeExpression: EScopeExpression) {
        build("ScopeExpression") {
            build(scopeExpression.type.toString())
            build(scopeExpression.scope.toString())
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
            build(constantPartSelectExpression.msbIndex)
            build(constantPartSelectExpression.lsbIndex)
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
            build(immediateAssertStatement.type.toString())
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

    override fun visitWhileExpression(whileExpression: EWhileExpression) {
        build("WhileExpression") {
            build(whileExpression.type.toString())
            build(whileExpression.condition)
            build(whileExpression.body)
            build(whileExpression.isDoWhile)
        }
    }

    override fun visitForExpression(forExpression: EForExpression) {
        build("ForExpression") {
            build(forExpression.type.toString())
            build(forExpression.valueParameter)
            build(forExpression.range)
            build(forExpression.body)
        }
    }

    override fun visitForStatement(forStatement: EForStatement) {
        build("ForStatement") {
            build(forStatement.type.toString())
            build(forStatement.valueParameter)
            build(forStatement.initializer)
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
            build(eventExpression.edgeType.toString())
            build(eventExpression.expression)
        }
    }

    override fun visitEventControlExpression(eventControlExpression: EEventControlExpression) {
        build("EventControlExpression") {
            build(eventControlExpression.type.toString())
            build(eventControlExpression.expression)
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
        builder.append(content)
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

    private fun build(elements: List<Any>) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        elements.forEach {
            when (it) {
                is EElement -> it.accept(this)
                is String -> build(it)
                is LiteralStringEntry -> build(it.text)
                is ExpressionStringEntry -> it.expression.accept(this)
                else -> throw IllegalArgumentException("Unrecognized type: ${it::class.simpleName}")
            }
        }
        builder.append("]")
        first = false
    }

    private fun <E> buildList(elements: List<E>, block: (E) -> Unit) {
        if (!first) builder.append(", ")
        builder.append("[")
        first = true
        elements.forEach { block(it) }
        builder.append("]")
        first = false
    }

    private fun <E> buildNullable(element: E?, block: (E) -> Unit) {
        if (!first) builder.append(", ")
        first = true
        if (element != null) block(element)
        else builder.append("null")
        first = false
    }

    private fun buildSuperTypeCallEntry(superTypeCallEntry: SuperTypeCallEntry?) {
        buildNullable(superTypeCallEntry) {
            build("SuperTypeCallEntry") {
                build(it.reference.name)
                build(it.valueArguments)
            }
        }
    }

    private fun buildPortInstantiations(portInstantiations: List<PortInstantiation>) {
        buildList(portInstantiations) {
            build("PortInstantiation") {
                build(it.reference.name)
                build(it.expression)
                build(it.portType.toString())
            }
        }
    }

    companion object {

        fun dump(element: EElement): String {
            val elementPrinter = ElementPrinter()
            element.accept(elementPrinter)
            return elementPrinter.builder.toString()
        }
    }
}
