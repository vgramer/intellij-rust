/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.projectRoots.ProjectJdkTable
import org.rust.ide.icons.RsIcons
import org.rust.ide.sdk.RsSdkType
import javax.swing.Icon

class RsFacetType : FacetType<RsFacet, RsFacetConfiguration>(RsFacet.ID, ID, "Rust") {

    override fun createDefaultConfiguration(): RsFacetConfiguration {
        val result = RsFacetConfiguration()
        val sdks = ProjectJdkTable.getInstance().getSdksOfType(RsSdkType.getInstance())
        result.sdk = sdks.firstOrNull()
        return result
    }

    override fun createFacet(
        module: Module,
        name: String,
        configuration: RsFacetConfiguration,
        underlyingFacet: Facet<*>?
    ): RsFacet = RsFacet(this, module, name, configuration, underlyingFacet)

    override fun isSuitableModuleType(moduleType: ModuleType<*>): Boolean = true

    override fun getIcon(): Icon = RsIcons.RUST

    companion object {
        private const val ID: String = "Rust"

        fun getInstance(): RsFacetType = findInstance(RsFacetType::class.java)
    }
}
