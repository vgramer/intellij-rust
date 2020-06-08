/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.facet

import com.intellij.facet.FacetConfiguration
import com.intellij.facet.ui.FacetEditorContext
import com.intellij.facet.ui.FacetEditorTab
import com.intellij.facet.ui.FacetValidatorsManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element
import org.rust.ide.sdk.RsSdkType

class RsFacetConfiguration : RsFacetSettings(), FacetConfiguration {

    override fun createEditorTabs(
        editorContext: FacetEditorContext,
        validatorsManager: FacetValidatorsManager
    ): Array<FacetEditorTab> = arrayOf()

    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        val sdkName = element.getAttributeValue(SDK_NAME)

        val sdk = if (sdkName.isNotEmpty()) {
            ProjectJdkTable.getInstance().findJdk(sdkName, RsSdkType.getInstance().name)
        } else {
            null
        }

        this.sdk = sdk
        if (sdk != null) {
            ApplicationManager.getApplication()
                .messageBus
                .syncPublisher(ProjectJdkTable.JDK_TABLE_TOPIC)
                .jdkAdded(sdk)
        }
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        element.setAttribute(SDK_NAME, sdk?.name.orEmpty())
    }

    companion object {
        private const val SDK_NAME: String = "sdkName"
    }
}
