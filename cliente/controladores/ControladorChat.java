package cliente.controladores;

import cliente.UsuarioI;
import cliente.UsuarioImpl;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import servidor.ServidorI;

import javafx.scene.control.TextArea;
import java.io.IOException;
import java.rmi.RemoteException;

public class ControladorChat {

    @FXML
    private TextField nombreBuscar;

    @FXML
    private Button btnPeticion;

    @FXML
    private Button btnSolicitudes;

    @FXML
    private VBox panelIzq;

    @FXML
    private VBox amigosConectados;

    @FXML
    private TabPane panelChat;

    @FXML
    private ScrollPane listaAmigos;

    @FXML
    private Button btnModificar;

    private ServidorI servidor;
    private UsuarioImpl usuario;


    public void initialize() {

        panelIzq.setSpacing(3);
        panelIzq.setPadding(new Insets(5));
        panelIzq.setAlignment(Pos.TOP_CENTER);
        amigosConectados.setSpacing(5);
        amigosConectados.setAlignment(Pos.TOP_CENTER);
        listaAmigos.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        listaAmigos.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        btnPeticion.setOnAction(event -> {
            try {
                enviarSolicitud();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnSolicitudes.setOnAction(event -> {
            try {
                abrirSolicitudes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnModificar.setOnAction(event -> {
            try {
                modificarDatos();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void modificarDatos() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources" +
                "/modificarDatos.fxml"));
        Parent ventanaDatos = loader.load();
        ControladorDatos controlador = loader.getController();
        controlador.setServidor(servidor);
        controlador.setUsuario(usuario);

        Stage window = new Stage();

        window.setScene(new Scene(ventanaDatos));
        window.showAndWait();
    }

    private void enviarSolicitud() throws RemoteException {
        servidor.enviarSolicitud(usuario, nombreBuscar.getText());
    }

    public void setServidor(ServidorI servidor) {
        this.servidor = servidor;
    }

    public void setUsuario(UsuarioImpl usuario) {
        this.usuario = usuario;
    }

    public void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Platform.runLater(() -> {
            new Alerta(tipo, mensaje, "Informacion", btnPeticion.getScene().getWindow());
        });
    }

    private void abrirSolicitudes() throws IOException {
        Stage window = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources" +
                "/solicitudes.fxml"));
        Parent ventanaSolicitudes = loader.load();
        ControladorSolicitudes controlador = loader.getController();
        controlador.setServidor(servidor);
        controlador.setUsuario(usuario);
        controlador.setPeticiones(servidor.obtenerPeticiones(usuario.obtenerNombreUsuario()));
        controlador.cargarPeticiones();

        window.setScene(new Scene(ventanaSolicitudes));
        window.showAndWait();
    }

    public void cargarAmigos() throws RemoteException {
        Platform.runLater(()->{
            amigosConectados.getChildren().clear();
            if(usuario.getAmigos().size()==0){
                return;
            }
            usuario.getAmigos().forEach((k, v) -> {
                cargarAmigo(k);
            });
        });
    }

    public void cargarAmigo(String usuario){
        Platform.runLater(()->{
            Label amigo = new Label(usuario);
            amigo.setStyle("-fx-cursor: hand");
            amigo.setOnMouseClicked(event -> {
                try {
                    crearTab(usuario);
                } catch (IOException e) {
                    this.mostrarAlerta(Alert.AlertType.ERROR,
                            "Ha ocurrido un error con el usuario");
                }
            });
            VBox.setMargin(amigo, new Insets(5,0,5,0));
            amigosConectados.getChildren().add(amigo);
        });
    }

    public Tab crearTab(String usuario) throws IOException {
        //Comprobamos que no existe una tab abierta para ese usuario
        for(Tab tab : panelChat.getTabs()){
            if(usuario.equals(tab.getText())){
                panelChat.getSelectionModel().select(tab);
                return tab;
            }
        }

        Tab chat = new Tab(usuario);
        AnchorPane panel = FXMLLoader.load(getClass().getResource("../resources" +
                "/mensaje.fxml"));
        Button btnEnviar = (Button)panel.getChildren().get(1);
        TextArea texto = (TextArea)panel.getChildren().get(2);
        ScrollPane scrollMensajes = (ScrollPane)panel.getChildren().get(3);
        Button btnEliminar = (Button)panel.getChildren().get(4);
        VBox mensajesRecibidos = (VBox)scrollMensajes.getContent();

        scrollMensajes.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollMensajes.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mensajesRecibidos.setStyle("-fx-background-color: grey");

        btnEliminar.setOnAction(event -> {
            try {
                servidor.eliminarAmigo(this.usuario.getUsuario(), usuario);
                panelChat.getTabs().remove(chat);
                if(!this.usuario.getAmigos().containsKey(usuario)){
                    return;
                }
                for (Node child : amigosConectados.getChildren()) {
                    Label label = (Label)child;
                    if(usuario.equals(label.getText())){
                        amigosConectados.getChildren().remove(child);
                        return;
                    }
                }
            } catch (RemoteException e) {
                this.mostrarAlerta(Alert.AlertType.ERROR,
                        "Ha ocurrido un error en el servidor");
            }
        });

        btnEnviar.setOnAction(event -> {
            if(this.usuario.getAmigos().get(usuario)==null){
                this.mostrarAlerta(Alert.AlertType.ERROR,
                        "El usuario "+usuario+" está desconectado");
                return;
            }
            String mensaje = texto.getText();
            ponerMensaje(chat, mensaje, Pos.CENTER_RIGHT);

            try {
                this.usuario.getAmigos().get(usuario).recibirMensaje(mensaje,
                        this.usuario.getUsuario());
            } catch (RemoteException e) {
                this.mostrarAlerta(Alert.AlertType.ERROR,
                        "Ha ocurrido un error en el usuario");
            }
            texto.setText("");
        });

        chat.setContent(panel);
        panelChat.getTabs().add(chat);
        panelChat.getSelectionModel().select(chat);
        return chat;
    }

    public void eliminarAmigo(UsuarioI amigo){
        Platform.runLater(()->{
            try {
                String usuario = amigo.obtenerNombreUsuario();

                for (Node child : amigosConectados.getChildren()) {
                    Label label = (Label)child;
                    if(usuario.equals(label.getText())){
                        amigosConectados.getChildren().remove(child);
                        return;
                    }
                }
            } catch (RemoteException e) {
                this.mostrarAlerta(Alert.AlertType.ERROR,
                        "Ha ocurrido un error con el usuario");
            }
        });
    }

    public void recibirMensaje(String mensaje, String usuario){
        Platform.runLater(()->{
            try {
                Tab tab = crearTab(usuario);
                ponerMensaje(tab, mensaje, Pos.CENTER_LEFT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void ponerMensaje(Tab tab, String mensaje, Pos value){
        AnchorPane panel = (AnchorPane)tab.getContent();
        ScrollPane scrollMensajes = (ScrollPane)panel.getChildren().get(3);
        VBox mensajesRecibidos = (VBox)scrollMensajes.getContent();

        HBox linea = new HBox();
        Label text = new Label(mensaje);

        HBox.setMargin(text, new Insets(4,20,0,6));
        linea.setStyle("color: white; ");

        Color col = Color.rgb(255,255,255);
        CornerRadii corner = new CornerRadii(5);
        Background background = new Background(new BackgroundFill(col, corner, Insets.EMPTY));

        text.setPadding(new Insets(4));
        text.setMaxWidth(200);
        text.setWrapText(true);
        text.setStyle("-fx-text-fill: black;");
        text.setBackground(background);
        linea.setAlignment(value);
        linea.getChildren().add(text);
        mensajesRecibidos.getChildren().add(linea);
        scrollMensajes.setVvalue(1.0);
    }
    public void cerrarConexion() throws RemoteException {
        servidor.desconectar(usuario);
    }
}
