package cliente.controladores;

import cliente.UsuarioImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import servidor.ServidorI;

import java.rmi.RemoteException;

public class ControladorDatos {

    @FXML
    private PasswordField contrasena;
    @FXML
    private PasswordField confirmarContrasena;
    @FXML
    private Button btnModificar;

    private ServidorI servidor;
    private UsuarioImpl usuario;

    public void initialize(){

        btnModificar.setOnAction(event -> {
            try {
                modificarDatos();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    public void setServidor(ServidorI servidor) {
        this.servidor = servidor;
    }

    public void setUsuario(UsuarioImpl usuario) {
        this.usuario = usuario;
    }

    private void modificarDatos() throws RemoteException {
        if(contrasena.getText().isEmpty() || confirmarContrasena.getText().isEmpty()){
            new Alerta(Alert.AlertType.ERROR, "Rellene los dos campos", "Error",
                    btnModificar.getScene().getWindow());
        }
        if(confirmarContrasena.getText().equals(contrasena.getText())){
            servidor.cambiarDatos(usuario.getUsuario(), contrasena.getText());
            Stage stage = (Stage)btnModificar.getScene().getWindow();
            stage.close();
        }else{
            new Alerta(Alert.AlertType.ERROR, "Las contraseñas tienen que ser iguales",
                    "Error",
                    btnModificar.getScene().getWindow());
        }
    }
}
