package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRule
import com.verik.core.sv.SvExpression

// Copyright (c) 2020 Francis Wang

enum class VkFunctionType {
    REGULAR,
    TARGETED,
    OPERATOR
}

sealed class VkExpression(open var dataType: VkDataType, open var linePos: LinePos) {

    fun extract(): SvExpression {
        return VkExpressionExtractor.extract(this)
    }

    companion object {

        operator fun invoke(expression: KtRule): VkExpression {
            return VkExpressionParser.parse(expression)
        }
    }
}

data class VkFunctionExpression(
        override var dataType: VkDataType,
        override var linePos: LinePos,
        val name: String,
        val functionType: VkFunctionType,
        val args: List<VkExpression>
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, name: String, functionType: VkFunctionType, args: List<VkExpression>):
            this(VkUnitType, linePos, name, functionType, args)
}

data class VkNavigationExpression(
        override var dataType: VkDataType,
        override var linePos: LinePos,
        val target: VkExpression,
        val identifier: String
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, target: VkExpression, identifier: String):
            this(VkUnitType, linePos, target, identifier)
}

data class VkIdentifierExpression(
        override var dataType: VkDataType,
        override var linePos: LinePos,
        val identifier: String
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, identifier: String): this(VkUnitType, linePos, identifier)
}

data class VkLiteralExpression(
        override var dataType: VkDataType,
        override var linePos: LinePos,
        val value: String
): VkExpression(dataType, linePos) {

    constructor(linePos: LinePos, value: String): this(VkUnitType, linePos, value)
}
