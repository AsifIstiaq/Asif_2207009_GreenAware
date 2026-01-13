package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SceneUtil {

    public static void switchRoot(Node anyNodeInScene, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneUtil.class.getResource("/com/example/greenaware/" + fxmlPath)
            );
            Parent newRoot = loader.load();

            Scene scene = anyNodeInScene.getScene();
            scene.setRoot(newRoot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void switchScene(
            Node node,
            String fxml,
            String title,
            Consumer<T> controllerConsumer
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneUtil.class.getResource("/com/example/greenaware/" + fxml)
            );
            Parent root = loader.load();

            T controller = loader.getController();
            if (controllerConsumer != null) {
                controllerConsumer.accept(controller);
            }

            Scene scene = node.getScene();
            Stage stage = (Stage) scene.getWindow();

            scene.setRoot(root);
            stage.setTitle(title);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
