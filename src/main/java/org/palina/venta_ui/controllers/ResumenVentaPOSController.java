package org.palina.venta_ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.palina.venta_ui.dto.Producto;

import java.util.List;

public class ResumenVentaPOSController {

    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, Integer> colCantidad;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Double> colSubtotal;

    @FXML private Label totalProductosLabel;
    @FXML private Label totalImporteLabel;
    @FXML private Label tipoPagoLabel;
    @FXML private Label tipoVentaLabel;
    @FXML private Label clienteLabel;

    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML private TextField pagoRecibidoField;
    @FXML private Label cambioLabel;

    private boolean confirmado = false;
    private double totalImporte;

    public void setDatos(List<Producto> productos, String tipoPago, String tipoVenta, String cliente) {

        totalProductosLabel.setText(String.valueOf(productos.size()));
        totalImporte = productos.stream().mapToDouble(p -> p.getCantidad() * p.getPrecioVenta()).sum();
        totalImporteLabel.setText(String.format("%.2f", totalImporte));

        tipoPagoLabel.setText(tipoPago);
        tipoVentaLabel.setText(tipoVenta);
        clienteLabel.setText(cliente != null ? cliente : "-");
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    @FXML
    private void initialize() {
        confirmButton.setOnAction(e -> {
            confirmado = true;
            closeWindow();
        });

        cancelButton.setOnAction(e -> {
            confirmado = false;
            closeWindow();
        });

        pagoRecibidoField.textProperty().addListener((obs, oldV, newV) -> actualizarCambio());
    }

    private void closeWindow() {
        confirmButton.getScene().getWindow().hide();
    }

    private void actualizarCambio() {
        try {
            double pago = Double.parseDouble(pagoRecibidoField.getText());
            double cambio = pago - totalImporte;
            cambioLabel.setText(String.format("%.2f", Math.max(cambio, 0)));
        } catch (NumberFormatException e) {
            cambioLabel.setText("0.00");
        }
    }
}
