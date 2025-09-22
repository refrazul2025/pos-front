package org.palina.venta_ui.views;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView extends Application {

    private boolean loginError = false;
    private boolean isLoading = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox container = new VBox(15);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("login-container");

        // Imagen
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/login.png")));
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Alerta de error
        Label errorLabel = new Label("Usuario / contraseña incorrectos");
        errorLabel.getStyleClass().add("alert");
        errorLabel.setVisible(false); // oculto por defecto

        // Campos de formulario
        TextField emailField = new TextField();
        emailField.setPromptText("email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("password");

        Button loginButton = new Button("Ingresar");
        loginButton.getStyleClass().add("btn-hellokitty");

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setVisible(false);

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            // Simular login
            isLoading = true;
            loadingIndicator.setVisible(true);

            // Simula validación simple
            if (email.equals("refrazul@gmail.com") && password.equals("87654321")) {
                errorLabel.setVisible(false);
                System.out.println("Login exitoso");
                // Cerrar login
                Stage loginStage = (Stage) loginButton.getScene().getWindow();
                loginStage.close();

                // Abrir vista principal
                PrincipalView principal = new PrincipalView();
                principal.mostrar(email); // pasa el usuario
            } else {
                errorLabel.setVisible(true);
            }

            loadingIndicator.setVisible(false);
            isLoading = false;
        });

        container.getChildren().addAll(
                imageView,
                errorLabel,
                new Label("Ingresar"),
                new Label("Correo"), emailField,
                new Label("Password"), passwordField,
                loginButton,
                loadingIndicator
        );

        Scene scene = new Scene(container, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());

        stage.setTitle("Login Hello Kitty");
        stage.setScene(scene);
        stage.show();
    }
}