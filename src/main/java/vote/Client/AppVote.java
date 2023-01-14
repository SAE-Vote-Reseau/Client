package vote.Client;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.text.Font;
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
import vote.Urne.Requete.RequeteClient.RequeteUtilisateur.*;
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

    StackPane stackPanePanelHisto = new StackPane();

    StackPane StackPolitique = new StackPane();
    StackPane stackPanePanelChangeMdp = new StackPane();
    SplitPane splitPane = new SplitPane();
    Button btn1 = new Button();
    Button btn2 = new Button();

    Button btn3 = new Button();
    Button btn4 = new Button("Historique");

    Button btnDeco = new Button("Deconnexion");

    Button ChangerMdp = new Button("Changer mot de passe");

    Button PolitiqueButton = new Button("Politique de confidentialité");
    Label lblVote = new Label();

    boolean PanelOpen = false;

    boolean PaneHistoryOpen = false;

    boolean PaneChangeMdpOpen = false;

    boolean PolitiqueOpen = false;

    public volatile Group groupPie;

    public volatile PieChart chart;

    HBox hBox = new HBox();
    VBox vBox = new VBox();

    VBox vBoxLOGIN = new VBox();

    HBox buttonStack = new HBox();

    HBox CircleHBouton = new HBox();

    HBox funBox = new HBox();
    Button RedButton = new Button();
    Button GreenButton = new Button();
    Button YellowButton = new Button();


    Circle redCircle = new Circle(9, Color.RED);
    Circle greenCircle = new Circle(9, Color.GREEN);
    Circle YellowCircle = new Circle(9, Color.YELLOW);

    String ColorHex = "#191919";
    String ColorStyle="#5F5AA2";

    Image img = new Image("file:./resources/blahaj.png");

    ImageView gifBlahaj = new ImageView(new Image("file:./resources/blahspinny.gif"));
    ImageView logo = new ImageView(img);
    public volatile Label label;

    public volatile Label labelDejaVote= new Label("");

   volatile Sondage sondage;

    RequeteConnexion requeteConnexion;

    ConnexionReponse connexionReponse;

    ListView<Employe> listViewEmploye = new ListView<>();



    private double xOffset = 0;
    private double yOffset = 0;

    private double xOffsetPanel;

    private double yOffsetPanel;

    private String ip = "127.0.0.1"; // par defaut
    private int port = 5565;

    boolean Deconnexion = false;


    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException, InterruptedException {
        setKonami();

        System.setProperty("javax.net.ssl.trustStore", SSLConf.getInstance().getPath());
        System.setProperty("javax.net.ssl.trustStorePassword", SSLConf.getInstance().getPassword());

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
        primaryStage.getIcons().add(new Image("file:./resources/blahajLogo.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void mainScene(boolean isAdmin, Stage primaryStage, String ssid) throws IOException, ClassNotFoundException {
        root.getChildren().remove(vBoxLOGIN);
        getSondage();
        initButtons(primaryStage);
        initPaneAndBox(isAdmin, primaryStage,ssid);

    }



    public void ConnexionScene(Stage primaryStage){
        TextField Username = new TextField();
        PasswordField Password = new PasswordField();
        Button Connexion = new Button("Connexion");


        TextField IP = new TextField();
        TextField Port = new TextField();

        VBox vBoxMDPOUBLIE = new VBox();
        Button PasswordForgot = new Button("Mot de passe oublie");

        PasswordForgot.setStyle("-fx-background-color: #5F5AA2;-fx-text-fill: white;-fx-font-size: 15px;-fx-font-weight: bold;-fx-background-radius: 25px;");
        PasswordForgot.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(vBoxMDPOUBLIE.getChildren().size()==0){
                    Label label = new Label("Entrez votre adresse mail");
                    label.setStyle("-fx-text-fill: white;-fx-font-size: 15px;-fx-font-weight: bold;");
                    TextField questionMail = new TextField();
                    questionMail.setMaxWidth(200);
                    Button button = new Button("Envoyer");
                    Button Quitter = new Button("Quitter");

                    TextField code = new TextField();
                    code.setMaxWidth(200);
                    TextField nouveauMdp = new TextField();
                    nouveauMdp.setMaxWidth(200);
                    Button buttonCode = new Button("Envoyer");
                    buttonCode.setStyle("-fx-background-color: #5F5AA2;-fx-text-fill: white;-fx-font-size: 15px;-fx-font-weight: bold;-fx-background-radius: 25px;");
                    Label labelCode = new Label("Entrez code recu par mail");
                    labelCode.setStyle("-fx-text-fill: white;-fx-font-size: 15px;-fx-font-weight: bold;");
                    Label labelMdp = new Label("Entrez mot de passe");
                    labelMdp.setStyle("-fx-text-fill: white;-fx-font-size: 15px;-fx-font-weight: bold;");


                    vBoxMDPOUBLIE.getChildren().addAll(label,questionMail, button, Quitter);
                    vBoxMDPOUBLIE.setAlignment(Pos.CENTER);
                    vBoxMDPOUBLIE.setSpacing(10);
                    vBoxMDPOUBLIE.setMaxWidth(200);
                    vBoxMDPOUBLIE.setMaxHeight(200);

                    vBoxMDPOUBLIE.setStyle("-fx-background-color: " + ColorStyle + ";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");



                    button.setStyle("-fx-background-color: #5F5AA2;-fx-text-fill: white;-fx-font-size: 15px;-fx-font-weight: bold;-fx-background-radius: 25px;");
                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                                SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(ip, port);

                                RequeteMotDePasseOublie requeteMotDePasseOublie = new RequeteMotDePasseOublie(questionMail.getText());
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(sslsocket.getOutputStream());
                                objectOutputStream.writeObject(requeteMotDePasseOublie);
                                objectOutputStream.flush();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            vBoxMDPOUBLIE.getChildren().clear();

                            vBoxMDPOUBLIE.getChildren().addAll(labelCode,code,labelMdp,nouveauMdp, buttonCode,Quitter);

                        }
                    });

                    buttonCode.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                                SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(ip, port);

                                RequeteChangerMotDePasseOublie requeteChangerMotDePasseOublie = new RequeteChangerMotDePasseOublie(code.getText(),nouveauMdp.getText());
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(sslsocket.getOutputStream());
                                objectOutputStream.writeObject(requeteChangerMotDePasseOublie);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            vBoxMDPOUBLIE.getChildren().clear();
                            StackConnexion.getChildren().remove(vBoxMDPOUBLIE);
                        }
                    });

                    buttonCode.disableProperty().bind(Bindings.isEmpty(code.textProperty()).or(Bindings.isEmpty(nouveauMdp.textProperty())));

                    Quitter.setStyle("-fx-background-color: #5F5AA2;-fx-text-fill: white;-fx-font-size: 15px;-fx-font-weight: bold;-fx-background-radius: 25px;");
                    Quitter.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            StackConnexion.getChildren().remove(vBoxMDPOUBLIE);
                            vBoxMDPOUBLIE.getChildren().removeAll(label,labelCode,questionMail, button,buttonCode, Quitter);
                        }
                    });
                    StackConnexion.getChildren().add(vBoxMDPOUBLIE);
                }

        }});


        PolitiqueButton.setStyle("-fx-background-color: #5F5AA2;-fx-text-fill: white;-fx-font-size: 15px;-fx-font-weight: bold;-fx-background-radius: 25px;");
        PolitiqueButton.setOnAction(E -> {
            PolitiqueOpen = !PolitiqueOpen;
            if (PolitiqueOpen) {
                PolitiqueScene();

            } else {


                root.getChildren().remove(StackPolitique);
            }
        });




        HBox hBox = new HBox();

        VBox vBox2 = new VBox();
        vBox2.getChildren().addAll(Username,Password,IP,Port);
        vBox2.setMaxWidth(160);

        vBox2.setSpacing(10);

        hBox.getChildren().add(Connexion);
        vBoxLOGIN.getChildren().clear();
        vBoxLOGIN.getChildren().addAll(logo,vBox2,hBox);

        logo.setFitHeight(110);
        logo.setPreserveRatio(true);

        vBoxLOGIN.setSpacing(10);
        StackConnexion.getChildren().clear();
        StackConnexion.getChildren().add(vBoxLOGIN);

        StackConnexion.getChildren().add(PasswordForgot);
        StackPane.setAlignment(PasswordForgot, Pos.BOTTOM_RIGHT);
        PasswordForgot.setTranslateX(-30);
        PasswordForgot.setTranslateY(-30);
        StackConnexion.getChildren().add(PolitiqueButton);
        StackPane.setAlignment(PolitiqueButton, Pos.BOTTOM_CENTER);
        PolitiqueButton.setTranslateY(-30);

        root.getChildren().add(StackConnexion);


        vBoxLOGIN.setAlignment(Pos.CENTER);

        hBox.setAlignment(Pos.CENTER);

        Username.setPromptText("Username");
        Password.setPromptText("Password");

        Username.setStyle("-fx-background-color: #191919;-fx-text-fill: #5F5AA2;-fx-border-color: #5F5AA2;-fx-border-width: 2px;");
        Password.setStyle("-fx-background-color: #191919;-fx-text-fill: #5F5AA2;-fx-border-color: #5F5AA2;-fx-border-width: 2px;");
        IP.setStyle("-fx-background-color: #191919;-fx-text-fill: #5F5AA2;-fx-border-color: #5F5AA2;-fx-border-width: 2px;");
        IP.setPromptText("IP");
        Port.setStyle("-fx-background-color: #191919;-fx-text-fill: #5F5AA2;-fx-border-color: #5F5AA2;-fx-border-width: 2px;");
        Port.setPromptText("Port");

        Connexion.setStyle("-fx-background-color: #5F5AA2; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-background-radius: 25px; -fx-padding: 10px 20px 10px 20px;");

        Connexion.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ip = IP.getText();
                port = Integer.parseInt(Port.getText());
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
                        root.getChildren().remove(StackConnexion);
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
                                                    mainScene(connexionReponse.getEmploye().isEstAdmin(), primaryStage, connexionReponse.getSsid());
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
        Connexion.disableProperty().bind(Bindings.isEmpty(Username.textProperty()).or(Bindings.isEmpty(Password.textProperty()).or(Bindings.isEmpty(IP.textProperty()).or(Bindings.isEmpty(Port.textProperty())))));
        buttonStack.toFront();
    }

    private void PolitiqueScene() {

        StackPolitique.setStyle("-fx-background-color: #af8bf1;-fx-border-radius: 25px;-fx-background-radius: 25px;");
        StackPolitique.setMaxHeight(550);
        StackPolitique.setMaxWidth(700);
        VBox vBoxPolitique = new VBox();
        vBoxPolitique.setSpacing(10);
        vBoxPolitique.setAlignment(Pos.CENTER);
        Label labelPolitique = new Label("Politique de confidentialité");
        Text textPolitique = new Text("Dans le cadre de son activité, SharkVote, dont le siège social est situé à Montpellier, est amenée à collecter et à traiter des informations dont certaines sont qualifiées de « données personnelles ». SharkVote attache une grande importance au respect de la vie privée, et n’utilise que des données de manière responsable et confidentielle et dans une finalité précise.\n" +
                "\n" +
                "Données personnelles\n" +
                "Sur l’application de vote SharkVote, seuls les données transmis directement, via un formulaire de contact remplis par l'admin de l'entreprise dans laquelle vous travaillez, sont utilisés. Seul le nom, prénom et l’email fourni par l’entreprise est obligatoire.\n" +
                "\n" +
                "Utilisation des données\n" +
                "Les données que vous nous transmettez directement sont utilisées dans le but de vous permettre de voter sur les référendums établis par l’entreprise dans laquelle vous travaillez. Les votes sont chiffrés pour conserver l’anonymat de chaque voteur, SharkVote et le client n’a pas accès à vos votes de manière personnelle et peut seulement voir le résultat du vote. Seul les admins de l'entreprise a accès a votre identité (nom, prénom).\n" +
                "\n" +
                "Base légale\n" +
                "Les données personnelles ne sont collectées qu’après consentement obligatoire de l’utilisateur. Ce consentement est valablement recueilli (boutons et cases à cocher), libre, clair et sans équivoque.\n" +
                "\n" +
                "Durée de conservation\n" +
                "Les données seront sauvegardées pour une durée maximale d’1 an en cas d'inactivité.\n" +
                "\n" +
                "Vos droits concernant les données personnelles\n" +
                "Vous avez le droit de consultation, de modification ou d’effacement sur l’ensemble de vos données personnelles.");

        textPolitique.setWrappingWidth(600);
        labelPolitique.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 42px; -fx-font-weight: bold;");
        //set the text in white
        textPolitique.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 13px;");


        vBoxPolitique.getChildren().addAll(labelPolitique, textPolitique);
        StackPolitique.getChildren().add(vBoxPolitique);
        root.getChildren().add(StackPolitique);
    }


    public synchronized void ResultScene(){
        root.getChildren().remove(StackVote);
        root.getChildren().remove(label);
        root.getChildren().remove(stackPanePanelHisto);
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
                if(Deconnexion){
                    this.cancel();
                    System.out.println("get sondage annulé");
                }
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

                        Platform.runLater(()-> {
                            lblVote.setText("Aucun sondage en cours");
                            btn1.setText("N");
                            btn1.setDisable(true);
                            btn2.setText("A");
                            btn2.setDisable(true);
                            vBox.getChildren().remove(labelDejaVote);

                            root.getChildren().remove(chart);
                            root.getChildren().remove(StackVote);
                            root.getChildren().add(StackVote);
                        });
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
                           if(aDejaVote(connexionReponse.getSsid())){
                               btn1.setDisable(true);
                               btn2.setDisable(true);
                               labelDejaVote.setText("Vous avez deja emis un vote !");

                                 labelDejaVote.setStyle("-fx-font-size: 32;-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #fff;");
                           }
                            else{
                                 labelDejaVote.setText("");
                                 labelDejaVote.setVisible(false);
                            }


                       });

                    }
                    SSLSocketFactory socketFactory2 = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    Socket socket2 = (SSLSocket) socketFactory2.createSocket(ip, port);

                    RequeteEstConnecte req2 = new RequeteEstConnecte(connexionReponse.getSsid());
                    ObjectOutputStream out2 = new java.io.ObjectOutputStream(socket2.getOutputStream());
                    out2.writeObject(req2);
                    out2.flush();

                    //recupère l'inputstream du socket
                    java.io.ObjectInputStream in2 = new java.io.ObjectInputStream(socket2.getInputStream());

                    boolean estConnecte = (boolean) in2.readObject();
                    System.out.println("est connecté: " + estConnecte);
                    if(!estConnecte){
                        if(!Deconnexion){
                        System.out.println("vous avez été déconnecté");
                        Platform.runLater(()->{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erreur");
                            alert.setHeaderText("Vous avez été déconnecté du serveur");
                            alert.setContentText("Veuillez vous reconnecter");
                            alert.showAndWait();
                            System.exit(0);
                        });}
                    }

                    //fermeture du flux d'entrée
                    in.close();
                    //fermeture du flux de sortie
                    out.close();



                }catch (IOException | ClassNotFoundException e){
                    Platform.runLater(()->{
                        System.out.println("Erreur lors de la récupération du sondage");
                        lblVote.setText("La connexion au serveur n'est pas disponible");
                        btn1.setText("N");
                        btn1.setDisable(true);
                        btn2.setText("A");
                        btn2.setDisable(true);
                    });


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



    public void initButtons(Stage primaryStage) {

        btnDeco.setOnAction(event -> {
            root.getChildren().remove(StackVote);
            root.getChildren().remove(btn3);
            root.getChildren().remove(btn4);
            root.getChildren().remove(btnDeco);
            root.getChildren().remove(ChangerMdp);
            root.getChildren().remove(stackPanePanel);
            root.getChildren().remove(label);

            Deconnexion = true;
            deconnexion(primaryStage);
            buttonStack.toFront();

        });


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

        btn4.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn4.setEffect(new Glow());
            }
        });
        btn4.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn4.setEffect(null);
            }
        });

        btnDeco.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btnDeco.setEffect(new Glow());
            }
        });
        btnDeco.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btnDeco.setEffect(null);
            }
        });

        ChangerMdp.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ChangerMdp.setEffect(new Glow());
            }
        });
        ChangerMdp.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ChangerMdp.setEffect(null);
            }
        });

        PolitiqueButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                PolitiqueButton.setEffect(new Glow());
            }
        });
        PolitiqueButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                PolitiqueButton.setEffect(null);
            }
        });



        lblVote.setFont(new javafx.scene.text.Font(26));
        lblVote.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btnDeco.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn3.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        ChangerMdp.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn4.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
    }

    private void deconnexion(Stage primaryStage) {
        try {
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(ip, port);

            RequeteDeconnexion req = new RequeteDeconnexion(connexionReponse.getSsid());
            ObjectOutputStream out = new ObjectOutputStream(sslsocket.getOutputStream());
            out.writeObject(req);
            out.flush();

            ConnexionScene(primaryStage);

        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void AdminPanelScene(Stage primaryStage){
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
                stackPanePanel.setTranslateX(290);

            });
            Button btn2Panel = new Button("Supprimer");
            btn2Panel.setOnAction(e -> {
                supprimerUtilisateur(listView);
                rafraichirUtilisateurs(listView);
                //move the panel to the right
                stackPanePanel.setTranslateX(290);
            });
            Button btn3Panel = new Button("Modifier");
            btn3Panel.setOnAction(e -> {
                modifierUtilisateur(listView);
                rafraichirUtilisateurs(listView);
                stackPanePanel.setTranslateX(290);
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
                    Socket socket = socketFactory.createSocket(ip, port);

                    RequeteFermerRecolte req = new RequeteFermerRecolte(connexionReponse.getSsid());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(req);
                    oos.flush();
                    Socket socket2 = socketFactory.createSocket(ip, port);
                    ObjectOutputStream oos2 = new ObjectOutputStream(socket2.getOutputStream());
                    RequetePublierResultat req2 = new RequetePublierResultat(connexionReponse.getSsid());
                    oos2.writeObject(req2);
                    oos2.flush();
                    stackPanePanel.toFront();
                    getSondage();

                } catch (IOException | ClassNotFoundException ex) {
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
                    stackPanePanel.setTranslateX(290);
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

    Button btn = new Button("Creer");
    btn.setOnAction(e->{
        String consigne = textField.getText();
        String choix1 = textField2.getText();
        String choix2 = textField3.getText();
        if(consigne.equals("") || choix1.equals("") || choix2.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
        }else{
            try{
                SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                RequeteCreerSondage req = new RequeteCreerSondage(consigne, choix1, choix2,connexionReponse.getSsid());
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
    vbox.getChildren().addAll(textField, textField2, textField3, hbox);
    vbox.setAlignment(Pos.CENTER);
    hbox.setAlignment(Pos.CENTER);
    paneUser.getChildren().add(vbox);
    StackPane.setAlignment(vbox, Pos.CENTER);
    root.getChildren().add(paneUser);
}

    public void initPaneAndBox(boolean isAdmin,Stage primaryStage, String ssid){


        funBox.getChildren().clear();
        funBox.getChildren().add(lblVote);
        logo.setPreserveRatio(true);
        logo.setFitHeight(100);
        vBox.getChildren().clear();
        vBox.getChildren().add(logo);
        vBox.getChildren().add(labelDejaVote);
        vBox.getChildren().add(funBox);
        vBox.getChildren().add(hBox);
        hBox.getChildren().clear();
        hBox.getChildren().addAll(btn1, btn2);





        if(isAdmin) {
            root.getChildren().add(btn3);
            btn3.setText("Admin Panel");
            btn3.setOnAction(e -> {
                AdminPanelScene(primaryStage);
            });
        }
        root.getChildren().add(btn4);
        root.getChildren().add(btnDeco);
        root.getChildren().add(ChangerMdp);
        StackPane.setAlignment(btn4, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(btn3, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(btnDeco, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(ChangerMdp, Pos.BOTTOM_LEFT);
        btn4.setText("Historique");
        StackPane.setMargin(btnDeco, new Insets(0, 40, 85, 0));
        StackPane.setMargin(btn3, new Insets(0, 40, 40, 0));
        StackPane.setMargin(btn4, new Insets(0, 0, 40, 40));
        StackPane.setMargin(ChangerMdp, new Insets(0, 0, 85, 40));

        btn4.setOnAction(e -> {
            PaneHistoryOpen = !PaneHistoryOpen;
            if (PaneHistoryOpen) {
                HistoriqueScene(primaryStage);
                StackVote.setVisible(false);
            } else {
                StackVote.setVisible(true);

                root.getChildren().remove(stackPanePanelHisto);
            }
        });

        ChangerMdp.setOnAction(e -> {
             PaneChangeMdpOpen = !PaneChangeMdpOpen;
            if (PaneChangeMdpOpen) {
                ChangeMdpScene(primaryStage);
                StackVote.setVisible(false);
            } else {
                StackVote.setVisible(true);

                root.getChildren().remove(stackPanePanelChangeMdp);
            }
        });

        vBox.setAlignment(Pos.CENTER);
        hBox.setAlignment(Pos.CENTER);
        funBox.setAlignment(Pos.CENTER);
        funBox.setSpacing(20);
        hBox.setSpacing(50);
        vBox.setSpacing(70);
        StackVote.getChildren().clear();
        StackVote.getChildren().add(vBox);
        StackVote.setMaxHeight(300);



        StackVote.setAlignment(Pos.CENTER);

        root.getChildren().add(StackVote);

    }



    private boolean aDejaVote(String ssid){
        try{
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

            RequeteGetADejaVote req = new RequeteGetADejaVote(ssid);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(req);
            oos.flush();

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            return (boolean) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void ChangeMdpScene(Stage primaryStage) {
        stackPanePanelChangeMdp = new StackPane();
        stackPanePanelChangeMdp.setStyle("-fx-background-color: #891bd7;-fx-border-color: #000000;-fx-border-width: 2px;-fx-border-radius: 10px;-fx-background-radius: 10px;");
        stackPanePanelChangeMdp.setMaxHeight(300);
        stackPanePanelChangeMdp.setMaxWidth(400);
        VBox vboxMDP = new VBox();
        vboxMDP.setSpacing(10);
        vboxMDP.setAlignment(Pos.CENTER);
        Label labelMDP = new Label("Changer de mot de passe");
        labelMDP.setStyle("-fx-text-fill: #ffffff;-fx-font-size: 26px;-fx-font-weight: bold;");

        TextField textFieldMDP = new TextField();
        textFieldMDP.setPromptText("Ancien mot de passe");
        textFieldMDP.setPadding(new Insets(10, 10, 10, 10));
        textFieldMDP.setMaxWidth(200);
        TextField textFieldMDP2 = new TextField();
        textFieldMDP2.setPromptText("Nouveau mot de passe");
        textFieldMDP2.setPadding(new Insets(10, 10, 10, 10));
        textFieldMDP2.setMaxWidth(200);
        TextField textFieldMDP3 = new TextField();
        textFieldMDP3.setPromptText("Confirmer mot de passe");
        textFieldMDP3.setPadding(new Insets(10, 10, 10, 10));
        textFieldMDP3.setMaxWidth(200);
        Button btnMDP = new Button("Valider");
        btnMDP.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        vboxMDP.getChildren().addAll(labelMDP,textFieldMDP, textFieldMDP2, textFieldMDP3, btnMDP);

        stackPanePanelChangeMdp.getChildren().add(vboxMDP);
        StackPane.setAlignment(vboxMDP, Pos.CENTER);
        root.getChildren().add(stackPanePanelChangeMdp);
        btnMDP.setOnAction(e -> {
            if (textFieldMDP.getText().equals("") || textFieldMDP2.getText().equals("") || textFieldMDP3.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur");
                alert.setContentText("Veuillez remplir tous les champs");
                alert.showAndWait();
            } else {
                if (textFieldMDP2.getText().equals(textFieldMDP3.getText())) {
                    try {
                        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                        Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                        RequeteChangePassword req = new RequeteChangePassword( textFieldMDP2.getText(),connexionReponse.getSsid());
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject(req);
                        oos.flush();
                        System.out.println("Envoi de la requete");
                        root.getChildren().remove(stackPanePanelChangeMdp);
                        StackVote.setVisible(true);

        } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

    }
    private void HistoriqueScene(Stage primaryStage) {

            StackVote.setVisible(false);
            stackPanePanelHisto.setStyle("-fx-background-color: transparent;");
            stackPanePanelHisto.setMaxWidth(500);
            stackPanePanelHisto.setMaxHeight(500);
            StackPane.setAlignment(stackPanePanelHisto, Pos.CENTER);
            root.getChildren().add(stackPanePanelHisto);
            try {
                SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);
                RequeteHistory req = new RequeteHistory();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(req);
                oos.flush();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ArrayList<Sondage> sondages = (ArrayList<Sondage>) ois.readObject();
                VBox vBox = new VBox();
                vBox.setSpacing(10);
                vBox.setAlignment(Pos.CENTER);
                vBox.setStyle("-fx-background-color: " + ColorStyle + "; -fx-border-color: #000000; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
                ScrollPane scrollPane = new ScrollPane();

                for (Sondage sondage : sondages) {
                    HBox hBox = new HBox();
                    hBox.setSpacing(10);
                    hBox.setAlignment(Pos.CENTER);
                    System.out.println(sondage.getNbVotant());
                    System.out.println(sondage.getResultat());
                    System.out.println(" ");
                    Label label = new Label(sondage.getConsigne() + " : " + sondage.getChoix1() + " (" + (sondage.getNbVotant()-sondage.getResultat()) + "), " + sondage.getChoix2() + " (" + sondage.getResultat() + ")");
                    label.setStyle("-fx-font-size: 20px;");
                    hBox.getChildren().add(label);
                    vBox.getChildren().add(hBox);
                }
                ScrollBar scrollBar = new ScrollBar();

                scrollPane.setContent(vBox);
                stackPanePanelHisto.getChildren().add(scrollPane);


                vBox.maxWidth(250);
                StackPane.setAlignment(scrollPane, Pos.CENTER);

                scrollPane.setStyle("-fx-background-color: transparent;-fx-background: transparent;");
                scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setFitToWidth(true);
                scrollPane.setPannable(true);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

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
            //si l'utilisateur sélectionner est le même que celui qui est connecté
            if (view.getSelectionModel().getSelectedItem().equals(connexionReponse.getEmploye().getNom() + " " + connexionReponse.getEmploye().getPrenom())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Vous ne pouvez pas supprimer votre propre compte");
                alert.setContentText("Veuillez vous déconnecter et supprimer votre compte depuis un autre compte");
                alert.showAndWait();
            } else {


                listViewEmploye.getSelectionModel().select(view.getSelectionModel().getSelectedIndex());
                SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                Socket socket = (SSLSocket) socketFactory.createSocket(ip, port);

                RequeteDeleteUser req = new RequeteDeleteUser(listViewEmploye.getSelectionModel().getSelectedItem().getEmail(), connexionReponse.getSsid());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(req);
                oos.flush();
                oos.close();
                // socket.close();
            }
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





