package org.palina.venta_ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.palina.venta_ui.dto.OutletDto;
import org.palina.venta_ui.dto.UserDto;
import org.palina.venta_ui.service.OutletService;
import org.palina.venta_ui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private ApplicationContext springContext;

    @Autowired
    private UserService loginService;

    @Autowired
    private OutletService outletService;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Button loginButton;

    @FXML
    public void initialize() {
        // ocultar el indicador y la etiqueta de error al iniciar
        loadingIndicator.setVisible(false);
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        loadingIndicator.setVisible(true);

        try {
            UserDto userDto = loginService.validate(email, password);
            if (userDto == null) {
                mostrarAlerta("Usuario inválido o contraseña incorrecta");
                return;
            }

            OutletDto outletDto = outletService.getOutlet(userDto);
            if (outletDto == null) {
                mostrarAlerta("No se encontró la tienda asociada al usuario");
                return;
            }

            errorLabel.setVisible(false);

            // 1) Cerrar ventana de login
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();

            // 2) Cargar la vista principal desde FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main/PrincipalView.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // 3) Pasar el usuario y la tienda al controlador principal
            PrincipalViewController controller = loader.getController();
            controller.setUsuario(userDto);
            controller.setTienda(outletDto);
            controller.initData();

            // 4) Mostrar la ventana principal
            Stage mainStage = new Stage();
            mainStage.setTitle("Venta @Palina");
            mainStage.setScene(new Scene(root));
            mainStage.setMaximized(true);
            mainStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta("Error al abrir la ventana principal:\n" + ex.getMessage());
        } finally {
            loadingIndicator.setVisible(false);
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
