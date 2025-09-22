package org.palina.venta_ui.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.palina.venta_ui.service.ProductoService;
import org.palina.venta_ui.dto.Producto;
import org.palina.venta_ui.dto.ProductoDto;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PrincipalView {

    private BorderPane layout;
    private ObservableList<Producto> productos;
    private ObservableList<Producto> productosSeleccionados;
    private TableView<Producto> tablaProductos;
    private TableView<Producto> tablaSeleccionados;
    private Label totalCantidadLabel;
    private Label totalImporteLabel;
    private ComboBox<String> comboTipoVenta;
    private TextField campoCredito;

    public void mostrar(String usuario) {
        layout = new BorderPane();

        // Estilo Hello Kitty
        ImageView kittyIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/helloKitie.png")));
        kittyIcon.setFitWidth(50);
        kittyIcon.setFitHeight(50);
        Label usuarioLabel = new Label("Usuario: " + usuario);
        usuarioLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #d63384;");

        HBox topBar = new HBox(kittyIcon, new Region(), usuarioLabel);
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        MenuBar menuBar = new MenuBar();
        Menu inventarioMenu = new Menu("Inventario");
        Menu ventaMenu = new Menu("Venta");
        MenuItem productosItem = new MenuItem("Productos");
        MenuItem generarVentaItem = new MenuItem("Generar Venta");

        inventarioMenu.getItems().add(productosItem);
        ventaMenu.getItems().add(generarVentaItem);
        menuBar.getMenus().addAll(inventarioMenu, ventaMenu);

        productosItem.setOnAction(e -> mostrarListadoProductos());
        generarVentaItem.setOnAction(e -> mostrarListadoProductos());
        productosItem.setOnAction(e -> abrirInventarioProductos());

        VBox topContainer = new VBox(menuBar, topBar);
        layout.setTop(topContainer);

        Scene scene = new Scene(layout, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("/styles/principal.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Principal - Hello Kitty");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    private void mostrarListadoProductos() {
        ProductoService productoService = new ProductoService(null);
        List<ProductoDto> list = productoService.getProducts();

        List<Producto> products = list.stream().map(p->{
            Producto p1 = new Producto(p.getCodigo(), p.getDescripcion(), p.getCategoria1(), p.getCategoria2(), p.getCodigoBarras(), p.getPrecioVenta(), 0.0,p.getStockActual());
            return p1;
        }).collect(Collectors.toList());

        productos = FXCollections.observableArrayList(products);
        productosSeleccionados = FXCollections.observableArrayList();

        // Filtros
        TextField filtroCodigo = new TextField();
        TextField filtroDescripcion = new TextField();
        TextField filtroCategoria = new TextField();
        TextField filtroBarras = new TextField();
        filtroCodigo.setPromptText("Código");
        filtroDescripcion.setPromptText("Descripción");
        filtroCategoria.setPromptText("Categoría");
        filtroBarras.setPromptText("Código Barras");

        HBox filtros = new HBox(10, filtroCodigo, filtroDescripcion, filtroCategoria, filtroBarras);
        filtros.setPadding(new Insets(10));

        tablaProductos = new TableView<>(productos);
        tablaProductos.getColumns().addAll(
                crearColumna("Código", "codigo"),
                crearColumna("Descripción", "descripcion"),
                crearColumna("Categoría 1", "categoria1"),
                crearColumna("Categoría 2", "categoria2"),
                crearColumna("Código Barras", "codigoBarras"),
                crearColumna("Precio", "precioVenta"),
                crearColumna("Stock", "stockActual")
        );

        filtroCodigo.textProperty().addListener((obs, o, n) -> aplicarFiltro(filtroCodigo, filtroDescripcion, filtroCategoria, filtroBarras));
        filtroDescripcion.textProperty().addListener((obs, o, n) -> aplicarFiltro(filtroCodigo, filtroDescripcion, filtroCategoria, filtroBarras));
        filtroCategoria.textProperty().addListener((obs, o, n) -> aplicarFiltro(filtroCodigo, filtroDescripcion, filtroCategoria, filtroBarras));
        filtroBarras.textProperty().addListener((obs, o, n) -> aplicarFiltro(filtroCodigo, filtroDescripcion, filtroCategoria, filtroBarras));

        tablaProductos.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Producto p = tablaProductos.getSelectionModel().getSelectedItem();
                if (p != null && !productosSeleccionados.contains(p)) {
                    Producto copia = new Producto(p.getCodigo(), p.getDescripcion(), p.getCategoria1(), p.getCategoria2(), p.getCodigoBarras(), p.getPrecioVenta(), 0.0, p.getStockActual());
                    copia.setCantidad(1);
                    productosSeleccionados.add(copia);
                }
            }
        });

        Label labelSeleccionados = new Label("Productos seleccionados:");
        tablaSeleccionados = new TableView<>(productosSeleccionados);
        tablaSeleccionados.setEditable(true);
        tablaSeleccionados.getColumns().addAll(
                crearColumna("Código", "codigo"),
                crearColumna("Descripción", "descripcion"),
                crearColumnaCantidad(),
                crearColumna("Precio", "precioVenta"),
                crearColumna("Stock", "stockActual")
        );

        totalCantidadLabel = new Label();
        totalImporteLabel = new Label();
        actualizarTotales();

        productosSeleccionados.addListener((javafx.collections.ListChangeListener<Producto>) c -> actualizarTotales());

        HBox totalesBox = new HBox(30, totalCantidadLabel, totalImporteLabel);
        totalesBox.setPadding(new Insets(10));
        totalesBox.setAlignment(Pos.CENTER_LEFT);

        // Tipo de venta
        comboTipoVenta = new ComboBox<>();
        comboTipoVenta.getItems().addAll("Contado", "Crédito");
        comboTipoVenta.setValue("Contado");

        campoCredito = new TextField();
        campoCredito.setPromptText("Total crédito");
        campoCredito.setDisable(true);

        comboTipoVenta.setOnAction(e -> {
            if ("Crédito".equals(comboTipoVenta.getValue())) {
                campoCredito.setDisable(false);
                campoCredito.setText(String.format("%.2f", calcularTotalImporte()));
            } else {
                campoCredito.setDisable(true);
                campoCredito.setText("");
            }
        });

        HBox ventaTipoBox = new HBox(10, new Label("Tipo de venta:"), comboTipoVenta, campoCredito);
        ventaTipoBox.setPadding(new Insets(10));
        ventaTipoBox.setAlignment(Pos.CENTER_LEFT);

        Button btnVenta = new Button("Generar Venta");
        btnVenta.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
        btnVenta.setOnAction(e -> generarVenta());

        VBox contenedorSeleccionados = new VBox(10, labelSeleccionados, tablaSeleccionados, totalesBox, ventaTipoBox, btnVenta);
        contenedorSeleccionados.setPadding(new Insets(10));

        VBox layoutPrincipal = new VBox(10, filtros, tablaProductos, contenedorSeleccionados);
        layoutPrincipal.setPadding(new Insets(20));
        layout.setCenter(layoutPrincipal);
    }

    private void abrirInventarioProductos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/inventario/InventarioView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Inventario de Productos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void aplicarFiltro(TextField codigo, TextField descripcion, TextField categoria, TextField barras) {
        String cod = codigo.getText().toLowerCase();
        String desc = descripcion.getText().toLowerCase();
        String cat = categoria.getText().toLowerCase();
        String cb = barras.getText().toLowerCase();

        tablaProductos.setItems(productos.filtered(p ->
                p.getCodigo().toLowerCase().contains(cod) &&
                        p.getDescripcion().toLowerCase().contains(desc) &&
                        (p.getCategoria1() + " " + p.getCategoria2()).toLowerCase().contains(cat) &&
                        p.getCodigoBarras().toLowerCase().contains(cb)
        ));
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
            int cantidad = e.getNewValue() != null && e.getNewValue() > 0 ? e.getNewValue() : 1;
            producto.setCantidad(cantidad);
            actualizarTotales();
        });
        return col;
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

    private void generarVenta() {
        if (productosSeleccionados.isEmpty()) {
            mostrarAlerta("Debe seleccionar al menos un producto.");
            return;
        }

        String tipoVenta = comboTipoVenta.getValue();
        double totalCompra = productosSeleccionados.stream().mapToDouble(p -> p.getCantidad() * p.getPrecioVenta()).sum();

        if ("Crédito".equals(tipoVenta)) {
            try {
                double montoCredito = Double.parseDouble(campoCredito.getText());
                if (montoCredito > totalCompra) {
                    mostrarAlerta("El monto ingresado no puede ser mayor al total de la compra.");
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Monto de crédito inválido.");
                return;
            }
        }

        StringBuilder ticket = new StringBuilder();
        ticket.append("      *** TIENDA HELLO KITTY ***\n\n");
        for (Producto p : productosSeleccionados) {
            ticket.append(String.format("%s x%d  $%.2f\n", p.getDescripcion(), p.getCantidad(), p.getCantidad() * p.getPrecioVenta()));
        }
        ticket.append("\n-------------------------------\n");
        ticket.append(String.format("TOTAL: $%.2f\n", totalCompra));
        ticket.append("Tipo de venta: ").append(tipoVenta).append("\n\n");
        ticket.append("Gracias por su compra!\n");

        imprimirTicketConJavaFX(ticket.toString());

        productosSeleccionados.clear();
        comboTipoVenta.setValue("Contado");
        campoCredito.clear();
        campoCredito.setVisible(false);
        campoCredito.setDisable(true);
        actualizarTotales();
        mostrarAlerta("Venta realizada");
    }



    private void imprimirTicketConJavaFX(String contenido) {
        TextFlow textFlow = new TextFlow();
        for (String line : contenido.split("\n")) {
            textFlow.getChildren().add(new Text(line + "\n"));
        }

        Scene printScene = new Scene(textFlow);

        // Buscar impresora específica (puedes cambiar el nombre si deseas una en particular)
        Printer impresora = Printer.getAllPrinters()
                .stream()
               // .filter(p -> p.getName().contains("local"))
                .filter(p -> p.getName().contains("EPSON"))
                .findFirst()
                .orElse(null);

        if (impresora == null) {
            System.err.println("❌ No se encontró impresora compatible.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob(impresora);

        if (job == null) {
            System.err.println("❌ No se pudo crear el trabajo de impresión.");
            return;
        }

        System.out.println("🖨️ Imprimiendo en: " + impresora.getName());

        boolean success = job.printPage(textFlow);
        if (success) {
            job.endJob();
            System.out.println("✅ Impresión completada.");
        } else {
            System.err.println("⚠️ Falló la impresión.");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}