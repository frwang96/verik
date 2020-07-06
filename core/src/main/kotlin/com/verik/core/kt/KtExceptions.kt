package com.verik.core.kt

import com.verik.core.LinePos
import com.verik.core.LinePosException

// Copyright (c) 2020 Francis Wang

class KtAntlrException(msg: String, linePos: LinePos): LinePosException(msg, linePos)
class KtParseException(msg: String, linePos: LinePos): LinePosException(msg, linePos)