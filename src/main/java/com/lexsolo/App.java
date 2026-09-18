package com.lexsolo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale de l'application LexSolo
 * Gère le lancement et la navigation entre les vues
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("LoginView"), 800, 600);
        stage.setScene(scene);
        stage.setTitle("LexSolo Manager");
        stage.setResizable(false); // Optionnel : empêche le redimensionnement
        stage.show();
    }

    /**
     * Change la vue actuelle
     *
     * @param fxml Le nom du fichier FXML (sans extension)
     * @throws IOException Si le fichier FXML n'est pas trouvé
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Charge un fichier FXML
     *
     * @param fxml Le nom du fichier FXML (sans extension)
     * @return Le Parent chargé
     * @throws IOException Si le fichier FXML n'est pas trouvé
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}