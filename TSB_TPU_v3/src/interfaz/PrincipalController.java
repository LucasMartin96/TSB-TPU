package interfaz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import negocio.Agrupaciones;
import negocio.Region;
import negocio.Regiones;
import negocio.Resultados;

import java.io.File;

public class PrincipalController {
    public Button btnCargar;
    public Button btnCambiar;
    public Label lblCarpeta;
    public ListView lvwResultados;
    public ComboBox cboDistritos;
    public ComboBox cboSecciones;

    public void cargar(ActionEvent actionEvent) {

        ObservableList ol;

        String carpeta = lblCarpeta.getText();
        Agrupaciones agrupaciones = new Agrupaciones(carpeta);
        Regiones regiones = new Regiones(carpeta);

        ol = FXCollections.observableArrayList(regiones.getDistritos());
        cboDistritos.setItems(ol);

        Resultados resultados = new Resultados(agrupaciones, carpeta);

        ol = FXCollections.observableArrayList(resultados.getResultados("00"));
        lvwResultados.setItems(ol);


    }

    public void cambiarUbicacion(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);
        if (file != null) {
            lblCarpeta.setText(file.getParent());
        }
    }

    public void elegirDistrito(ActionEvent actionEvent) {
        Region region = (Region)cboDistritos.getValue();
        if(region !=null)
            {
            ObservableList ol = FXCollections.observableArrayList(region.getSubregiones());
            cboSecciones.setItems(ol);
            }
    }
}
