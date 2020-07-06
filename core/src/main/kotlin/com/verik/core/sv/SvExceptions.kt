package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.LinePosException

// Copyright (c) 2020 Francis Wang

class SvBuildException(msg: String, linePos: LinePos): LinePosException(msg, linePos)
