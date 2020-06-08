/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.facet

import com.intellij.facet.Facet
import com.intellij.facet.FacetType
import com.intellij.facet.FacetTypeId
import com.intellij.openapi.module.Module

class RsFacet(
    facetType: FacetType<*, *>,
    module: Module,
    name: String,
    configuration: RsFacetConfiguration,
    underlyingFacet: Facet<*>?
) : Facet<RsFacetConfiguration>(facetType, module, name, configuration, underlyingFacet) {

    companion object {
        @JvmField
        val ID: FacetTypeId<RsFacet> = FacetTypeId<RsFacet>("rust")
    }
}
