package vote.Client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import vote.Urne.Sondage;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


public class AppVote extends Application {


    Button btn1 = new Button();
    StackPane root = new StackPane();

    StackPane StackVote = new StackPane();


    Button btn2 = new Button();
    Label lblVote = new Label();


    HBox hBox = new HBox();
    VBox vBox = new VBox();

    HBox buttonStack = new HBox();

    HBox CircleHBouton = new HBox();
    Button RedButton = new Button();
    Button GreenButton = new Button();
    Button YellowButton = new Button();

    Button RefreshButton = new Button();

    Circle redCircle = new Circle(9, Color.RED);
    Circle greenCircle = new Circle(9, Color.GREEN);
    Circle YellowCircle = new Circle(9, Color.YELLOW);

    String ColorHex = "#191919";
    String ColorStyle="#5F5AA2";

    Image img = new Image("file:src/main/resources/blahaj.png");
    ImageView logo = new ImageView(img);

    //chiffrage
    Random rdn = new Random();
    ArrayList<BigInteger> cle = Emalgam.keyGeneration();
    ArrayList<BigInteger> clePublique = Emalgam.getPublique(cle);
    ArrayList<BigInteger> choix1 = Emalgam.chiffrer(1,clePublique);
    ArrayList<BigInteger> choix2 = Emalgam.chiffrer(0,clePublique);

    private double xOffset = 0;
    private double yOffset = 0;


    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException, InterruptedException {
        //recuperation du sondage
        getSondage();

        initColorButton(primaryStage);

        initPaneAndBox();

        initButtons(primaryStage);


        root.setStyle("-fx-background-radius: 25px;-fx-background-color: "+ColorHex+";-fx-effect: dropshadow(three-pass-box, "+ColorHex+", 18, 0.5, 0, 0);-fx-background-insets: 12;");


        buttonStack.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!primaryStage.isMaximized()) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            }
        });

        buttonStack.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!primaryStage.isMaximized()) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            }
        });


        Scene MainScene = new Scene(root, 1100, 700, Color.TRANSPARENT);



        primaryStage.initStyle(StageStyle.TRANSPARENT);



        primaryStage.setScene(MainScene);
        primaryStage.getIcons().add(new Image("file:src/main/resources/blahajLogo.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void switchScene(String value) {
        root.getChildren().remove(StackVote);
        Label label = new Label("votre choix de vote est \"" + value + "\" !");
        label.setFont(new javafx.scene.text.Font(26));
        label.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        root.setStyle("-fx-background-radius: 25px;-fx-background-color: "+ColorHex+";-fx-effect: dropshadow(three-pass-box, "+ColorHex+", 18, 0.5, 0, 0);-fx-background-insets: 12;");
        root.getChildren().add(label);
        label.setAlignment(Pos.CENTER);



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

        if (sondage == null) {
            lblVote.setText("Aucun sondage en cours");
            btn1.setText("N");
            btn1.setDisable(true);
            btn2.setText("A");
            btn2.setDisable(true);
            RefreshButton.setDisable(false);

        } else {
            //set les valeurs du sondage
            lblVote.setText(sondage.getConsigne());
            btn1.setDisable(false);
            btn1.setText(sondage.getChoix1());
            btn2.setDisable(false);
            btn2.setText(sondage.getChoix2());
            RefreshButton.setDisable(true);


        }

        //fermeture du flux d'entrée
        in.close();
        //fermeture du flux de sortie
        out.close();



}






    public void initButtons(Stage primaryStage){
        btn1.setOnAction(e -> {
            switchScene(btn1.getText());
            try {
                sendVote(1);
                System.out.println("vote crypté : "+choix1);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        btn1.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn1.setEffect(new Glow());
            }
        });
        btn1.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn1.setEffect(null);
            }
        });


        btn2.setOnAction(e -> {
            switchScene(btn2.getText());
            try {
                sendVote(0);
                System.out.println("vote crypté : "+choix2);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        btn2.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn2.setEffect(new Glow());
            }
        });
        btn2.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn2.setEffect(null);
            }
        });

        RedButton.setOnAction(event -> {
            primaryStage.close();
        });

        YellowButton.setOnAction(event -> {
            primaryStage.setIconified(true);
        });

        GreenButton.setOnAction(event -> {
            primaryStage.setMaximized(!primaryStage.isMaximized());
            if(primaryStage.isMaximized()){
                root.setStyle("-fx-background-color: "+ColorHex+";");
                logo.setStyle("-fx-scale-x: 1.7; -fx-scale-y: 1.7;");
            }else{
                root.setStyle("-fx-background-radius: 25px;-fx-background-color: "+ColorHex+";-fx-effect: dropshadow(three-pass-box, "+ColorHex+", 18, 0.5, 0, 0);-fx-background-insets: 12;");
                logo.setStyle("-fx-scale-x: 1; -fx-scale-y: 1;");
            }

        });

        RefreshButton.setOnAction(event -> {
            try {
                getSondage();
                System.out.println("refresh");
            } catch (IOException | ClassNotFoundException  e) {
                e.printStackTrace();
            }
        });

        RefreshButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                RefreshButton.setEffect(new Glow());
            }
        });

        RefreshButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                RefreshButton.setEffect(null);
            }
        });





        RefreshButton.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        RefreshButton.setGraphic(new ImageView(new Image("file:src/main/resources/reflesh32px.png")));
        lblVote.setFont(new javafx.scene.text.Font(26));
        lblVote.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
    }

    public void initPaneAndBox(){
HBox funBox = new HBox();
        funBox.getChildren().addAll(lblVote,RefreshButton);
        //set insets
        funBox.setPadding(new Insets(0, 0, 0, 70));


        logo.setPreserveRatio(true);
        logo.setFitHeight(100);
        vBox.getChildren().add(logo);
        vBox.getChildren().add(funBox);
        vBox.getChildren().add(hBox);
        hBox.getChildren().addAll(btn1, btn2);

        vBox.setAlignment(Pos.CENTER);
        hBox.setAlignment(Pos.CENTER);
        funBox.setAlignment(Pos.CENTER);
        funBox.setSpacing(20);
        hBox.setSpacing(50);
        vBox.setSpacing(70);
        StackVote.getChildren().add(vBox);
        StackVote.setMaxHeight(300);


        StackVote.setAlignment(Pos.CENTER);

        root.getChildren().add(StackVote);




    }

    public void initColorButton(Stage stage){

        RedButton.setGraphic(redCircle);
        RedButton.setStyle("-fx-background-color: transparent;");
        GreenButton.setGraphic(greenCircle);
        GreenButton.setStyle("-fx-background-color: transparent;");
        YellowButton.setGraphic(YellowCircle);
        YellowButton.setStyle("-fx-background-color: transparent;");
        CircleHBouton.getChildren().addAll(RedButton,YellowButton, GreenButton);
        buttonStack.getChildren().add(CircleHBouton);

        buttonStack.prefWidthProperty().bind(stage.widthProperty());

        buttonStack.setMaxHeight(10);

        StackPane.setAlignment(buttonStack, Pos.TOP_RIGHT);
        //space the buttonStack off the top of the window
        StackPane.setMargin(buttonStack, new Insets(15, 0, 0, 0));


        root.getChildren().add(buttonStack);
        buttonStack.setStyle("-fx-background-color: transparent;");

        CircleHBouton.setStyle("-fx-spacing: 1px;");
        HBox.setMargin(CircleHBouton, new Insets(10, 0, 0, 30));
        //buttonStack.setAlignment(Pos.TOP_LEFT);

    }


}




