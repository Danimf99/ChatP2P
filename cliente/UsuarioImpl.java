package cliente;

import cliente.controladores.ControladorChat;
import cliente.controladores.ControladorInicio;
import cliente.controladores.ControladorRegistro;
import javafx.scene.control.Alert;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class UsuarioImpl extends UnicastRemoteObject implements UsuarioI {

    private ControladorChat ventanaChat;
    private ControladorInicio ventanaInicio;
    private ControladorRegistro ventanaRegistro;
    private HashMap<String, UsuarioI> amigos;
    private String usuario;

    public UsuarioImpl() throws RemoteException {
        super();
        this.ventanaChat = null;
        this.ventanaInicio = null;
        this.ventanaRegistro = null;
    }

    public UsuarioImpl(ControladorChat ventanaChat, ControladorInicio ventanaInicio,
                       ControladorRegistro ventanaRegistro
    ) throws RemoteException {
        super();

        this.ventanaChat = ventanaChat;
        this.ventanaInicio = ventanaInicio;
        this.ventanaRegistro = ventanaRegistro;
        this.usuario = null;
        this.amigos = new HashMap<>();
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    /*
        INICIO FUNCIONES
         */
    @Override
    public void notificar(Notificacion notificacion, String mensaje) throws RemoteException {
        switch (notificacion) {
            case REGISTRO:
                ventanaRegistro.mostrarAlerta(Alert.AlertType.CONFIRMATION, mensaje,
                        "Confirmacion");
                break;
            case REGISTRO_ERROR:
                ventanaRegistro.mostrarAlerta(Alert.AlertType.ERROR, mensaje, "Error!");
                break;
            case LOGIN:
                ventanaInicio.mostrarAlerta(Alert.AlertType.ERROR, mensaje);
                break;
            case PETICION_ERROR:
                ventanaChat.mostrarAlerta(Alert.AlertType.ERROR, mensaje);
                break;
            case PETICION_EXITO:
                ventanaChat.mostrarAlerta(Alert.AlertType.INFORMATION, mensaje);
                break;
        }
    }

    @Override
    public void recibirMensaje(String mensaje, String usuario) throws RemoteException {
        ventanaChat.recibirMensaje(mensaje, usuario);
    }

    @Override
    public void setAmigos(HashMap<String, UsuarioI> listaAmigos) throws RemoteException {
        this.amigos = listaAmigos;
    }

    /**
     * Notifica conexion y desconexion de usuarios para poder añadir o borrarlo de la
     * lista de amigos conectados
     */
    @Override
    public void notificar(Notificacion notificacion, String mensaje, UsuarioI usuario) throws RemoteException {
        if(notificacion == Notificacion.CONEXION ) {
            amigos.put(usuario.obtenerNombreUsuario(), usuario);
            ventanaChat.cargarAmigo(usuario.obtenerNombreUsuario());
            ventanaChat.mostrarAlerta(Alert.AlertType.INFORMATION,mensaje);
        }
        if(notificacion == Notificacion.DESCONEXION){
            amigos.remove(usuario.obtenerNombreUsuario());
            ventanaChat.cargarAmigos();
            ventanaChat.mostrarAlerta(Alert.AlertType.INFORMATION, mensaje);
        }
        if(notificacion == Notificacion.ELIMINADO){
            amigos.remove(usuario.obtenerNombreUsuario());
            ventanaChat.eliminarAmigo(usuario);
        }
    }

    @Override
    public String obtenerNombreUsuario() throws RemoteException {
        return usuario;
    }


    public HashMap<String, UsuarioI> getAmigos() {
        return amigos;
    }
}
