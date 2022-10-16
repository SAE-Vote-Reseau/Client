package vote.Client;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;



public class AppVote extends Application {

    Button btn1 = new Button();


    Button btn2 = new Button();
    Label lblVote = new Label();
    HBox hBox = new HBox();
    VBox vBox = new VBox();



    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        getSondage();
        btn1.setOnAction(e -> {
            switchscene(btn1.getText(), primaryStage);
            try {
                sendVote(1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });


        btn2.setOnAction(e -> {
            switchscene(btn2.getText(), primaryStage);
            try {
                sendVote(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        lblVote.setFont(new javafx.scene.text.Font(26));
        lblVote.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");

        StackPane root = new StackPane();

        vBox.getChildren().add(lblVote);
        vBox.getChildren().add(hBox);

        hBox.getChildren().addAll(btn1, btn2);


        root.getChildren().add(vBox);
        vBox.setAlignment(Pos.CENTER);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(50);
        vBox.setSpacing(70);


        primaryStage.setScene(new javafx.scene.Scene(root, 1100, 700));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void switchscene(String value, Stage stage) {
        Stage stage1 = stage;
        Label label = new Label("votre choix de vote est \"" + value + "\" !");
        label.setFont(new javafx.scene.text.Font(26));
        label.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        ;
        StackPane root = new StackPane();
        root.getChildren().add(label);
        label.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 1100, 700);
        stage1.setScene(scene);
        stage1.show();
    }


    public void sendVote(int choice) throws IOException {
        //envoie un vote au serveur
        try {
            //création d'un socket client
            java.net.Socket socket = new java.net.Socket("127.0.0.1", 5565);
            //création d'un flux de sortie
            java.io.DataOutputStream out = new java.io.DataOutputStream(socket.getOutputStream());
            //écriture dans le flux de sortie
            out.writeUTF(String.valueOf(choice));
            //fermeture du flux de sortie
            out.close();
            //fermeture du socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSondage() throws IOException, ClassNotFoundException {
        java.net.Socket socket = new java.net.Socket("127.0.01", 5565);
        java.io.DataOutputStream out = new java.io.DataOutputStream(socket.getOutputStream());

        out.writeUTF("getSondage");
        //recupère l'inputstream du socket
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());
        //lit le message envoyé par le serveur
        String[] sondage = (String[]) in.readObject();
        //set les valeurs du sondage
        lblVote.setText(sondage[0]);
        btn1.setText(sondage[2]);
        btn2.setText(sondage[1]);

        //fermeture du flux d'entrée
        in.close();
        //fermeture du flux de sortie
        out.close();
        socket.close();

    }




}




