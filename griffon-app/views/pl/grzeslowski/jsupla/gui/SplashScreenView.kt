package pl.grzeslowski.jsupla.gui

import com.jfoenix.controls.JFXProgressBar
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.kordamp.ikonli.javafx.FontIcon
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView::class)
class SplashScreenView : AbstractView() {
    @set:[MVCMember Nonnull]
    lateinit var model: SplashScreenModel
    @set:[MVCMember Nonnull]
    lateinit var controller: SplashScreenController

    @FXML
    private lateinit var splashScreenCenterBox: VBox
    @FXML
    private lateinit var progressBar: JFXProgressBar
    @FXML
    private lateinit var loadingInfo: Label
    @FXML
    private lateinit var version: Label
    @FXML
    private lateinit var closeButton: FontIcon

    override fun internalInit(): Scene {
        centerAfterWindowIsShown()
        val node: Parent = loadParentFxml()
        bindModel()
        connectActions(node, controller)
        connectMessageSource(node)
        val scene = Scene(node)
        scene.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            if (event.code == KeyCode.ESCAPE) {
                shutdown()
            }
        }
        return scene
    }

    private fun bindModel() {
        model.loadingInfo.bindBidirectional(loadingInfo.textProperty())
        model.version.bindBidirectional(version.textProperty())

        model.centerBoxChildren = splashScreenCenterBox.children
        model.progressBar = progressBar
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED) { shutdown() }
    }

    private fun shutdown() {
        runOutsideUI {
            application.shutdown()
        }
    }

    private fun centerAfterWindowIsShown() {
        application.eventRouter.addEventListener("WindowShown") { args ->
            if (args[0].toString() == windowName()) {
                centerWindow(args[1] as Stage)
            }
        }
    }

    override fun tweakStage(stage: Stage) {
        stage.initStyle(StageStyle.UNDECORATED)
    }

    override fun windowName() = "splashScreenWindow"
}