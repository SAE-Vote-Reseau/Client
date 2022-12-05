package vote.Client;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import jdk.jfr.Category;
import vote.crypto.ElGamal;
import vote.crypto.Message;
import vote.Urne.RequeteGetSondage;
import vote.Urne.RequeteVote;
import vote.Urne.Sondage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sun.glass.events.KeyEvent.*;


public class AppVote extends Application {


    Button btn1 = new Button();
    public StackPane root = new StackPane();

    public volatile StackPane StackVote = new StackPane();


    Button btn2 = new Button();
    Label lblVote = new Label();

    public volatile Group groupPie;


    HBox hBox = new HBox();
    VBox vBox = new VBox();

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

    private double xOffset = 0;
    private double yOffset = 0;


    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException, InterruptedException {
        setKonami();
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

    public synchronized void ResultScene(){
        if(root.getChildren().contains(StackVote)){
            root.getChildren().remove(StackVote);
        }
        if(root.getChildren().contains(label)){
            root.getChildren().remove(label);
        }
        groupPie = new Group();
        PieChart chart;

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

            final Label caption = new Label("");


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
            Socket socket = new Socket("127.0.0.1", 5565);

            ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
            RequeteVote req = new RequeteVote(voteChiffre);
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
                    Socket socket = new Socket("127.0.01", 5565);


                    RequeteGetSondage req = new RequeteGetSondage();


                    java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(req);
                    out.flush();
                    //recupère l'inputstream du socket
                    java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());

                    //lit le message envoyé par le serveur
                    sondage = (Sondage) in.readObject();

                    if (sondage == null) {
                        lblVote.setText("Aucun sondage en cours");
                        btn1.setText("N");
                        btn1.setDisable(true);
                        btn2.setText("A");
                        btn2.setDisable(true);


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
            }
        };
        Timer timerSondage = new Timer("SondageTimer");
        timerSondage.scheduleAtFixedRate(getSondageTask, 0, 5000);

    }








    public void initButtons(Stage primaryStage){
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








        lblVote.setFont(new javafx.scene.text.Font(26));
        lblVote.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn1.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        btn2.setStyle("-fx-font-family: 'Open Sans'; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: "+ColorStyle+";  -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
    }

    public void initPaneAndBox(){
HBox funBox = new HBox();
        funBox.getChildren().add(lblVote);
        //set insets



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

    private static class ConnectException extends Exception {
        public ConnectException(String exception) {
            super(exception);
        }
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





