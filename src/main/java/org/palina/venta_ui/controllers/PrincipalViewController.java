package org.palina.venta_ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class PrincipalViewController implements Initializable {

    @Autowired
    private ApplicationContext springContext;

    @FXML private MenuItem menuInventarioAgregar;
    @FXML private MenuItem menuVentaGenerar;
    @FXML private MenuItem menuApartados;
    @FXML private MenuItem menuAdminUsuarios;
    @FXML private MenuItem menuAdminRegistros;

    @FXML private Label usuarioLabel;
    @FXML private VBox contenidoCentral;

    private String usuario;

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Mostrar usuario si ya está seteado antes de initialize
        if (usuario != null) {
            usuarioLabel.setText("Usuario: " + usuario);
        }

        // Carga por defecto la sección de productos
        loadSection("/venta/VentaView.fxml");

        // Configurar navegación de menú
        menuInventarioAgregar.setOnAction(evt -> loadSection("/inventario/AgregarProductoView.fxml"));
        menuVentaGenerar.setOnAction(evt -> loadSection("/venta/VentaView.fxml"));
        menuApartados.setOnAction(evt -> loadSection("ApartadosView.fxml"));
        menuAdminUsuarios.setOnAction(evt -> loadSection("AdminUsuariosView.fxml"));
        menuAdminRegistros.setOnAction(evt -> loadSection("AdminRegistrosView.fxml"));
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
