package servidor;

import cliente.UsuarioI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServidorI extends Remote {

    void registrarUsuario(String nombreUsuario, String contrasena, UsuarioI usuario) throws RemoteException;

    void desconectar(UsuarioI usuario) throws RemoteException;

    boolean conectar(String nombre, String contrasena, UsuarioI usuario) throws RemoteException;

    void enviarSolicitud(UsuarioI usuario, String receptor) throws RemoteException;

    ArrayList<String> obtenerPeticiones(String usuario) throws RemoteException;

    void aceptarPeticion(String usuario, String aceptado)throws RemoteException;

    void rechazarPeticion(String usuario, String rechazado) throws RemoteException;

    void cambiarDatos(String usuario, String contrasena) throws RemoteException;

    void eliminarAmigo(String usuario, String eliminado) throws RemoteException;
}
