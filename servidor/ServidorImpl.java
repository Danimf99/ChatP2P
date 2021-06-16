package servidor;

import cliente.Notificacion;
import cliente.UsuarioI;
import servidor.dao.DAOUsuario;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class ServidorImpl extends UnicastRemoteObject implements ServidorI {

    private HashMap<String, UsuarioI> usuarios;
    private DAOUsuario dao;

    public ServidorImpl() throws RemoteException {
        super();

        usuarios = new HashMap<>();
        dao = new DAOUsuario();
    }

    @Override
    public void registrarUsuario(String nombreUsuario, String contrasena,
                                 UsuarioI usuario) throws RemoteException {
        if (dao.existeUsuario(nombreUsuario)) {
            usuario.notificar(Notificacion.REGISTRO_ERROR, "EL usuario ya existe");
            return;
        }
        dao.registrarUsuario(nombreUsuario, contrasena);
        usuario.notificar(Notificacion.REGISTRO, "Has sido registrado correctamente");
    }

    @Override
    public void desconectar(UsuarioI usuario) throws RemoteException {
        ArrayList<String> amigos = dao.obtenerAmigos(usuario.obtenerNombreUsuario());

        for (String amigo : amigos) {
            if (usuarios.get(amigo) != null) {
                usuarios.get(amigo).notificar(Notificacion.DESCONEXION, "Se ha " +
                        "desconectado "+usuario.obtenerNombreUsuario(), usuario);
            }
        }

        usuarios.remove(usuario.obtenerNombreUsuario());
    }

    @Override
    public boolean conectar(String nombre, String contrasena, UsuarioI usuario) throws RemoteException {
        if (!dao.existeUsuario(nombre, contrasena)) {
            usuario.notificar(Notificacion.LOGIN, "El nombre o la contraseña no son " +
                    "correctas");
            return false;
        }
        if(usuarios.get(nombre)!=null){
            usuario.notificar(Notificacion.LOGIN, "Ya hay una sesión abierta");
            return false;
        }
        usuarios.put(nombre, usuario);
        ArrayList<String> amigos = dao.obtenerAmigos(nombre);
        HashMap<String, UsuarioI> listaAmigos = new HashMap<>();

        for (String amigo : amigos) {
            if (usuarios.get(amigo) != null) {
                listaAmigos.put(amigo, usuarios.get(amigo));
                usuarios.get(amigo).notificar(Notificacion.CONEXION,
                        "Se ha conectado "+nombre, usuario);
            }
        }
        usuario.setAmigos(listaAmigos);
        return true;
    }

    @Override
    public void enviarSolicitud(UsuarioI usuario, String receptor) throws RemoteException {
        if (!dao.existeUsuario(receptor)) {
            usuario.notificar(Notificacion.PETICION_ERROR, "El usuario " + receptor + " " +
                    "no existe.");
            return;
        }
        if(dao.sonAmigos(usuario.obtenerNombreUsuario(), receptor)){
            usuario.notificar(Notificacion.PETICION_ERROR, "Ya eres amigo de " + receptor);
            return;
        }
        dao.insertarSolicitud(usuario.obtenerNombreUsuario(), receptor);
        usuario.notificar(Notificacion.PETICION_EXITO,
                "Se ha mandado la solicitud correctamente al usuario " + receptor);
    }

    @Override
    public ArrayList<String> obtenerPeticiones(String usuario) throws RemoteException {
        return dao.obtenerPeticiones(usuario);
    }

    @Override
    public void aceptarPeticion(String usuario, String aceptado) throws RemoteException {
        dao.aceptarSolicitud(usuario, aceptado);
        UsuarioI aceptante = usuarios.get(usuario);
        UsuarioI elAceptado = usuarios.get(aceptado);
       if(elAceptado!=null){
           elAceptado.notificar(Notificacion.CONEXION,
                   "Has sido aceptado por "+ usuario, aceptante);
           aceptante.notificar(Notificacion.CONEXION, "El usuario "+ aceptado +
                   " se encuentra en línea", elAceptado);
           return;
       }
    }

    @Override
    public void rechazarPeticion(String usuario, String rechazado) throws RemoteException {
        dao.rechazarSolicitud(usuario, rechazado);
    }

    @Override
    public void cambiarDatos(String usuario, String contrasena) throws RemoteException {
        dao.modificarDatos(usuario,contrasena);
    }

    @Override
    public void eliminarAmigo(String usuario, String eliminado) throws RemoteException {
        dao.eliminarAmigo(usuario, eliminado);
        UsuarioI elimina = usuarios.get(usuario);
        UsuarioI amigo = usuarios.get(eliminado);

        amigo.notificar(Notificacion.ELIMINADO, usuario + " te ha eliminado de amigos."
                , elimina);
    }
}
