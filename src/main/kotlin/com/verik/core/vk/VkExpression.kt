package com.verik.core.vk

import com.verik.core.kt.KtRule

// Copyright (c) 2020 Francis Wang

enum class VkFunctionType {
    REGULAR,
    TARGETED,
    OPERATOR
}

sealed class VkExpression(open var dataType: VkDataType) {

    companion object {

        operator fun invoke(expression: KtRule): VkExpression {
            return VkExpressionParser.parse(expression)
        }
    }
}

data class VkFunctionExpression(
        override var dataType: VkDataType,
        val name: String,
        val functionType: VkFunctionType,
        val args: List<VkExpression>
): VkExpression(dataType) {

    constructor(name: String, functionType: VkFunctionType, args: List<VkExpression>): this(VkUnitType, name, functionType, args)
}

data class VkNavigationExpression(
        override var dataType: VkDataType,
        val target: VkExpression,
        val identifier: String
): VkExpression(dataType) {

    constructor(target: VkExpression, identifier: String): this(VkUnitType, target, identifier)
}

data class VkIdentifierExpression(
        override var dataType: VkDataType,
        val identifier: String
): VkExpression(dataType) {

    constructor(identifier: String): this(VkUnitType, identifier)
}

data class VkLiteralExpression(
        override var dataType: VkDataType,
        val value: String
): VkExpression(dataType) {

    constructor(value: String): this(VkUnitType, value)
}
