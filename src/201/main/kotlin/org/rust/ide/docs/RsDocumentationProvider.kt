/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.docs

import com.intellij.psi.PsiDocCommentBase
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.SyntaxTraverser
import org.rust.lang.core.psi.RsDocCommentImpl
import org.rust.lang.core.psi.RsFile
import org.rust.lang.core.psi.ext.RsDocAndAttributeOwner
import org.rust.lang.doc.docElements
import org.rust.lang.doc.documentationAsHtml
import java.util.function.Consumer

@Suppress("UnstableApiUsage")
class RsDocumentationProvider : RsDocumentationProviderBase() {

    override fun collectDocComments(file: PsiFile, sink: Consumer<PsiDocCommentBase>) {
        if (file !is RsFile) return
        for (element in SyntaxTraverser.psiTraverser(file)) {
            if (element is RsDocCommentImpl) {
                sink.accept(element)
            }
        }
    }

    override fun generateRenderedDoc(element: PsiElement): String? {
        if (element !is RsDocAndAttributeOwner) return null
        // Current API doesn't allow determining what comment should be rendered
        // if element have more than one doc comment
        // Fixed in 2020.2
        if (element.docElements().singleOrNull() == null) return null
        return element.documentationAsHtml()
    }
}
