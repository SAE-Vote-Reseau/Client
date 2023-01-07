package vote.Client;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SSLConf {

    public String getPath() {
        return path;
    }

    public String getPassword() {
        return password;
    }

    private String path,password;

    private static SSLConf instance;

    public SSLConf() {
        try{
            File file = new File("./TLS.props");
            if (file.exists()) {
                System.out.println("file au répertoire : " + file.getAbsolutePath());
                Properties props = new Properties();
                FileInputStream fis = new FileInputStream(file);
                props.load(fis);

                path = props.getProperty("pathTrust");
                password = props.getProperty("password");

                if(path == null || password == null){
                    throw new RuntimeException("Poprietes invalides");
                }

                fis.close();
            } else {
                file.createNewFile();
                Properties props = new Properties();
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(file);
                props.load(fis);
                props.setProperty("pathTrust","A remplir");
                props.setProperty("password","A remplir");

                props.store(fos,"PROPERTIES");
                fis.close();
                fos.close();
                throw new RuntimeException("Veuillez remplir le fichier de configuration");
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static SSLConf getInstance(){
        if(instance == null){
            instance = new SSLConf();
        }
        return instance;
    }
}
