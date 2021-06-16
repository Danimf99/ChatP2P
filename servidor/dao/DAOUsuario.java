package servidor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAOUsuario {

    private AccesoBD conector;
    private Connection conexion;

    public DAOUsuario(){
        conector = new AccesoBD();
        this.conexion = conector.getConexion();
    }

    public void registrarUsuario(String nombre, String contrasena){
        PreparedStatement insert = null;
        try{
            insert = conexion.prepareStatement("INSERT INTO usuario VALUES(?, ?)");
            insert.setString(1, nombre);
            insert.setString(2, contrasena);
            insert.executeUpdate();

        }
        catch(SQLException e) {
            System.out.println("Excepcion SQL. " + e.getMessage());
        }finally {
            try{
                insert.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean existeUsuario(String nombre, String contrasena){
        ResultSet rs = null;
        PreparedStatement sentenciaSQL = null;
        try {
             sentenciaSQL = conexion.prepareStatement("SELECT * FROM " +
                    "usuario WHERE nombre = ? AND contrasena = ?");

            sentenciaSQL.setString(1, nombre);
            sentenciaSQL.setString(2, contrasena);

            rs = sentenciaSQL.executeQuery();

            if(rs.next()) {
                return true;
            }

        }
        catch(SQLException e) {
            System.out.println("Excepcion SQL. " + e.getMessage());
        }finally {
            try {
                sentenciaSQL.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;

    }

    public boolean existeUsuario(String nombre){
        ResultSet rs = null;
        PreparedStatement sentenciaSQL = null;
        try {
            sentenciaSQL = conexion.prepareStatement("SELECT * FROM " +
                    "usuario WHERE nombre = ? ");

            sentenciaSQL.setString(1, nombre);

            rs = sentenciaSQL.executeQuery();

            if(rs.next()) {
                return true;
            }

        }
        catch(SQLException e) {
            System.out.println("Excepcion SQL. " + e.getMessage());
        }finally {
            try {
                sentenciaSQL.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;

    }

    public ArrayList<String> obtenerAmigos(String nombre){
        ArrayList<String> amigos = new ArrayList<>();
        PreparedStatement select = null;
        String query = "SELECT * FROM seramigo WHERE usuario1 = ? OR usuario2 = ?";
        ResultSet lista = null;

        try{
            select = conexion.prepareStatement(query);

            select.setString(1, nombre);
            select.setString(2, nombre);

            lista = select.executeQuery();
            String usuario1, usuario2;
            while(lista.next()){
                usuario1 = lista.getString("usuario1");
                usuario2 = lista.getString("usuario2");
                if(usuario1.equals(nombre)){
                    amigos.add(usuario2);
                }else{
                    amigos.add(usuario1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return amigos;
    }

    public void insertarSolicitud(String remitente, String receptor){
        PreparedStatement insert = null;
        try{
            insert = conexion.prepareStatement("INSERT INTO peticion VALUES(?, ?)");
            insert.setString(1, remitente);
            insert.setString(2, receptor);
            insert.executeUpdate();
        }
        catch(SQLException e) {
            System.out.println("Excepcion SQL. " + e.getMessage());
        }finally {
            try{
                insert.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> obtenerPeticiones(String nombre){
        ArrayList<String> solicitudes = new ArrayList<>();
        PreparedStatement select = null;
        ResultSet remitentes = null;
        String query = "SELECT remitente FROM peticion WHERE receptor = ?";
        try{
            select = conexion.prepareStatement(query);
            select.setString(1, nombre);

            remitentes = select.executeQuery();
            while(remitentes.next()){
                solicitudes.add(remitentes.getString("remitente"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return solicitudes;
    }

    public void aceptarSolicitud(String usuario, String aceptado){
        PreparedStatement delete = null;
        PreparedStatement insert = null;
        String deleteQuery = "DELETE FROM peticion WHERE remitente = ? AND receptor = ?";
        String insertQuery = "INSERT INTO seramigo VALUES(?,?)";


        try{
            conexion.setAutoCommit(false);

            delete = conexion.prepareStatement(deleteQuery);
            delete.setString(1, aceptado);
            delete.setString(2, usuario);

            delete.executeUpdate();

            insert = conexion.prepareStatement(insertQuery);
            insert.setString(1, usuario);
            insert.setString(2, aceptado);

            insert.executeUpdate();

            conexion.commit();
            conexion.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void rechazarSolicitud(String usuario, String rechazado){
        PreparedStatement delete = null;
        PreparedStatement insert = null;
        String deleteQuery = "DELETE FROM peticion WHERE remitente = ? AND receptor = ?";


        try{
            conexion.setAutoCommit(false);

            delete = conexion.prepareStatement(deleteQuery);
            delete.setString(1, rechazado);
            delete.setString(2, usuario);

            delete.executeUpdate();

            conexion.commit();
            conexion.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public boolean sonAmigos(String nombre1, String nombre2){
        PreparedStatement select = null;
        String query = "SELECT * FROM seramigo WHERE (usuario1 = ? AND usuario2 = ?) OR (usuario1 = ? AND usuario2 = ?)";
        ResultSet lista = null;

        try{
            select = conexion.prepareStatement(query);

            select.setString(1, nombre1);
            select.setString(2, nombre2);
            select.setString(3, nombre2);
            select.setString(4, nombre1);

            lista = select.executeQuery();
            if(lista.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void modificarDatos(String usuario, String contrasena){
        PreparedStatement insert = null;
        try{
            insert = conexion.prepareStatement("UPDATE usuario SET " +
                    "contrasena = ? WHERE nombre=? ");
            insert.setString(2, usuario);
            insert.setString(1, contrasena);
            insert.executeUpdate();

        }
        catch(SQLException e) {
            System.out.println("Excepcion SQL. " + e.getMessage());
        }finally {
            try{
                insert.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void eliminarAmigo(String usuario, String eliminado){
        PreparedStatement delete = null;
        try{
            delete = conexion.prepareStatement("DELETE FROM seramigo WHERE (usuario1=? " +
                    "AND usuario2=?) OR (usuario1=? AND usuario2=?)");
            delete.setString(1, usuario);
            delete.setString(2, eliminado);
            delete.setString(4, usuario);
            delete.setString(3, eliminado);
            delete.executeUpdate();

        }
        catch(SQLException e) {
            System.out.println("Excepcion SQL. " + e.getMessage());
        }finally {
            try{
                delete.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
