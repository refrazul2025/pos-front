package org.palina.venta_ui.controllers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.palina.venta_ui.dto.PagoDto;
import org.palina.venta_ui.dto.VentaDto;
import org.palina.venta_ui.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ConsultaVentaController {

    @FXML private TextField filtroIdVenta;
    @FXML private TextField filtroCliente;
    @FXML private DatePicker filtroFecha;
    @FXML private Button buscarButton;

    @FXML private TableView<VentaDto> tablaVentas;
    @FXML private TableColumn<VentaDto, Long> colVentaId;
    @FXML private TableColumn<VentaDto, String> colFechaVenta;
    @FXML private TableColumn<VentaDto, String> colCliente;
    @FXML private TableColumn<VentaDto, String> colTipoVenta;
    @FXML private TableColumn<VentaDto, BigDecimal> colTotalVenta;
    @FXML private TableColumn<VentaDto, Boolean> colEstado;

    @FXML private TableView<PagoDto> tablaPagos;
    @FXML private TableColumn<PagoDto, Long> colPagoId;
    @FXML private TableColumn<PagoDto, BigDecimal> colMontoPago;
    @FXML private TableColumn<PagoDto, String> colFechaPago;
    @FXML private TableColumn<PagoDto, String> colTipoPago;

    @FXML private TextField montoPagoField;
    @FXML private ComboBox<String> comboTipoPagoNuevo;
    @FXML private Button agregarPagoButton;

    @Autowired
    private VentaService ventaService; // 🔹 tu servicio REST
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarVentas(); // 👈 carga automática al iniciar
        configurarEventos();
        agregarPagoButton.setOnAction(e -> agregarPago());
    }

    private void configurarColumnas() {
        // 🧾 Configurar columnas de la tabla de ventas
        colVentaId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colFechaVenta.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSaleDate()));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomer()));
        colTipoVenta.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSaleType()));
        colTotalVenta.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getTotal()));
        colEstado.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().isSaleClosed()));

        // 💰 Configurar columnas de pagos
        colPagoId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colMontoPago.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getAmountPaid()));
        colFechaPago.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getPaymentDate() != null
                                ? c.getValue().getPaymentDate().format(formatter)
                                : ""
                ));
        colTipoPago.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaymentType()));
    }

    private void configurarEventos() {
        // 🎯 Cuando seleccionas una venta, muestra sus pagos
        tablaVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, nuevaVenta) -> {
            if (nuevaVenta != null && nuevaVenta.getPayments() != null) {
                tablaPagos.setItems(FXCollections.observableArrayList(nuevaVenta.getPayments()));
            } else {
                tablaPagos.getItems().clear();
            }
        });

        // 🔍 Filtro manual con botón Buscar
        buscarButton.setOnAction(e -> cargarVentas());
    }

    /**
     * 🔄 Carga todas las ventas desde el backend.
     * Se ejecuta automáticamente al iniciar la vista.
     */
    private void cargarVentas() {
        try {
            // 🔹 Llama al servicio (ajusta a tu API)
            List<VentaDto> ventas = ventaService.getApartados();

            // 🔹 Aplica filtros si hay valores ingresados
            String idFiltro = filtroIdVenta.getText();
            String clienteFiltro = filtroCliente.getText();
            String fechaFiltro = filtroFecha.getValue() != null ? filtroFecha.getValue().toString() : null;

            List<VentaDto> filtradas = ventas.stream()
                    .filter(v -> idFiltro == null || idFiltro.isEmpty() || v.getId().toString().contains(idFiltro))
                    .filter(v -> clienteFiltro == null || clienteFiltro.isEmpty() || v.getCustomer().toLowerCase().contains(clienteFiltro.toLowerCase()))
                    .filter(v -> fechaFiltro == null || fechaFiltro.isEmpty() || v.getSaleDate().toString().equals(fechaFiltro))
                    .collect(toList());

            tablaVentas.setItems(FXCollections.observableArrayList(filtradas));

            // Limpia pagos al refrescar lista
            tablaPagos.getItems().clear();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar las ventas", e.getMessage());
        }
    }

    private void agregarPago() {
        VentaDto ventaSeleccionada = tablaVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada == null) {
            mostrarAlerta("Selecciona una venta", "Debe seleccionar una venta antes de agregar un pago.");
            return;
        }

        try {
            String tipoPago = comboTipoPagoNuevo.getValue();
            String montoTexto = montoPagoField.getText();

            if (tipoPago == null || tipoPago.isEmpty() || montoTexto == null || montoTexto.isEmpty()) {
                mostrarAlerta("Datos incompletos", "Debe ingresar el monto y seleccionar un tipo de pago.");
                return;
            }

            BigDecimal monto = new BigDecimal(montoTexto);

            PagoDto nuevoPago = new PagoDto();
            nuevoPago.setId(ventaSeleccionada.getId());
            nuevoPago.setAmountPaid(monto);
            nuevoPago.setPaymentType(tipoPago);
            nuevoPago.setPaymentDate(java.time.LocalDate.now());

            // 🔹 Llamar al servicio para guardar en backend
            VentaDto ventaDto = ventaService.agregarPago(ventaSeleccionada.getId(), nuevoPago);

            // 🔹 Actualizar la tabla localmente
            ventaSeleccionada.getPayments().add(nuevoPago);
            tablaPagos.getItems().add(nuevoPago);
            montoPagoField.clear();
            comboTipoPagoNuevo.getSelectionModel().clearSelection();



        } catch (NumberFormatException ex) {
            mostrarAlerta("Monto inválido", "El monto debe ser un número válido.");
        } catch (Exception ex) {
            mostrarAlerta("Error al guardar pago", ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
