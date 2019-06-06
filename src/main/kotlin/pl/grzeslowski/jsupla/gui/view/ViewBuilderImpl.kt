package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.controlsfx.control.ToggleSwitch
import pl.grzeslowski.jsupla.api.generated.model.Channel
import pl.grzeslowski.jsupla.api.generated.model.ChannelFunctionEnumNames.LIGHTSWITCH
import pl.grzeslowski.jsupla.api.generated.model.ChannelFunctionEnumNames.POWERSWITCH
import pl.grzeslowski.jsupla.api.generated.model.Device
import java.util.stream.Collectors

internal class ViewBuilderImpl : ViewBuilder {
    override fun buildViewForDevice(device: Device): Node {
        val deviceName = Label(device.name)
        deviceName.styleClass.add("item-title")
        val deviceComment: Label? = if (device.comment != null && device.comment.isNotBlank()) {
            Label(device.comment)
        } else {
            null
        }

        val channels = device.channels.stream().map { buildViewForChannel(it) }.collect(Collectors.toList())

        val node = VBox()
        node.children.addAll(deviceName)
        if (deviceComment != null) {
            node.children.add(deviceComment)
        }
        node.children.addAll(channels)
        return node
    }

    private fun buildViewForChannel(channel: Channel): Node =
            when (channel.function.name) {
                LIGHTSWITCH, POWERSWITCH -> {
                    val toggle = ToggleSwitch(channel.caption)
                    toggle.isSelected = channel.state.on
                    toggle
                }
                else -> Label(" > Unknown ${channel.function.name}")
            }
}
