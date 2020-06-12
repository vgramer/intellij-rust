/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.debugger.runconfig

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.BuildNumber
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.impl.XDebugProcessConfiguratorStarter
import com.intellij.xdebugger.impl.ui.XDebugSessionData
import org.rust.cargo.runconfig.CargoRunStateBase
import org.rust.debugger.RsDebuggerToolchainService
import org.rust.debugger.settings.RsDebuggerSettings
import org.rust.openapiext.computeWithCancelableProgress

object RsDebugRunnerUtils {

    // TODO: move them into bundle
    const val ERROR_MESSAGE_TITLE: String = "Unable to run debugger"
    const val MSVC_IS_NOT_SUPPORTED_MESSAGE: String = "MSVC toolchain is not supported. Please use GNU toolchain."

    fun showRunContent(
        state: CargoRunStateBase,
        environment: ExecutionEnvironment,
        runExecutable: GeneralCommandLine
    ): RunContentDescriptor? {
        val runParameters = RsDebugRunParameters(environment.project, runExecutable, state.cargoProject)
        return XDebuggerManager.getInstance(environment.project)
            .startSession(environment, object : XDebugProcessConfiguratorStarter() {
                override fun start(session: XDebugSession): XDebugProcess =
                    RsLocalDebugProcess(runParameters, session, state.consoleBuilder).apply {
                        ProcessTerminatedListener.attach(processHandler, environment.project)
                        start()
                    }

                override fun configure(data: XDebugSessionData?) {}
            })
            .runContentDescriptor
    }

    @Suppress("UnnecessaryVariable")
    fun checkToolchainSupported(project: Project, state: CargoRunStateBase): Boolean {
        if (ApplicationInfo.getInstance().build < BuildNumber.fromString("202")!!) {
            Messages.showErrorDialog(project, MSVC_IS_NOT_SUPPORTED_MESSAGE, ERROR_MESSAGE_TITLE)
            return false
        }
        val host = project.computeWithCancelableProgress("Checking if toolchain is supported...") {
            state.rustVersion().rustc?.host.orEmpty()
        }
        val isMSVCRustToolchain = "msvc" in host
        return isMSVCRustToolchain
    }

    fun checkToolchainConfigured(project: Project): Boolean {
        val lldbStatus = RsDebuggerToolchainService.getInstance().getLLDBStatus()
        val (message, action) = when (lldbStatus) {
            RsDebuggerToolchainService.LLDBStatus.Unavailable -> return false
            RsDebuggerToolchainService.LLDBStatus.NeedToDownload -> "Debugger is not loaded yet" to "Download"
            RsDebuggerToolchainService.LLDBStatus.NeedToUpdate -> "Debugger is outdated" to "Update"
            is RsDebuggerToolchainService.LLDBStatus.Binaries -> return true
        }

        val option = if (!RsDebuggerSettings.getInstance().downloadAutomatically) {
            showDialog(project, message, action)
        } else {
            Messages.OK
        }

        if (option == Messages.OK) {
            val result = RsDebuggerToolchainService.getInstance().downloadDebugger(project)
            if (result is RsDebuggerToolchainService.DownloadResult.Ok) {
                RsDebuggerSettings.getInstance().lldbPath = result.lldbDir.absolutePath
                return true
            }
        }
        return false
    }

    private fun showDialog(project: Project, message: String, action: String): Int {
        return Messages.showDialog(
            project,
            message,
            RsDebugRunnerUtils.ERROR_MESSAGE_TITLE,
            arrayOf(action),
            Messages.OK,
            Messages.getErrorIcon(),
            object : DialogWrapper.DoNotAskOption.Adapter() {
                override fun rememberChoice(isSelected: Boolean, exitCode: Int) {
                    if (exitCode == Messages.OK) {
                        RsDebuggerSettings.getInstance().downloadAutomatically = isSelected
                    }
                }
            }
        )
    }
}
