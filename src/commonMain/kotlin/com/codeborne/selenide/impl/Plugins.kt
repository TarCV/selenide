package com.codeborne.selenide.impl

import co.touchlab.stately.isolate.IsolateState
import com.codeborne.selenide.commands.Commands
import okio.ExperimentalFileSystem

interface PluginProvider {
    val downloadFileToFolder: DownloadFileToFolder
    val elementDescriber: ElementDescriber
}
data class PluginProviderData(
    override var downloadFileToFolder: DownloadFileToFolder,
    override var elementDescriber: ElementDescriber
) : PluginProvider

interface PluginProviderExperimental {
    @ExperimentalFileSystem
    var commands: Commands
}

@ExperimentalFileSystem
data class PluginProviderExperimentalData(override var commands: Commands)
    : PluginProviderExperimental

/**
 * We assume this API will change in next releases.
 * Be aware if you are going to use it.
 *
 * @since Selenide 5.15.0
 */
object Plugins : PluginProvider, PluginProviderExperimental {
    private val holder: IsolateState<PluginProviderData> by lazy {
        val data = PluginProviderData(
            DownloadFileToFolder(),
            SelenideElementDescriber()
        )

        IsolateState { data }
    }

    @ExperimentalFileSystem
    private val experimentalHolder: IsolateState<PluginProviderExperimentalData> by lazy {
        val data = PluginProviderExperimentalData(Commands())

        IsolateState { data }
    }

    override var downloadFileToFolder: DownloadFileToFolder
        get() = holder.access { it.downloadFileToFolder }
        set(value) = holder.access { it.downloadFileToFolder = value }

    @ExperimentalFileSystem
    override var commands: Commands
        get() = experimentalHolder.access { it.commands }
        set(value) = experimentalHolder.access { it.commands = value }

    override var elementDescriber: ElementDescriber
        get() = holder.access { it.elementDescriber }
        set(value) = holder.access { it.elementDescriber = value }
}
