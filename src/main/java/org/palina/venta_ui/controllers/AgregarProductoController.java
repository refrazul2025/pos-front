package org.palina.venta_ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.palina.venta_ui.service.ProductoService;
import org.palina.venta_ui.dto.Producto;
import org.palina.venta_ui.dto.ProductoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class AgregarProductoController implements Initializable {

    @Autowired
    private ProductoService productoService;

    @FXML private TableView<Producto> tablaProductos;

    @FXML private TextField tfCodigo;
    @FXML private TextField tfDescripcion;
    @FXML private TextField tfCategoria1;
    @FXML private TextField tfCategoria2;
    @FXML private TextField tfCodigoBarras;
    @FXML private TextField tfPrecioCompra;
    @FXML private TextField tfPrecioVenta;
    @FXML private TextField tfStockActual;
    @FXML private Button btnGuardar;

    private ObservableList<Producto> productos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarProductos();

        // Configurar columnas de tabla
        tablaProductos.getColumns().setAll(
                crearColumna("Código", "codigo"),
                crearColumna("Descripción", "descripcion"),
                crearColumna("Categoría 1", "categoria1"),
                crearColumna("Categoría 2", "categoria2"),
                crearColumna("Código Barras", "codigoBarras"),
                crearColumna("Precio Venta", "precioVenta"),
                crearColumna("Precio Compra", "precioCompra"),
                crearColumna("Stock", "stockActual")
        );
    }

    private void cargarProductos() {
        List<ProductoDto> dtos = productoService.getProducts();
        productos = FXCollections.observableArrayList(
                dtos.stream()
                        .map(p -> new Producto(
                                p.getCodigo(),
                                p.getDescripcion(),
                                p.getCategoria1(),
                                p.getCategoria2(),
                                p.getCodigoBarras(),
                                p.getPrecioVenta(),
                                p.getPrecioCompra(),
                                p.getStockActual()
                        ))
                        .collect(Collectors.toList())
        );
        tablaProductos.setItems(productos);
    }

    @FXML
    private void guardarProducto() {
        try {
            ProductoDto dto = new ProductoDto();
            dto.setCodigo(tfCodigo.getText());
            dto.setDescripcion(tfDescripcion.getText());
            dto.setCategoria1(tfCategoria1.getText());
            dto.setCategoria2(tfCategoria2.getText());
            dto.setCodigoBarras(tfCodigoBarras.getText());
            dto.setPrecioVenta(Double.parseDouble(tfPrecioVenta.getText()));
            dto.setPrecioCompra(Double.parseDouble(tfPrecioCompra.getText()));
            dto.setStockActual(Integer.parseInt(tfStockActual.getText()));

            productoService.createProduct(dto);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", null, "Producto agregado correctamente.");

            // Refrescar tabla
            cargarProductos();

            // Limpiar formulario
            tfCodigo.clear();
            tfDescripcion.clear();
            tfCategoria1.clear();
            tfCategoria2.clear();
            tfCodigoBarras.clear();
            tfPrecioCompra.clear();
            tfPrecioVenta.clear();
            tfStockActual.clear();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "Precio o stock inválido.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", null, "No se pudo guardar el producto: " + e.getMessage());
        }
    }

    private TableColumn<Producto, ?> crearColumna(String titulo, String propiedad) {
        TableColumn<Producto, Object> col = new TableColumn<>(titulo);
        col.setCellValueFactory(new PropertyValueFactory<>(propiedad));
        return col;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
