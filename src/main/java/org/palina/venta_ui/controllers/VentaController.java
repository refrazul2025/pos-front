package org.palina.venta_ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.converter.IntegerStringConverter;
import org.palina.venta_ui.service.ProductoService;
import org.palina.venta_ui.dto.Producto;
import org.palina.venta_ui.dto.ProductoDto;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class VentaController implements Initializable {

    @FXML private TextField filtroCodigo, filtroDescripcion, filtroCategoria, filtroBarras;
    @FXML private TableView<Producto> tablaProductos, tablaSeleccionados;
    @FXML private Label totalCantidadLabel, totalImporteLabel;
    @FXML private ComboBox<String> comboTipoVenta;
    @FXML private TextField campoCredito;

    private ObservableList<Producto> productos;
    private ObservableList<Producto> productosSeleccionados;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarProductos();

        comboTipoVenta.getItems().addAll("Contado", "Crédito");
        comboTipoVenta.setValue("Contado");
        campoCredito.setDisable(true);

        comboTipoVenta.setOnAction(e -> {
            if ("Crédito".equals(comboTipoVenta.getValue())) {
                campoCredito.setDisable(false);
                campoCredito.setText(String.format("%.2f", calcularTotalImporte()));
            } else {
                campoCredito.setDisable(true);
                campoCredito.clear();
            }
        });

        productosSeleccionados.addListener((javafx.collections.ListChangeListener<Producto>) c -> actualizarTotales());

        filtroCodigo.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro());
        filtroDescripcion.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro());
        filtroCategoria.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro());
        filtroBarras.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro());
    }

    private void cargarProductos() {
        ProductoService service = new ProductoService(null);
        List<ProductoDto> dtos = service.getProducts();

        productos = FXCollections.observableArrayList(
                dtos.stream().map(p -> new Producto(
                        p.getCodigo(), p.getDescripcion(), p.getCategoria1(), p.getCategoria2(),
                        p.getCodigoBarras(), p.getPrecioVenta(), p.getPrecioCompra(), p.getStockActual())).collect(Collectors.toList())
        );
        productosSeleccionados = FXCollections.observableArrayList();

        tablaProductos.setItems(productos);
        tablaSeleccionados.setItems(productosSeleccionados);

        tablaProductos.getColumns().addAll(
                crearColumna("Código", "codigo"),
                crearColumna("Descripción", "descripcion"),
                crearColumna("Categoría 1", "categoria1"),
                crearColumna("Categoría 2", "categoria2"),
                crearColumna("Código Barras", "codigoBarras"),
                crearColumna("Precio", "precioVenta"),
                crearColumna("Stock", "stockActual")
        );

        tablaSeleccionados.getColumns().addAll(
                crearColumna("Código", "codigo"),
                crearColumna("Descripción", "descripcion"),
                crearColumnaCantidad(),
                crearColumna("Precio", "precioVenta"),
                crearColumna("Stock", "stockActual")
        );

        tablaProductos.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Producto p = tablaProductos.getSelectionModel().getSelectedItem();
                if (p != null && !productosSeleccionados.contains(p)) {
                    Producto copia = new Producto(p.getCodigo(), p.getDescripcion(), p.getCategoria1(),
                            p.getCategoria2(), p.getCodigoBarras(), p.getPrecioVenta(), 0.0, p.getStockActual());
                    copia.setCantidad(1);
                    productosSeleccionados.add(copia);
                }
            }
        });

        tablaSeleccionados.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                Producto seleccionado = tablaSeleccionados.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    productosSeleccionados.remove(seleccionado);
                }
            }
        });
    }

    private TableColumn<Producto, ?> crearColumna(String titulo, String propiedad) {
        TableColumn<Producto, Object> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        return col;
    }

    private TableColumn<Producto, Integer> crearColumnaCantidad() {
        TableColumn<Producto, Integer> col = new TableColumn<>("Cantidad");
        col.setCellValueFactory(cell -> cell.getValue().cantidadProperty().asObject());
        col.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        col.setOnEditCommit(e -> {
            Producto producto = e.getRowValue();
            int cantidad = e.getNewValue() > 0 ? e.getNewValue() : 1;
            producto.setCantidad(cantidad);
            actualizarTotales();
        });
        return col;
    }

    private void aplicarFiltro() {
        tablaProductos.setItems(productos.filtered(p ->
                p.getCodigo().toLowerCase().contains(filtroCodigo.getText().toLowerCase()) &&
                        p.getDescripcion().toLowerCase().contains(filtroDescripcion.getText().toLowerCase()) &&
                        (p.getCategoria1() + " " + p.getCategoria2()).toLowerCase().contains(filtroCategoria.getText().toLowerCase()) &&
                        p.getCodigoBarras().toLowerCase().contains(filtroBarras.getText().toLowerCase())
        ));
    }

    private void actualizarTotales() {
        int totalCantidad = productosSeleccionados.stream().mapToInt(Producto::getCantidad).sum();
        double totalImporte = calcularTotalImporte();

        totalCantidadLabel.setText("Total productos: " + totalCantidad);
        totalImporteLabel.setText(String.format("Total importe: $%.2f", totalImporte));
    }

    private double calcularTotalImporte() {
        return productosSeleccionados.stream()
                .mapToDouble(p -> p.getCantidad() * p.getPrecioVenta())
                .sum();
    }

    @FXML
    private void generarVenta() {
        if (productosSeleccionados.isEmpty()) {
            mostrarAlerta("Debe seleccionar al menos un producto.");
            return;
        }

        String tipoVenta = comboTipoVenta.getValue();
        double total = calcularTotalImporte();

        if ("Crédito".equals(tipoVenta)) {
            try {
                double monto = Double.parseDouble(campoCredito.getText());
                if (monto > total) {
                    mostrarAlerta("El monto ingresado no puede ser mayor al total.");
                    return;
                }
            } catch (NumberFormatException ex) {
                mostrarAlerta("Monto inválido.");
                return;
            }
        }

        mostrarAlerta("Venta realizada. Total: $" + String.format("%.2f", total));
        productosSeleccionados.clear();
        comboTipoVenta.setValue("Contado");
        campoCredito.clear();
        campoCredito.setDisable(true);
        filtroCodigo.clear();
        filtroDescripcion.clear();
        filtroCategoria.clear();
        filtroBarras.clear();
        actualizarTotales();
    }
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
