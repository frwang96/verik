/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.kt

enum class KtOperatorType {
    OR,
    AND,
    EQ,
    NOT_EQ,
    LT,
    GT,
    LT_EQ,
    GT_EQ,
    IN,
    NOT_IN,
    RANGE,
    ADD,
    SUB,
    MUL,
    MOD,
    DIV,
    UNARY_ADD,
    UNARY_SUB,
    NOT,
    GET,
    IF,
    IF_ELSE,
    RETURN_UNIT,
    RETURN,
    CONTINUE,
    BREAK
}