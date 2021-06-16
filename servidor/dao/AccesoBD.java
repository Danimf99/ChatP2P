package servidor.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccesoBD {

    private Connection conexion;

    public AccesoBD(){
        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306" +
                    "/comdis", "comdis", "comdis");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }
}
