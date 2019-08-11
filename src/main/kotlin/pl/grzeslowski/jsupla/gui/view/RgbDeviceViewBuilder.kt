package pl.grzeslowski.jsupla.gui.view

import com.jfoenix.controls.JFXSlider
import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import pl.grzeslowski.jsupla.gui.uidevice.*

class RgbDeviceViewBuilder : DeviceViewBuilder {
    override fun build(device: UiDevice, tile: Node): Node? {
        if (isRgbDevice(device).not()) {
            return null
        }

        tile.styleClass.addAll("color-device")

        @Suppress("ThrowableNotThrown")
        val rgbChannel = device.channels.stream()
                .filter { isRgbChannel(it.state) }
                .findAny()
                .orElseThrow { IllegalStateException("Should not happen!") }

        val node = VBox(3.0)

        when {
            rgbChannel.state is UiColorState -> hsvSliders(rgbChannel.state.hue, rgbChannel.state.saturation, rgbChannel.state.value, rgbChannel.state.rgb, node)
            rgbChannel.state is UiDimmerState -> dimmerSlider(rgbChannel.state.brightness, node)
            rgbChannel.state is UiColorAndBrightnessState -> {
                hsvSliders(rgbChannel.state.hue, rgbChannel.state.saturation, rgbChannel.state.value, rgbChannel.state.rgb, node)
                dimmerSlider(rgbChannel.state.brightness, node)
            }
        }

        return node
    }

    private fun dimmerSlider(brightness: DoubleProperty, node: VBox) {
        val dimmer = JFXSlider()
        dimmer.valueProperty().bindBidirectional(brightness)
        node.children.addAll(
                Label("Brightness:"),
                dimmer
        )
    }

    private fun hsvSliders(hue: DoubleProperty, saturation: DoubleProperty, value: DoubleProperty, color: Property<Color>, node: VBox) {
        val colorPicker = ColorPicker()
        VBox.setVgrow(colorPicker, Priority.ALWAYS)
        colorPicker.maxWidth = Double.MAX_VALUE
        colorPicker.valueProperty().bindBidirectional(color)
        val hueSlider = JFXSlider()
        hueSlider.max = 359.0
        hueSlider.valueProperty().bindBidirectional(hue)
        val saturationSlider = JFXSlider()
        saturationSlider.valueProperty().bindBidirectional(saturation)
        val valueSlider = JFXSlider()
        valueSlider.valueProperty().bindBidirectional(value)

        node.alignment = Pos.TOP_CENTER
        node.children.addAll(
                colorPicker,
                Label("Hue:"),
                hueSlider,
                Label("Saturation:"),
                saturationSlider,
                Label("Value:"),
                valueSlider
        )
    }

    private fun isRgbDevice(device: UiDevice) =
            device.channels
                    .stream()
                    .map { it.state }
                    .anyMatch { isRgbChannel(it) }

    private fun isRgbChannel(state: UiState) =
            state is UiColorState || state is UiColorAndBrightnessState || state is UiDimmerState
}