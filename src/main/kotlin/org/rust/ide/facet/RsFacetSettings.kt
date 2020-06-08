/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.facet

import com.intellij.openapi.projectRoots.Sdk

abstract class RsFacetSettings {
    var sdk: Sdk? = null
}
