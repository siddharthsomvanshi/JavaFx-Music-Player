package sample;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.InputStream;

public class Main extends Application{
    private double x = 0;
    private double y = 0;
    public Scene scene;
    public Parent root;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        scene = new Scene(root, 530, 410);
        scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        primaryStage.setTitle("PlayHard");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        InputStream inpt = getClass().getResourceAsStream("res/icon.png");
        javafx.scene.image.Image imag = new javafx.scene.image.Image(inpt);
        primaryStage.getIcons().add(imag);
        root.setOnMousePressed(new javafx.event.EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new javafx.event.EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                primaryStage.setX(event.getScreenX() - x);
                primaryStage.setY(event.getScreenY() - y);
            }
        });
        primaryStage.setResizable(true);
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}
