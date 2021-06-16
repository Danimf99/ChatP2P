import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servidor.ServidorImpl;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorAplicaciones {


    public static void main(String[] args) {
        String registryURL;
        int portnum = 5004;

        try {
            startRegistry(portnum);
            registryURL = "rmi://localhost:" + portnum + "/servidor";

            ServidorImpl objetoExportado = new ServidorImpl();
            Naming.rebind(registryURL, objetoExportado);

            System.out.println("Servidor listo.");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static void startRegistry(int portNum) throws RemoteException {
        try{
            Registry registry = LocateRegistry.getRegistry(portNum);
            registry.list();
        }catch (RemoteException e){
            LocateRegistry.createRegistry(portNum);
        }
    }
}
