package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException

// Copyright (c) 2020 Francis Wang

class VkGrammarException: Exception()
class VkParseException(msg: String, linePos: LinePos): LinePosException(msg, linePos)
class VkExtractException(msg: String, linePos: LinePos): LinePosException(msg, linePos)
