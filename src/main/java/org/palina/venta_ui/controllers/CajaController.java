package org.palina.venta_ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.palina.venta_ui.dto.CajaDto;
import org.palina.venta_ui.service.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Controller
public class CajaController implements Initializable {

    @Autowired
    private CajaService cajaService;

    // 🔹 Label de saldo total
    @FXML private Label lblSaldoActual;

    // 🔹 Tabla de totales de caja
    @FXML private TableView<TotalCajaModel> tablaTotalesCaja;
    @FXML private TableColumn<TotalCajaModel, String> colTipoCaja;
    @FXML private TableColumn<TotalCajaModel, String> colSaldoCaja;

    // 🔹 Tabla de ventas realizadas
    @FXML private TableView<VentaModel> tablaVentas;
    @FXML private TableColumn<VentaModel, String> colFechaVenta;
    @FXML private TableColumn<VentaModel, String> colProductoVenta;
    @FXML private TableColumn<VentaModel, String> colImporteVenta;

    private final ObservableList<TotalCajaModel> totalesCajaList = FXCollections.observableArrayList();
    private final ObservableList<VentaModel> ventasList = FXCollections.observableArrayList();

    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas de la tabla de caja
        colTipoCaja.setCellValueFactory(data -> data.getValue().tipoCajaProperty());
        colSaldoCaja.setCellValueFactory(data -> data.getValue().saldoFormateadoProperty());

        // Configurar columnas de la tabla de ventas
        colFechaVenta.setCellValueFactory(data -> data.getValue().fechaProperty());
        colProductoVenta.setCellValueFactory(data -> data.getValue().productoProperty());
        colImporteVenta.setCellValueFactory(data -> data.getValue().importeFormateadoProperty());

        // Cargar datos iniciales
        actualizarVista();
    }

    @FXML
    private void actualizarVista() {
        // 🔸 Datos de ejemplo (puedes reemplazar con datos desde la BD)
        totalesCajaList.clear();

        List<CajaDto> cajas = cajaService.obtenerSaldoActual();

        double totalGeneral = cajas.stream()
                .mapToDouble(c -> c.getMonto().doubleValue())
                .sum();
        lblSaldoActual.setText("$" + totalGeneral);

        List<TotalCajaModel> infoCajas = cajas.stream()
                .map(c ->{
                    return new TotalCajaModel(c.getTipo(), c.getMonto().doubleValue());
                }).toList();

        totalesCajaList.addAll(infoCajas);
        tablaTotalesCaja.setItems(totalesCajaList);

        ventasList.clear();
        ventasList.addAll(
                new VentaModel(LocalDateTime.now().minusHours(1), "Pelota Hello Kitty", 150.0),
                new VentaModel(LocalDateTime.now().minusHours(2), "Mochila Rosa", 450.0),
                new VentaModel(LocalDateTime.now().minusDays(1), "Taza de colección", 200.0)
        );
        tablaVentas.setItems(ventasList);

        // 🔸 Calcular saldo total
        double total = totalesCajaList.stream()
                .mapToDouble(TotalCajaModel::getSaldo)
                .sum();

        lblSaldoActual.setText(formatoMoneda.format(total));
    }

    // 🔹 Modelo interno para total de caja
    public static class TotalCajaModel {
        private final javafx.beans.property.SimpleStringProperty tipoCaja;
        private final javafx.beans.property.SimpleDoubleProperty saldo;

        public TotalCajaModel(String tipoCaja, Double saldo) {
            this.tipoCaja = new javafx.beans.property.SimpleStringProperty(tipoCaja);
            this.saldo = new javafx.beans.property.SimpleDoubleProperty(saldo);
        }

        public String getTipoCaja() { return tipoCaja.get(); }
        public javafx.beans.property.SimpleStringProperty tipoCajaProperty() { return tipoCaja; }

        public Double getSaldo() { return saldo.get(); }
        public javafx.beans.property.SimpleDoubleProperty saldoProperty() { return saldo; }

        public String getSaldoFormateado() {
            return NumberFormat.getCurrencyInstance(new Locale("es", "MX")).format(getSaldo());
        }

        public javafx.beans.property.SimpleStringProperty saldoFormateadoProperty() {
            return new javafx.beans.property.SimpleStringProperty(getSaldoFormateado());
        }
    }

    // 🔹 Modelo interno para ventas
    public static class VentaModel {
        private final javafx.beans.property.SimpleStringProperty fecha;
        private final javafx.beans.property.SimpleStringProperty producto;
        private final javafx.beans.property.SimpleDoubleProperty importe;

        public VentaModel(LocalDateTime fecha, String producto, Double importe) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.fecha = new javafx.beans.property.SimpleStringProperty(fecha.format(formatter));
            this.producto = new javafx.beans.property.SimpleStringProperty(producto);
            this.importe = new javafx.beans.property.SimpleDoubleProperty(importe);
        }

        public String getFecha() { return fecha.get(); }
        public javafx.beans.property.SimpleStringProperty fechaProperty() { return fecha; }

        public String getProducto() { return producto.get(); }
        public javafx.beans.property.SimpleStringProperty productoProperty() { return producto; }

        public Double getImporte() { return importe.get(); }
        public javafx.beans.property.SimpleDoubleProperty importeProperty() { return importe; }

        public String getImporteFormateado() {
            return NumberFormat.getCurrencyInstance(new Locale("es", "MX")).format(getImporte());
        }

        public javafx.beans.property.SimpleStringProperty importeFormateadoProperty() {
            return new javafx.beans.property.SimpleStringProperty(getImporteFormateado());
        }
    }
}
