/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.common

class SignatureFragment {

    val kind: SignatureFragmentKind
    val text: String

    constructor(text: String) {
        this.kind = SignatureFragmentKind.TEXT
        this.text = text
    }

    constructor(kind: SignatureFragmentKind) {
        this.kind = kind
        this.text = ""
    }
}
