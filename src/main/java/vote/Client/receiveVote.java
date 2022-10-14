package vote.Client;

import java.net.Socket;

public class receiveVote {
    //reçois le vote du serveur
    public static void main(String[] args) {

        try{
            //création d'un socket client
            java.net.ServerSocket Serversocket = new java.net.ServerSocket(8080);
            Socket ClientSocket = Serversocket.accept();
            //création d'un flux d'entrée
            java.io.DataInputStream in = new java.io.DataInputStream(ClientSocket.getInputStream());
            //lecture du flux d'entrée
            String vote = in.readUTF();
            System.out.println("vote reçu : " + vote);
            //fermeture du flux d'entrée
            in.close();
            //fermeture du socket
            ClientSocket.close();
            Serversocket.close();
        }
        catch (java.io.IOException e){
            e.printStackTrace();
        }
main(args);

}}
