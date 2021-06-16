package cliente;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface UsuarioI extends Remote {


    void notificar(Notificacion notificacion, String mensaje) throws RemoteException;

    void notificar(Notificacion notificacion, String mensaje, UsuarioI usuario) throws RemoteException;

    void recibirMensaje(String mensaje, String usuario) throws RemoteException;

    void setAmigos(HashMap<String, UsuarioI> listaAmigos) throws RemoteException;

    String obtenerNombreUsuario() throws RemoteException;

}
