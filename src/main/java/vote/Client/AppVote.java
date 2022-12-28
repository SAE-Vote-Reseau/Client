package vote.Client;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteAddUser;
import vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteDeleteUser;
import vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteEtat.RequeteArreterSondage;
import vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteEtat.RequeteCreerSondage;
import vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteEtat.RequeteFermerRecolte;
import vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteEtat.RequetePublierResultat;
import vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteGetAllUsers;
import vote.Urne.Requete.RequeteClient.RequeteAdmin.RequeteUpdateUser;
import vote.Urne.Requete.RequeteClient.RequeteUtilisateur.ConnexionReponse;
import vote.Urne.Requete.RequeteClient.RequeteUtilisateur.RequeteConnexion;
import vote.Urne.Requete.RequeteClient.RequeteUtilisateur.RequeteGetSondage;
import vote.Urne.Requete.RequeteClient.RequeteUtilisateur.RequeteVote;
import vote.Urne.metier.Employe;
import vote.Urne.metier.Sondage;
import vote.crypto.ElGamal;
import vote.crypto.Message;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.*;

import static com.sun.glass.events.KeyEvent.*;


public class AppVote extends Application {


    StackPane stackPanePanel = new StackPane();

    public StackPane root = new StackPane();

    public volatile StackPane StackVote = new StackPane();

    public static StackPane StackConnexion = new StackPane();

    SplitPane splitPane = new SplitPane();
    Button btn1 = new Button();
    Button btn2 = new Button();

    Button btn3 = new Button();
    Label lblVote = new Label();

    boolean PanelOpen = false;


    public volatile Group groupPie;

    public volatile PieChart chart;

    HBox hBox = new HBox();
    VBox vBox = new VBox();

    VBox vBoxLOGIN = new VBox();

    HBox buttonStack = new HBox();

    HBox CircleHBouton = new HBox();
    Button RedButton = new Button();
    Button GreenButton = new Button();
    Button YellowButton = new Button();


    Circle redCircle = new Circle(9, Color.RED);
    Circle greenCircle = new Circle(9, Color.GREEN);
    Circle YellowCircle = new Circle(9, Color.YELLOW);

    String ColorHex = "#191919";
    String ColorStyle="#5F5AA2";

    Image img = new Image("file:src/main/resources/blahaj.png");

    ImageView gifBlahaj = new ImageView(new Image("file:src/main/resources/blahspinny.gif"));
    ImageView logo = new ImageView(img);
    public volatile Label label;

    Sondage sondage;

    RequeteConnexion requeteConnexion;

    ConnexionReponse connexionReponse;

    ListView<Employe> listViewEmploye = new ListView<>();



    private double xOffset = 0;
    private double yOffset = 0;

    private double xOffsetPanel;

    private double yOffsetPanel;

    private String ip = "127.0.0.1"; // par defaut
    private int port = 5565;


    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException, InterruptedException {
        setKonami();

        System.setProperty("javax.net.ssl.trustStore", "./client.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "auuugh");

        ConnexionScene(primaryStage);

        initColorButton(primaryStage);

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

    public void mainScene(boolean isAdmin, Stage primaryStage) throws IOException, ClassNotFoundException {
        root.getChildren().remove(vBoxLOGIN);
        getSondage();
        initButtons();
        initPaneAndBox(isAdmin, primaryStage);

    }

    public void ConnexionScene(Stage primaryStage){
        TextField Username = new TextField();
        PasswordField Password = new PasswordField();
        Button Connexion = new Button("Connexion");


        HBox hBox = new HBox();

        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(Username,Password);
        vBox2.setMaxWidth(160);

        vBox2.setSpacing(10);

        hBox.getChildren().add(Connexion);
        vBoxLOGIN.getChildren().addAll(logo,vBox2,hBox);

        logo.setFitHeight(110);
        logo.setPreserveRatio(true);

        vBoxLOGIN.setSpacing(10);

        StackConnexion.getChildren().add(vBoxLOGIN);

        root.getChildren().add(vBoxLOGIN);

        vBoxLOGIN.setAlignment(Pos.CENTER);

        hBox.setAlignment(Pos.CENTER);

        Username.setPromptText("Username");
        Password.setPromptText("Password");

        Username.setStyle("-fx-background-color: #191919;-fx-text-fill: #5F5AA2;-fx-border-color: #5F5AA2;-fx-border-width: 2px;");
        Password.setStyle("-fx-background-color: #191919;-fx-text-fill: #5F5AA2;-fx-border-color: #5F5AA2;-fx-border-width: 2px;");

        Connexion.setStyle("-fx-background-color: #5F5AA2; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 25px; -fx-padding: 10px 20px 10px 20px;");

        Connexion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                requeteConnexion = new RequeteConnexion(Username.getText(),Password.getText());
                try {
                    Label labelTime = new Label();
                    SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(requeteConnexion);
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    objectOutputStream.flush();
                    Object objet = objectInputStream.readObject();
                    if (objet != null) {
                        connexionReponse = (ConnexionReponse) objet;
                        root.getChildren().remove(vBoxLOGIN);
                        if(LocalTime.now().isAfter(LocalTime.of(0,0,0)) && LocalTime.now().isBefore(LocalTime.of(6,0,0))) {
                             labelTime = new Label("Bonsoir " + connexionReponse.getEmploye().getPrenom() + " !");
                        }else {
                             labelTime = new Label("Bonjour " + connexionReponse.getEmploye().getPrenom() + " !");
                        }
                        labelTime.setStyle("-fx-text-fill: #5F5AA2; -fx-font-size: 42px; -fx-font-weight: bold;");
                        Label finalLabelTime = labelTime;
                        root.getChildren().add(finalLabelTime);
                        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), finalLabelTime);

                        fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                                pause.setOnFinished(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        FadeTransition FadeOut = new FadeTransition(Duration.millis(1000), finalLabelTime);
                                        FadeOut.setFromValue(1);
                                        FadeOut.setToValue(0);
                                        FadeOut.play();
                                        FadeOut.setOnFinished(new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent event) {
                                                root.getChildren().remove(finalLabelTime);
                                                try {
                                                    mainScene(connexionReponse.getEmploye().isEstAdmin(), primaryStage);
                                                } catch (IOException | ClassNotFoundException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        });
                                    }
                                });
                                pause.play();

                            }
                        });
                        fadeTransition.setFromValue(0);
                        fadeTransition.setToValue(1);
                        fadeTransition.play();
                    }
                    objectOutputStream.close();
                    //socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });




    }





    public synchronized void ResultScene(){
        if(root.getChildren().contains(StackVote)){
            root.getChildren().remove(StackVote);
        }
        if(root.getChildren().contains(label)){
            root.getChildren().remove(label);
        }
        groupPie = new Group();


        float choice1 = 100-((float)sondage.getResultat()/(float)sondage.getNbVotant()*100);
        choice1 = (float) (Math.floor(choice1 * 100) / 100);
        float choice2 = (float)sondage.getResultat()/(float)sondage.getNbVotant()*100 ;
        choice2 = (float) (Math.floor(choice2 * 100) / 100);

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data(sondage.getChoix1()+" = "+ choice1+ "%", sondage.getNbVotant()-sondage.getResultat()),
                        new PieChart.Data(sondage.getChoix2()+" = "+ choice2+ "%", sondage.getResultat())
                );


            chart = new PieChart(pieChartData);



            root.getChildren().add(chart);
            StackPane.setAlignment(chart,Pos.CENTER);
            chart.setLegendSide(Side.LEFT);
           chart.setStyle("-fx-font-size: 26;-fx-scale-x: 0.66;-fx-scale-y:0.66;");
            chart.setLabelLineLength(0);


    }
    public void switchScene(String value) {
        root.getChildren().remove(StackVote);
        label = new Label("votre choix de vote est \"" + value + "\" !");
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
            Message voteChiffre = ElGamal.encrypt(BigInteger.valueOf(choice),sondage.getPublicKeyInfo());

            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

            ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
           RequeteVote req = new RequeteVote(voteChiffre,connexionReponse.getSsid());
            out.writeObject(req);
            out.flush();
            System.out.println("vote envoyé");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void getSondage() throws IOException, ClassNotFoundException {
        TimerTask getSondageTask = new TimerTask(){

            @Override
            public void run() {
                System.out.println("get sondage");
                try{
                    SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);


                    RequeteGetSondage req = new RequeteGetSondage(connexionReponse.getSsid());


                    java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(req);
                    out.flush();
                    //recupère l'inputstream du socket
                    java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());

                    //lit le message envoyé par le serveur
                    sondage = (Sondage) in.readObject();

                    if (sondage == null) {
                        Platform.runLater(()->{lblVote.setText("Aucun sondage en cours");
                            btn1.setText("N");
                            btn1.setDisable(true);
                            btn2.setText("A");
                            btn2.setDisable(true);});



                    }
                    else if (sondage.getResultat() != null){
                        System.out.println("Resultat du sondage: \n" + sondage.getChoix1() + ": " +(sondage.getNbVotant()-sondage.getResultat())+ "\n" + sondage.getChoix2() + ": " + sondage.getResultat() );
                        Platform.runLater(()->{
                            ResultScene();
                        });
                        cancel();
                    }
                    else {
                        //set les valeurs du sondage
                       Platform.runLater(()->{
                           lblVote.setText(sondage.getConsigne());
                           btn1.setDisable(false);
                           btn1.setText(sondage.getChoix1());
                           btn2.setDisable(false);
                           btn2.setText(sondage.getChoix2());
                       });

                    }

                    //fermeture du flux d'entrée
                    in.close();
                    //fermeture du flux de sortie
                    out.close();



                }catch (IOException | ClassNotFoundException e){

                    lblVote.setText("La connexion au serveur n'est pas disponible");
                    btn1.setText("N");
                    btn1.setDisable(true);
                    btn2.setText("A");
                    btn2.setDisable(true);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(()->{
                    if (stackPanePanel.isVisible()){
                        stackPanePanel.toFront();
                    }
                });

            }


        };
        Timer timerSondage = new Timer("SondageTimer");
        timerSondage.scheduleAtFixedRate(getSondageTask, 0, 5000);

    }



    public void initButtons(){
        btn1.setOnAction(e -> {
            switchScene(btn1.getText());
            try {
                sendVote(0);
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
                sendVote(1);
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

        btn3.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn3.setEffect(new Glow());
            }
        });
        btn3.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn3.setEffect(null);
            }
        });






        lblVote.setFont(new javafx.scene.text.Font(26));
        lblVote.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn3.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
    }

    public void  AdminPanelScene(Stage primaryStage){
        if(!PanelOpen) {

            splitPane = new SplitPane();
            splitPane.setOrientation(Orientation.VERTICAL);
            splitPane.setDividerPositions(0.75f);
            splitPane.setMaxWidth(150);
            splitPane.setMaxHeight(300);


            Text text = new Text("Admin Panel");
            text.setFont(new javafx.scene.text.Font(26));
            text.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: " + ColorStyle + ";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");


            stackPanePanel.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffsetPanel = event.getX();
                    yOffsetPanel = event.getY();
                }
            });


            stackPanePanel.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stackPanePanel.setTranslateX(event.getX() + stackPanePanel.getTranslateX() - xOffsetPanel);
                    stackPanePanel.setTranslateY(event.getY() + stackPanePanel.getTranslateY() - yOffsetPanel);

                }
            });

            VBox vbox = new VBox();
            ListView<String> listView = new ListView<String>();
            rafraichirUtilisateurs(listView);

            HBox hbox = new HBox();
            Text placeholder=new Text("Aucun sondage en cours");
            HBox hboxBottm = new HBox();
            hboxBottm.setAlignment(Pos.CENTER);
            VBox vboxBottm = new VBox();
            Button btn1Panel = new Button("Ajouter");
            btn1Panel.setOnAction(e -> {
                createUserscene(listView);

            });
            Button btn2Panel = new Button("Supprimer");
            btn2Panel.setOnAction(e -> {
                supprimerUtilisateur(listView);
                rafraichirUtilisateurs(listView);
            });
            Button btn3Panel = new Button("Modifier");
            btn3Panel.setOnAction(e -> {
                modifierUtilisateur(listView);
                rafraichirUtilisateurs(listView);
            });
            Button btnCreateSondage = new Button("Créer sondage");
            Button btn4Panel = new Button("Quitter");
            btn4Panel.setOnAction(e -> {
                root.getChildren().remove(stackPanePanel);
                stackPanePanel.getChildren().clear();
                PanelOpen = false;
            });
            Button btnGetResult = new Button("Resultats");
            btnGetResult.setOnAction(e -> {
                try{
                    SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                    RequeteFermerRecolte req = new RequeteFermerRecolte(connexionReponse.getSsid());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();
                    Socket socket2 = new Socket("127.0.0.1", 5565);
                    ObjectOutputStream oos2 = new ObjectOutputStream(socket2.getOutputStream());
                    RequetePublierResultat req2 = new RequetePublierResultat(connexionReponse.getSsid());
                    oos2.writeObject(req2);
                    oos2.flush();
                    stackPanePanel.toFront();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });



            Button btnFermerSondage = new Button("Fermer Sondage");
            btnFermerSondage.setOnAction(e -> {
                try{
                    SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                    RequeteArreterSondage req = new RequeteArreterSondage(connexionReponse.getSsid());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    System.out.println("arrêt du sondage");
                    oos.writeObject(req);
                    oos.flush();

                    root.getChildren().remove(chart);
                    root.getChildren().add(StackVote);
                    getSondage();
            } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });



            if(sondage!=null) {
                placeholder = new Text(sondage.getConsigne());
                hboxBottm.getChildren().addAll(btnGetResult,btnFermerSondage);

                //center the button
                hboxBottm.setAlignment(Pos.CENTER);

            }else {
                placeholder = new Text("Aucun sondage");
                btnCreateSondage = new Button("Creer sondage");
                hboxBottm.getChildren().add(btnCreateSondage);
                btnCreateSondage.setOnAction(e -> {
                    creerSondage();
                });
                //center the button
                btnCreateSondage.setAlignment(Pos.CENTER);
            }
            vboxBottm.getChildren().addAll(placeholder, hboxBottm);


            placeholder.setFont(new javafx.scene.text.Font(26));
            placeholder.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: " + ColorStyle + ";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");



            hbox.getChildren().addAll(btn1Panel, btn2Panel, btn3Panel);
            vbox.getChildren().addAll(listView, hbox);
            splitPane.getItems().addAll(vbox, vboxBottm);
            stackPanePanel.getChildren().addAll(text, splitPane, btn4Panel);
            stackPanePanel.setPrefHeight(400);
            stackPanePanel.setPrefWidth(170);
            stackPanePanel.setMaxHeight(400);
            stackPanePanel.setMaxWidth(210);
            StackPane.setAlignment(text, Pos.TOP_CENTER);
            StackPane.setAlignment(btn4Panel, Pos.BOTTOM_CENTER);
            stackPanePanel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            stackPanePanel.setStyle("-fx-background-color: " + ColorStyle + ";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");


            root.getChildren().add(stackPanePanel);
            PanelOpen = true;
        }


    }

public void creerSondage(){
    StackPane paneUser = new StackPane();
    paneUser.setStyle("-fx-background-color: #891bd7;-fx-border-color: #000000;-fx-border-width: 2px;-fx-border-radius: 10px;-fx-background-radius: 10px;");
    paneUser.setMaxHeight(300);
    paneUser.setMaxWidth(300);

    Text text = new Text("Créer un sondage");
    text.setFont(new javafx.scene.text.Font(26));
    text.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: #891bd7;  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");

    TextField textField = new TextField();
    textField.setPromptText("Consigne");
    textField.setMaxWidth(200);
    textField.setMaxHeight(50);
    textField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

    TextField textField2 = new TextField();
    textField2.setPromptText("Choix 1");
    textField2.setMaxWidth(200);
    textField2.setMaxHeight(50);
    textField2.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

    TextField textField3 = new TextField();
    textField3.setPromptText("Choix 2");
    textField3.setMaxWidth(200);
    textField3.setMaxHeight(50);
    textField3.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

    TextField textField4 = new TextField();
    textField4.setPromptText("nombre Bits");
    textField4.setMaxWidth(200);
    textField4.setMaxHeight(50);
    textField4.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

    Button btn = new Button("Creer");
    btn.setOnAction(e->{
        String consigne = textField.getText();
        String choix1 = textField2.getText();
        String choix2 = textField3.getText();
        int nbBits = Integer.parseInt(textField4.getText());
        System.out.println(nbBits);
        if(consigne.equals("") || choix1.equals("") || choix2.equals("") || nbBits == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
        }else{
            try{
                SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                RequeteCreerSondage req = new RequeteCreerSondage(consigne, choix1, choix2,nbBits,connexionReponse.getSsid());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(req);
                oos.flush();
                root.getChildren().remove(paneUser);
                root.getChildren().remove(stackPanePanel.getChildren().remove(1));
                root.getChildren().remove(stackPanePanel);
                PanelOpen = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }


        }
    });
    btn.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

    Button btn2 = new Button("Annuler");
    btn2.setOnAction(e -> {
        root.getChildren().remove(paneUser);
    });
    btn2.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

    VBox vbox = new VBox();
    HBox hbox = new HBox();
    vbox.setSpacing(10);
    hbox.setSpacing(10);
    hbox.getChildren().addAll(btn, btn2);
    vbox.getChildren().addAll(textField, textField2, textField3,textField4, hbox);
    vbox.setAlignment(Pos.CENTER);
    hbox.setAlignment(Pos.CENTER);
    paneUser.getChildren().add(vbox);
    StackPane.setAlignment(vbox, Pos.CENTER);
    root.getChildren().add(paneUser);
}

    public void initPaneAndBox(boolean isAdmin,Stage primaryStage){
HBox funBox = new HBox();
        funBox.getChildren().add(lblVote);
        logo.setPreserveRatio(true);
        logo.setFitHeight(100);
        vBox.getChildren().add(logo);
        vBox.getChildren().add(funBox);
        vBox.getChildren().add(hBox);
        hBox.getChildren().addAll(btn1, btn2);
        if(isAdmin) {
            root.getChildren().add(btn3);
            btn3.setText("Admin Panel");
            btn3.setOnAction(e -> {
                AdminPanelScene(primaryStage);
            });
        }
        StackPane.setAlignment(btn3, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(btn3, new Insets(0, 40, 40, 0));


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
        RedButton.setOnAction(event -> {
            stage.close();
        });

        YellowButton.setOnAction(event -> {
            stage.setIconified(true);
        });

        GreenButton.setOnAction(event -> {
            stage.setMaximized(!stage.isMaximized());
            if(stage.isMaximized()){
                root.setStyle("-fx-background-color: "+ColorHex+";");
                logo.setStyle("-fx-scale-x: 1.7; -fx-scale-y: 1.7;");
            }else{
                root.setStyle("-fx-background-radius: 25px;-fx-background-color: "+ColorHex+";-fx-effect: dropshadow(three-pass-box, "+ColorHex+", 18, 0.5, 0, 0);-fx-background-insets: 12;");
                logo.setStyle("-fx-scale-x: 1; -fx-scale-y: 1;");
            }

        });
        //buttonStack.setAlignment(Pos.TOP_LEFT);

    }

    private static class ConnectException extends Exception {
        public ConnectException(String exception) {
            super(exception);
        }
    }


    public void rafraichirUtilisateurs(ListView<String> view){
        try {
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

            RequeteGetAllUsers req = new RequeteGetAllUsers(connexionReponse.getSsid());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(req);
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            List<Employe> employes = (List<Employe>) ois.readObject();
            listViewEmploye.setItems(FXCollections.observableArrayList(employes));
            view.setItems(null);
            List<String> employesString = new ArrayList<>();
            for (Employe employe : employes) {
                employesString.add(employe.getNom() + " " + employe.getPrenom() +" "+ isAdmin(employe) );
            }
            view.setItems(FXCollections.observableArrayList(employesString));


            //socket.close();
            oos.close();
            ois.close();
            } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public String isAdmin(Employe employe){
        if(employe.isEstAdmin()){
            return "[Admin]";
        }else{
            return "";
        }
    }

    public void supprimerUtilisateur(ListView<String> view) {
        try {
            listViewEmploye.getSelectionModel().select(view.getSelectionModel().getSelectedIndex());
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

            RequeteDeleteUser req = new RequeteDeleteUser(listViewEmploye.getSelectionModel().getSelectedItem().getEmail(),connexionReponse.getSsid());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(req);
            oos.flush();
            oos.close();
           // socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void modifierUtilisateur(ListView<String> view) {
        StackPane paneUser = new StackPane();
        paneUser.setStyle("-fx-background-color: #891bd7;-fx-border-color: #000000;-fx-border-width: 2px;-fx-border-radius: 10px;-fx-background-radius: 10px;");
        paneUser.setMaxHeight(300);
        paneUser.setMaxWidth(300);
        VBox vbox = new VBox();
        listViewEmploye.getSelectionModel().select(view.getSelectionModel().getSelectedIndex());
        String email = listViewEmploye.getSelectionModel().getSelectedItem().getEmail();
        HBox hbox = new HBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        paneUser.getChildren().add(vbox);
        Label title = new Label("Modifier un utilisateur");
        title.setStyle("-fx-font-size: 25px;");
        vbox.getChildren().add(title);
        TextField nom = new TextField();
        nom.setPromptText("Nom");
        nom.setText(listViewEmploye.getSelectionModel().getSelectedItem().getNom());
        TextField prenom = new TextField();
        prenom.setText(listViewEmploye.getSelectionModel().getSelectedItem().getPrenom());
        prenom.setPromptText("Prenom");
        TextField password = new TextField();
        password.setPromptText("Mot de passe");
        TextField passwordConfirm = new TextField();
        passwordConfirm.setPromptText("Confirmer le mot de passe");
        CheckBox admin = new CheckBox("Administrateur");
        admin.setSelected(listViewEmploye.getSelectionModel().getSelectedItem().isEstAdmin());
        Button quite = new Button("Annuler");
        quite.setOnAction(event -> {
            root.getChildren().remove(paneUser);
        });
        Button save = new Button("Enregistrer");
        save.setOnAction(e -> {
            if(password.getText().equals(passwordConfirm.getText())){
                try {

                    SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                    RequeteUpdateUser req = new RequeteUpdateUser(email,prenom.getText(),nom.getText(),password.getText(),admin.isSelected(),connexionReponse.getSsid());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();
                    oos.close();
                    //socket.close();
                    root.getChildren().remove(paneUser);
                    rafraichirUtilisateurs(view);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Les mots de passe ne correspondent pas");
                alert.showAndWait();
            }
        });
        hbox.getChildren().addAll(quite,save);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(nom,prenom,password,passwordConfirm,admin,hbox);
        root.getChildren().add(paneUser);
    }

    public void createUserscene(ListView<String> view) {
        StackPane paneUser = new StackPane();
        paneUser.setStyle("-fx-background-color: #891bd7;-fx-border-color: #000000;-fx-border-width: 2px;-fx-border-radius: 10px;-fx-background-radius: 10px;");
        paneUser.setMaxHeight(300);
        paneUser.setMaxWidth(300);
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        paneUser.getChildren().add(vbox);
        Label title = new Label("Ajouter un utilisateur");
        title.setStyle("-fx-font-size: 25px;");
        vbox.getChildren().add(title);
        TextField nom = new TextField();
        nom.setPromptText("Nom");
        TextField prenom = new TextField();
        prenom.setPromptText("Prenom");
        TextField email = new TextField();
        email.setPromptText("Email");
        TextField password = new TextField();
        password.setPromptText("Mot de passe");
        TextField passwordConfirm = new TextField();
        passwordConfirm.setPromptText("Confirmer le mot de passe");
        CheckBox admin = new CheckBox("Administrateur");
        Button quite = new Button("Annuler");
        quite.setOnAction(event -> {
            root.getChildren().remove(paneUser);
        });
        Button save = new Button("Enregistrer");
        save.setOnAction(e -> {
            if(password.getText().equals(passwordConfirm.getText())){
                try {
                    SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                    RequeteAddUser req = new RequeteAddUser(email.getText(),prenom.getText(),nom.getText(),password.getText(),admin.isSelected(),connexionReponse.getSsid());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();
                    oos.close();
                    //socket.close();
                    root.getChildren().remove(paneUser);
                    rafraichirUtilisateurs(view);
    } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Les mots de passe ne correspondent pas");
                alert.showAndWait();
            }
        });
        hbox.getChildren().addAll(quite,save);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(nom,prenom,email,password,passwordConfirm,admin,hbox);
        root.getChildren().add(paneUser);
    }


                public void setKonami() {
        Thread threadNami = new Thread(() -> {
            Map<Integer, Integer>[] graph = Konami.generateSequenceMap(new int[]{VK_UP, VK_UP, VK_DOWN, VK_DOWN, VK_LEFT, VK_RIGHT, VK_LEFT, VK_RIGHT, VK_B, VK_A});



            root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    int code = keyEvent.getCode().getCode();
                    if (Konami.CheckK(code, graph)) {
                        root.getChildren().add(gifBlahaj);
                        gifBlahaj.setFitHeight(400);
                        gifBlahaj.setFitWidth(400);
                        gifBlahaj.toFront();
                        gifBlahaj.setEffect(new DropShadow(50, Color.BLACK));
                        PauseTransition pause = new PauseTransition(Duration.seconds(5));
                        pause.setOnFinished(event1 -> root.getChildren().remove(gifBlahaj));
                        pause.play();
                    }
                }
            });
        });
        threadNami.start();
    }


}





