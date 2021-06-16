package cliente.controladores;

import cliente.UsuarioI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import servidor.ServidorI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ControladorRegistro {

    @FXML
    private PasswordField contrasena;

    @FXML
    private TextField nombre;

    @FXML
    private Button btnRegistro;

    @FXML
    private Button btnInicio;

    private ServidorI servidor;
    private UsuarioI usuario;


    public void initialize() throws RemoteException, NotBoundException, MalformedURLException {

        btnInicio.setOnAction(event -> {
            try {
                volverAtras();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnRegistro.setOnAction(event -> {
            try {
                registrarse();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }


    public void setServidor(ServidorI servidor) {
        this.servidor = servidor;
    }

    public void setUsuario(UsuarioI usuario) {
        this.usuario = usuario;
    }

    public void registrarse() throws RemoteException {

        if(nombre.getText().equals("") || contrasena.getText().equals("")){
            new Alerta(Alert.AlertType.ERROR, "Rellene todos los campos antes de " +
                    "registrarse", "Error!", btnInicio.getScene().getWindow());
            return;
        }
        servidor.registrarUsuario(nombre.getText(), contrasena.getText(), usuario);
    }

    public void volverAtras() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/login" +
                ".fxml"));
        Parent root = loader.load();
        ControladorInicio controlador = loader.getController();

        Stage stage = (Stage) this.btnRegistro.getScene().getWindow();
        stage.setOnHidden(event -> {
            try {
                controlador.cerrarConexion();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(1);
        });
        stage.setScene(new Scene(root));
    }

    public void mostrarAlerta(Alert.AlertType tipo, String mensaje,String textoCabecera){
        Platform.runLater(()->{
            new Alerta(tipo, mensaje, textoCabecera, btnInicio.getScene().getWindow());
        });
    }
}
