package org.palina.venta_ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import org.palina.venta_ui.dto.OutletDto;
import org.palina.venta_ui.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class PrincipalViewController implements Initializable, PrincipalSection {

    @Autowired
    private ApplicationContext springContext;

    @FXML private MenuItem menuInventarioAgregar;
    @FXML private MenuItem menuVentaGenerar;
    @FXML private MenuItem menuApartados;
    @FXML private MenuItem menuAdminUsuarios;
    @FXML private MenuItem menuAdminRegistros;
    @FXML private MenuItem menuAdminCaja;

    @FXML private Label usuarioLabel;
    @FXML private VBox contenidoCentral;

    private UserDto usuario;
    private OutletDto tienda;

    public void setUsuario(UserDto usuario) {
        this.usuario = usuario;
        if (usuarioLabel != null && usuario != null) {
            usuarioLabel.setText("Usuario: " + usuario.getUsername());
        }
    }

    public void setTienda(OutletDto outlet){
        this.tienda = outlet;
    }

    @Override
    public void initData() {
        if (usuario != null) {
            usuarioLabel.setText("Usuario: " + usuario.getUsername());
        }
        // ahora sí cargamos la vista inicial
        loadSection("/venta/VentaView.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar navegación de menú
        menuInventarioAgregar.setOnAction(evt -> loadSection("/inventario/AgregarProductoView.fxml"));
        menuVentaGenerar.setOnAction(evt -> loadSection("/venta/VentaView.fxml"));
        menuApartados.setOnAction(evt -> loadSection("/venta/ConsultaVentaView.fxml"));
        menuAdminUsuarios.setOnAction(evt -> loadSection("AdminUsuariosView.fxml"));
        menuAdminRegistros.setOnAction(evt -> loadSection("AdminRegistrosView.fxml"));
        menuAdminCaja.setOnAction(evt->loadSection("/admin/CajaView.fxml"));
    }

    /**
     * Carga dinámicamente un FXML dentro de contenidoCentral.
     */
    private void loadSection(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views" + fxmlName)
            );
            loader.setControllerFactory(springContext::getBean);

            Parent section = loader.load();
            contenidoCentral.getChildren().setAll(section);

            // Obtener el controlador de la sección cargada
            Object controller = loader.getController();

            // Si la sección implementa una interfaz para recibir usuario/tienda, pasarlos
            if (controller instanceof PrincipalSection sectionController) {
                sectionController.setUsuario(usuario);
                sectionController.setTienda(tienda);
                sectionController.initData();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("No se pudo cargar la sección: " + fxmlName);
        }
    }


    private void mostrarAlerta(String mensaje) {
        // Simple alert dialog
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
        );
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
