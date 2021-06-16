package cliente.controladores;

import cliente.UsuarioI;
import cliente.UsuarioImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import servidor.ServidorI;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ControladorSolicitudes {

    @FXML
    private VBox listaSolicitudes;

    private ServidorI servidor;
    private UsuarioI usuario;
    private ArrayList<String> peticiones;

    public void initialize() throws RemoteException {
        listaSolicitudes.setSpacing(5);
    }

    public void setPeticiones(ArrayList<String> peticiones) {
        this.peticiones = peticiones;
    }

    public void setServidor(ServidorI servidor) {
        this.servidor = servidor;
    }

    public void setUsuario(UsuarioI usuario) {
        this.usuario = usuario;
    }

    public void cargarPeticiones(){
        if(peticiones == null){
            return;
        }
        for(String peticion: peticiones){
            listaSolicitudes.getChildren().add(crearSolicitud(peticion));
        }
    }

    private HBox crearSolicitud(String peticion){
        HBox solicitud = new HBox();
        Label nombre = new Label(peticion);
        Button aceptar = new Button("Aceptar");
        Button rechazar = new Button("Rechazar");

        aceptar.setOnAction(event -> {
            try {
                servidor.aceptarPeticion(usuario.obtenerNombreUsuario(), nombre.getText());
                peticiones.remove(peticion);
                listaSolicitudes.getChildren().clear();
                cargarPeticiones();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        rechazar.setOnAction(event -> {
            try {
                servidor.rechazarPeticion(usuario.obtenerNombreUsuario(), nombre.getText());
                peticiones.remove(peticion);
                listaSolicitudes.getChildren().clear();
                cargarPeticiones();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        solicitud.setSpacing(5);
        solicitud.setAlignment(Pos.CENTER);
        solicitud.getChildren().addAll(nombre, aceptar, rechazar);
        return solicitud;
    }

}
