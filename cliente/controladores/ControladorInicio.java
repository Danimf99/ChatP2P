package cliente.controladores;

import cliente.UsuarioImpl;
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
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ControladorInicio {

    @FXML
    private PasswordField contrasena;

    @FXML
    private TextField nombre;

    @FXML
    private Button btnInicio;

    @FXML
    private Button btnRegistro;

    private ServidorI servidor;
    private UsuarioImpl usuario;
    private Parent ventanaRegistro;
    private Parent ventanaChat;
    private ControladorChat chatW;

    public void initialize() throws IOException, NotBoundException {
        FXMLLoader registro = new FXMLLoader(getClass().getResource("../resources" +
                "/registro.fxml"));
        ;
        FXMLLoader chat = new FXMLLoader(getClass().getResource("../resources" +
                "/chat.fxml"));
        ;
        ventanaRegistro = registro.load();

        ControladorRegistro registroW = registro.getController();

        ventanaChat = chat.load();
        chatW = chat.getController();

        usuario = new UsuarioImpl(chatW, this, registroW);
        servidor = (ServidorI) Naming.lookup("rmi://localhost:5004/servidor");
        registroW.setUsuario(usuario);
        registroW.setServidor(servidor);
        chatW.setServidor(servidor);
        chatW.setUsuario(usuario);

        btnInicio.setOnAction(event -> {
            try {
                iniciarSesion();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnRegistro.setOnAction(event -> {
            registro();
        });
    }

    public void iniciarSesion() throws RemoteException {
        if (nombre.getText().equals("") || contrasena.getText().equals("")) {
            new Alerta(Alert.AlertType.ERROR, "Rellene todos los campos antes de " +
                    "iniciar sesion", "Error!", btnInicio.getScene().getWindow());
            return;
        }
        usuario.setUsuario(nombre.getText());
        if (!servidor.conectar(nombre.getText(), contrasena.getText(), usuario)) {
            return;
        }
        chatW.cargarAmigos();

        Stage stage = (Stage) this.btnInicio.getScene().getWindow();
        stage.setTitle(nombre.getText());
        stage.setScene(new Scene(ventanaChat));
    }

    public void registro() {
        Stage stage = (Stage) this.btnRegistro.getScene().getWindow();

        stage.setScene(new Scene(ventanaRegistro));

    }

    public void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Platform.runLater(() -> {
            new Alerta(tipo, mensaje, "Error!",btnInicio.getScene().getWindow());
        });
    }

    public void cerrarConexion() throws RemoteException {
        chatW.cerrarConexion();
    }
}
