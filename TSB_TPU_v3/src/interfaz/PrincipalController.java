package interfaz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
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
    public ComboBox cboCircuito;
    public ComboBox cboMesas;
    private Resultados resultados;

    /*public void cargar(ActionEvent actionEvent) {

        ObservableList ol;

        String carpeta = lblCarpeta.getText();
        Agrupaciones agrupaciones = new Agrupaciones(carpeta);
        Regiones regiones = new Regiones(carpeta);

        ol = FXCollections.observableArrayList(regiones.getDistritos());
        cboDistritos.setItems(ol);

        Resultados resultados = new Resultados(agrupaciones, carpeta);

        ol = FXCollections.observableArrayList(resultados.getResultados("00"));
        lvwResultados.setItems(ol);

        new Alert(Alert.AlertType.INFORMATION, "Datos Cargados", ButtonType.OK).show();


    }*/

    public void cargar(ActionEvent actionEvent) {
        String carpeta = lblCarpeta.getText();

        Agrupaciones agrupaciones = new Agrupaciones(carpeta);

        Regiones regiones = new Regiones(carpeta);
        cboDistritos.setItems(FXCollections.observableArrayList(regiones.getPais().getSubregiones()));

        resultados = new Resultados(agrupaciones, regiones.getPais(), carpeta);
        mostrarResultadosRegion(regiones.getPais().getCodigo());
        new Alert(Alert.AlertType.INFORMATION, "Datos Cargados", ButtonType.OK).show();
    }

    public void cambiarUbicacion(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);
        if (file != null) {
            lblCarpeta.setText(file.getParent());
        }
    }

    private void mostrarResultadosRegion(String codRegion) {
        lvwResultados.getItems().clear();
        ObservableList ol = FXCollections.observableArrayList(resultados.getResultados(codRegion));
        lvwResultados.setItems(ol);
    }

    public void elegirDistrito(ActionEvent actionEvent) {
        Region r = (Region) cboDistritos.getValue();
        if (r != null) {
            cboSecciones.setItems(FXCollections.observableArrayList(r.getSubregiones()));
            mostrarResultadosRegion(r.getCodigo());
        } else
            cboSecciones.setItems(null);
            }

    public void elegirSeccion(ActionEvent actionEvent) {
        Region r = (Region) cboSecciones.getValue();
        if (r != null) {
            cboCircuito.setItems(FXCollections.observableArrayList(r.getSubregiones()));
            mostrarResultadosRegion(r.getCodigo());
        } else
            cboCircuito.setItems(null);
    }

    public void elegirCircuito(ActionEvent actionEvent) {
        Region r = (Region) cboCircuito.getValue();
        if (r != null) {
            cboMesas.setItems(FXCollections.observableArrayList(r.getSubregiones()));
            mostrarResultadosRegion(r.getCodigo());
        }
        else
            cboMesas.setItems(null);
    }


    public void elegirMesa(ActionEvent actionEvent) {
            Region r = (Region) cboMesas.getValue();
            if (r != null) {
                mostrarResultadosRegion(r.getCodigo());
            }
    }
}
