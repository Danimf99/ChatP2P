package cliente;

import cliente.controladores.ControladorInicio;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.RemoteException;


public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/login" +
                ".fxml"));
        Parent root = loader.load();
        ControladorInicio controlador = loader.getController();

        primaryStage.setOnHidden(event -> {
            try {
                controlador.cerrarConexion();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(1);
        });
        primaryStage.setTitle("CompDis");
        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
