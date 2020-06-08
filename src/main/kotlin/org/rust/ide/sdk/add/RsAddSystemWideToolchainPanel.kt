package org.rust.ide.sdk.add

import com.intellij.openapi.module.Module
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import org.rust.cargo.icons.CargoIcons
import org.rust.ide.icons.RsIcons
import org.rust.ide.sdk.RsDetectedSdk
import org.rust.ide.sdk.RsSdkToInstall
import org.rust.ide.sdk.RsSdkType
import org.rust.ide.sdk.RsSdkUtils.detectRustSdks
import org.rust.ide.sdk.RsSdkUtils.detectSystemWideSdks
import org.rust.ide.sdk.getSdksToInstall
import java.awt.BorderLayout
import javax.swing.Icon

class RsAddSystemWideToolchainPanel(private val module: Module?, private val existingSdks: List<Sdk>) : RsAddSdkPanel() {
    override val panelName: String = "System toolchain"
    private val sdkComboBox: RsSdkPathChoosingComboBox = RsSdkPathChoosingComboBox()

    init {
        layout = BorderLayout()

        val formPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Toolchain path:", sdkComboBox)
            .panel
        add(formPanel, BorderLayout.NORTH)
        addToolchainsAsync(sdkComboBox) { detectSystemWideSdks(module, existingSdks) }
    }

    override fun validateAll(): List<ValidationInfo> = listOfNotNull(validateSdkComboBox(sdkComboBox))

    override fun getOrCreateSdk(): Sdk? {
        return when (val sdk = sdkComboBox.selectedSdk) {
            is RsDetectedSdk -> sdk.setup(existingSdks)
            else -> sdk
        }
    }

    override fun addChangeListener(listener: Runnable) {
        sdkComboBox.childComponent.addItemListener { listener.run() }
    }
}
