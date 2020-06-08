/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.cargo.project.configurable

import com.intellij.application.options.ModuleAwareProjectConfigurable
import com.intellij.facet.FacetManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.UnnamedConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.rust.ide.facet.RsFacet
import org.rust.ide.facet.RsFacetType

class RsActiveSdkModuleConfigurable(project: Project) : ModuleAwareProjectConfigurable<UnnamedConfigurable>(
    project,
    "Rust Toolchain",
    "reference.settings.project.toolchain"
) {

    override fun createDefaultProjectConfigurable(): UnnamedConfigurable = RsActiveSdkConfigurable(project)

    override fun createModuleConfigurable(module: Module): UnnamedConfigurable =
        object : RsActiveSdkConfigurable(module) {
            override var sdk: Sdk?
                get() {
                    val facetManager = FacetManager.getInstance(module)
                    val facet = facetManager.getFacetByType(RsFacet.ID)
                    return facet?.configuration?.sdk
                }
                set(item) {
                    val facetManager = FacetManager.getInstance(module)
                    val facet = facetManager.getFacetByType(RsFacet.ID)
                    if (facet == null) {
                        runWriteAction { addFacet(facetManager, item) }
                    } else {
                        setFacetSdk(facet, item)
                    }
                }
        }

    companion object {
        private fun setFacetSdk(facet: RsFacet, item: Sdk?) {
            facet.configuration.sdk = item
        }

        private fun addFacet(facetManager: FacetManager, sdk: Sdk?) {
            val facet = facetManager.addFacet(RsFacetType.getInstance(), "Rust facet", null)
            setFacetSdk(facet, sdk)
        }
    }
}
