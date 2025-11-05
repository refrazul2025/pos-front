package org.palina.venta_ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.palina.venta_ui.dto.*;
import org.palina.venta_ui.service.ProductoService;
import org.palina.venta_ui.service.VentaService;
import org.palina.venta_ui.util.TicketPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Controller
public class VentaController implements Initializable, PrincipalSection {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @FXML private TextField filtroCodigo, filtroDescripcion, filtroCategoria, filtroBarras;
    @FXML private TableView<Producto> tablaProductos, tablaSeleccionados;

    @FXML private Label totalProductosValue;
    @FXML private Label totalImporteValue;
    @FXML private ComboBox<String> comboTipoPago;
    @FXML private ComboBox<String> comboTipoVenta;
    @FXML private TextField clienteField;
    @FXML private Button generarVentaButton;
    @FXML private TextField montoApartadoField;

    //Formulario producto
    @FXML private VBox formAgregarProducto;
    @FXML private Button mostrarFormProductoButton;
    @FXML private Button guardarProductoButton;
    @FXML private Button cancelarProductoButton;

    @FXML private TextField nuevaDescripcionField;
    @FXML private TextField nuevaCategoriaField;
    @FXML private TextField nuevoPrecioField;

    private ObservableList<Producto> productos;
    private ObservableList<Producto> productosSeleccionados;
    private boolean resumenAbierto = false;

    private UserDto usuario;
    private OutletDto tienda;

    @Override
    public void setUsuario(UserDto usuario) {
        this.usuario = usuario;
    }

    @Override
    public void setTienda(OutletDto tienda) {
        this.tienda = tienda;
    }

    @Override
    public void initData() {
        cargarProductos();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboTipoVenta.valueProperty().addListener((obs, oldValue, newValue) -> {
            if ("Apartado".equalsIgnoreCase(newValue)) {
                montoApartadoField.setDisable(false);
            } else {
                montoApartadoField.clear();
                montoApartadoField.setDisable(true);
            }
        });

        productos = FXCollections.observableArrayList();
        productosSeleccionados = FXCollections.observableArrayList();
        productosSeleccionados.addListener((javafx.collections.ListChangeListener<Producto>) c -> actualizarTotales());
        comboTipoPago.setValue("Efectivo");
        comboTipoVenta.setValue("Contado");

        generarVentaButton.setOnAction(e -> generarVenta());
        filtroCodigo.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro());
        filtroDescripcion.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro());
        filtroCategoria.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro());
        filtroBarras.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro());

        //Formulario producto
        mostrarFormProductoButton.setOnAction(e -> {
            formAgregarProducto.setVisible(true);
            formAgregarProducto.setManaged(true);
        });

        cancelarProductoButton.setOnAction(e -> {
            formAgregarProducto.setVisible(false);
            formAgregarProducto.setManaged(false);
            limpiarFormulario();
        });

        guardarProductoButton.setOnAction(e -> {
            agregarProductoRapido();
            limpiarFormulario();
            formAgregarProducto.setVisible(false);
            formAgregarProducto.setManaged(false);
        });
    }

    private void cargarProductos() {
        List<ProductoDto> dtos = productoService.getProducts(tienda);

        productos = FXCollections.observableArrayList(
                dtos.stream().map(p -> new Producto(
                                p.getCode(), p.getDescription(), p.getCategory1(), p.getCategory2(),
                                p.getBarcode(), p.getSalePrice(), p.getPurchasePrice(), p.getCurrentStock()))
                        .collect(toList())
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

        tablaSeleccionados.setEditable(true);
        tablaSeleccionados.getColumns().addAll(
                crearColumna("Código", "codigo"),
                crearColumna("Descripción", "descripcion"),
                crearColumnaCantidad(),
                crearColumnaPrecioEditable(),
                crearColumna("Categoria2", "categoria2")
        );

        tablaProductos.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Producto p = tablaProductos.getSelectionModel().getSelectedItem();
                if (p != null) {
                    boolean yaExiste = productosSeleccionados.stream()
                            .anyMatch(prod -> prod.getCodigo().equals(p.getCodigo()));
                    if (!yaExiste) {
                        Producto copia = new Producto(
                                p.getCodigo(), p.getDescripcion(), p.getCategoria1(),
                                p.getCategoria2(), p.getCodigoBarras(),
                                p.getPrecioVenta(), 0.0, p.getStockActual()
                        );
                        copia.setCantidad(1);
                        productosSeleccionados.add(copia);
                        actualizarTotales();
                    }
                }
            }
        });

        tablaSeleccionados.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                Producto seleccionado = tablaSeleccionados.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    productosSeleccionados.remove(seleccionado);
                    actualizarTotales();
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

    private TableColumn<Producto, Double> crearColumnaPrecioEditable() {
        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");

        // Usa el valor inicial del producto
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));

        // Permitir edición con conversión Double <-> String
        colPrecio.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object != null ? String.format("%.2f", object) : "";
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }));

        // Guardar el nuevo valor en el producto y refrescar tabla + totales
        colPrecio.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            producto.setPrecioVenta(event.getNewValue() != null ? event.getNewValue() : 0.0);
            tablaSeleccionados.refresh();
            actualizarTotales(); // <- asegúrate que ya tienes este método
        });

        return colPrecio;
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
        int totalProductos = productosSeleccionados.stream().mapToInt(Producto::getCantidad).sum();
        double totalImporte = calcularTotalImporte();

        totalProductosValue.setText(String.valueOf(totalProductos));
        totalImporteValue.setText(String.format("%.2f", totalImporte));
    }

    private double calcularTotalImporte() {
        return productosSeleccionados.stream()
                .mapToDouble(p -> p.getCantidad() * p.getPrecioVenta())
                .sum();
    }

    @FXML
    private void generarVenta() {
        if (productosSeleccionados.isEmpty()) {
            mostrarAlerta("No hay productos seleccionados para la venta.");
            return;
        }

        if (resumenAbierto) return;
        resumenAbierto = true;

        if(comboTipoVenta.getValue().equalsIgnoreCase("Apartado")){
            if(null == montoApartadoField.getText() || montoApartadoField.getText().isEmpty()){
                mostrarAlerta("Para apartados ingresa el monto");
                return;
            }

            try {
                Double.valueOf(montoApartadoField.getText());
            }catch (Exception e){
                mostrarAlerta("Ingresa un monto valido de apartado");
                return;
            }

            if(null == clienteField.getText() || clienteField.getText().isEmpty()){
                mostrarAlerta("Para apartados ingresa el cliente");
                return;
            }
        }


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/venta/ResumenVentaPOSView.fxml"));
            Parent root = loader.load();

            ResumenVentaPOSController resumenController = loader.getController();
            resumenController.setDatos(
                    new ArrayList<>(productosSeleccionados),
                    comboTipoPago.getValue(),
                    comboTipoVenta.getValue(),
                    clienteField.getText()
            );

            Stage stage = new Stage();
            stage.setTitle("Resumen de Venta - POS");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            resumenAbierto = false;
            if (resumenController.isConfirmado()) {

                //Armamos Dto venta
                PagoDto pagoDto = new PagoDto();
                pagoDto.setPaymentType(comboTipoPago.getValue());

                if(comboTipoVenta.getValue().equalsIgnoreCase("Apartado")){
                    //Apartado
                    pagoDto.setAmountPaid(BigDecimal.valueOf(Double.valueOf(montoApartadoField.getText())));
                }else{
                    //Contado
                    pagoDto.setAmountPaid(BigDecimal.valueOf(Double.valueOf(totalImporteValue.getText())));
                }

                List<PagoDto> pagos = new ArrayList<>();
                pagos.add(pagoDto);

                List<DetalleVentaDto> details =  productosSeleccionados.stream().map(p->
                {
                    DetalleVentaDto detalleVentaDto = new DetalleVentaDto();
                    detalleVentaDto.setProductCode(p.getCodigo());
                    detalleVentaDto.setQuantity(p.getCantidad());
                    detalleVentaDto.setUnitPrice(BigDecimal.valueOf(p.getPrecioVenta()));
                    detalleVentaDto.setSubtotal(BigDecimal.valueOf( p.getPrecioVenta()*p.getCantidad()) );
                    return detalleVentaDto;
                }).toList();

                VentaDto venta = new VentaDto();
                venta.setCustomer(clienteField.getText());
                venta.setPayments(pagos);
                venta.setSaleDetails(details);
                venta.setSaleType(comboTipoVenta.getValue());
                venta.setPaymentType(comboTipoPago.getValue());
                venta.setOutletId(tienda.getId());
                venta.setUserId(usuario.getId());

                VentaDto response = null;

                try {
                    response = ventaService.generarVenta(venta);
                    List<String> codes = venta.getSaleDetails()
                            .stream()
                            .map(DetalleVentaDto::getProductCode).toList();
                    List<ProductoDto> productsCodes = productoService.getProducts(tienda);

                    TicketPrinter.generarTicket(response, productsCodes, tienda.getName());
                }catch (Exception e){
                    mostrarAlerta("Venta NO realizada / reintentar");
                    return;
                }

                mostrarAlerta("Venta confirmada con éxito!");
                // Limpiar después de venta
                productosSeleccionados.clear();
                montoApartadoField.clear();
                clienteField.clear();
                comboTipoPago.setValue("Efectivo");
                comboTipoVenta.setValue("Contado");
                filtroCodigo.clear();
                filtroDescripcion.clear();
                filtroCategoria.clear();
                filtroBarras.clear();
                actualizarTotales();
                cargarProductos();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta("Error al abrir el resumen de venta:\n" + ex.getMessage());
            resumenAbierto = false;
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void imprimirTicketConJavaFX(String contenido) {
        TextFlow textFlow = new TextFlow();
        for (String line : contenido.split("\n")) {
            textFlow.getChildren().add(new Text(line + "\n"));
        }

        Scene printScene = new Scene(textFlow);

        ObservableSet<Printer> all =  Printer.getAllPrinters();

        Printer impresora = Printer.getAllPrinters().stream().findFirst().get();

        // Buscar impresora específica (puedes cambiar el nombre si deseas una en particular)
       /* Printer impresora = Printer.getAllPrinters()
                .stream()
                // .filter(p -> p.getName().contains("local"))
                .filter(p -> p.getName().contains("Printer"))
                .findFirst()
                .orElse(null);*/

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

    private void imprimirTicketConJavaFX2(String contenido) {
        try {
            // 1️⃣ Convertir el contenido a bytes UTF-8
            byte[] ticketBytes = contenido.getBytes(StandardCharsets.UTF_8);

            // 2️⃣ Comando ESC/POS para corte total (opcional)
            byte[] corte = new byte[]{0x1D, 0x56, 0x41, 0x10};
            byte[] finalBytes = new byte[ticketBytes.length + corte.length];
            System.arraycopy(ticketBytes, 0, finalBytes, 0, ticketBytes.length);
            System.arraycopy(corte, 0, finalBytes, ticketBytes.length, corte.length);

            // 3️⃣ Buscar la primera impresora disponible RAW/CUPS
            PrintService impresora = PrintServiceLookup.lookupDefaultPrintService();
            if (impresora == null) {
                System.err.println("❌ No se encontró impresora compatible.");
                return;
            }

            // 4️⃣ Crear trabajo de impresión
            DocPrintJob job = impresora.createPrintJob();
            Doc doc = new SimpleDoc(finalBytes, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);

            // 5️⃣ Enviar a la impresora
            job.print(doc, null);
            System.out.println("✅ Ticket enviado a la impresora: " + impresora.getName());

        } catch (PrintException e) {
            e.printStackTrace();
            System.err.println("⚠️ Error al imprimir el ticket.");
        }
    }

    private void imprimirTicketESC_POS(String contenido) {
        try {
            // 1️⃣ Listar impresoras
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            if (services.length == 0) {
                System.err.println("❌ No se encontraron impresoras disponibles.");
                return;
            }

            // 2️⃣ Buscar impresora RAW/TERMAL
            PrintService impresora = null;
            for (PrintService ps : services) {
                String nombre = ps.getName().toLowerCase();
                if (nombre.contains("ticket") || nombre.contains("raw")) {
                    impresora = ps;
                    break;
                }
            }

            // Si no se encontró, usar la primera
            if (impresora == null) {
                impresora = services[0];
            }

            System.out.println("🖨️ Imprimiendo en: " + impresora.getName());

            // 3️⃣ Preparar el documento como bytes
            byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(bytes, flavor, null);

            // 4️⃣ Crear el trabajo de impresión
            DocPrintJob job = impresora.createPrintJob();
            PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();

            job.print(doc, attrs);
            System.out.println("✅ Impresión completada.");

        } catch (PrintException e) {
            System.err.println("⚠️ Error al imprimir: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        nuevaDescripcionField.clear();
        nuevaCategoriaField.clear();
        nuevoPrecioField.clear();
    }

    private void agregarProductoRapido() {
        ProductoDto producto = new ProductoDto();
        producto.setDescription(nuevaDescripcionField.getText());
        producto.setDescription(nuevaDescripcionField.getText());
        producto.setCategory2(nuevaCategoriaField.getText());
        producto.setSalePrice(Double.valueOf(nuevoPrecioField.getText()));
        producto.setCurrentStock(1);
        producto.setOutletId(tienda.getId());

        ProductoDto res = productoService.createFastProduct(producto);
        Producto tablaProduct = new Producto(res);

        // Agregar a la tabla y a la lista de productos
        tablaProductos.getItems().add(tablaProduct);
    }
}
