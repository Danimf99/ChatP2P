package cliente.controladores;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class Alerta {
    private Alert alert;

    public Alerta(Alert.AlertType tipo,String mensaje, String textoCabecera,
                  Window window){
        alert = new Alert(tipo);
        alert.setTitle("CompDis");
        alert.setHeaderText(textoCabecera);
        alert.setContentText(mensaje);
        alert.initOwner(window);
        alert.showAndWait();
    }
}
