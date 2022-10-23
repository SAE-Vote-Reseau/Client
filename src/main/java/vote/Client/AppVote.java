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
import vote.Urne.Sondage;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;


public class AppVote extends Application {

    Button btn1 = new Button();
    StackPane root = new StackPane();


    Button btn2 = new Button();
    Label lblVote = new Label();
    HBox hBox = new HBox();
    VBox vBox = new VBox();

    //chiffrage
    Random rdn = new Random();
    ArrayList<BigInteger> cle = Emalgam.keyGeneration();
    ArrayList<BigInteger> clePublique = Emalgam.getPublique(cle);
    ArrayList<BigInteger> choix1 = Emalgam.chiffrer(1,clePublique);
    ArrayList<BigInteger> choix2 = Emalgam.chiffrer(0,clePublique);



    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        //recuperation du sondage
        getSondage();
        //init des boutons ainsi que leur actions
        initButtons(primaryStage);

        //init de la StackPane , des Hbox et Vbox
        initPaneAndBox();
        primaryStage.setScene(new Scene(root, 1100, 700));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void switchScene(String value, Stage stage) {
        Label label = new Label("votre choix de vote est \"" + value + "\" !");
        label.setFont(new javafx.scene.text.Font(26));
        label.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        StackPane root = new StackPane();
        root.getChildren().add(label);
        label.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 1100, 700);
        stage.setScene(scene);
        stage.show();
    }


    public void sendVote(int choice) throws IOException {
        //envoie un vote au serveur
        try {
            //création d'un socket client
            Socket socket = new Socket("127.0.0.1", 5565);
            //création d'un flux de sortie
            java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
            //écriture dans le flux de sortie
            out.writeUTF(String.valueOf(choice));
            out.flush();
            //Fermeture du flux de sortie
            out.close();
            //Fermeture du socket
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSondage() throws IOException, ClassNotFoundException {
        Socket socket = new Socket("127.0.01", 5565);
        java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
        out.writeUTF("getSondage");
        out.flush();
        //recupère l'inputstream du socket
        java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());
        //lit le message envoyé par le serveur
        Sondage sondage = (Sondage) in.readObject();
        //set les valeurs du sondage
        lblVote.setText(sondage.getConsigne());
        btn1.setText(sondage.getChoix1());
        btn2.setText(sondage.getChoix2());

        //fermeture du flux d'entrée
        in.close();
        //fermeture du flux de sortie
        out.close();
        socket.close();

    }


    public void initButtons(Stage primaryStage){
        btn1.setOnAction(e -> {
            switchScene(btn1.getText(), primaryStage);
            try {
                sendVote(1);
                System.out.println("vote crypté : "+choix1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });


        btn2.setOnAction(e -> {
            switchScene(btn2.getText(), primaryStage);
            try {
                sendVote(0);
                System.out.println("vote crypté : "+choix2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        lblVote.setFont(new javafx.scene.text.Font(26));
        lblVote.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");

    }

    public void initPaneAndBox(){
        vBox.getChildren().add(lblVote);
        vBox.getChildren().add(hBox);

        hBox.getChildren().addAll(btn1, btn2);


        root.getChildren().add(vBox);
        vBox.setAlignment(Pos.CENTER);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(50);
        vBox.setSpacing(70);

    }


}




