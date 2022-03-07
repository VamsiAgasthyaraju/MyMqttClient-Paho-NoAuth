import org.eclipse.paho.client.mqttv3.*;

import java.util.UUID;
import java.util.concurrent.*;

public class MyClient {
    public static void main(String[] args) {
        System.out.println("Begin ...");
        MyClient obj = new MyClient();
        obj.code();
        System.out.println("Done.");
    }
    void code(){

        //Create a client   <<==========================
        System.out.println("Creating client");
        IMqttClient mqttClient;
        //IMqttClient mqttClientPub, mqttClientSub; //Cam also have individual clients
        String publisherId = UUID.randomUUID().toString();
        String TOPIC = "sensor/value";
        try {
            // running mqtt broker "mosquitto" locally in docker
            mqttClient = new MqttClient("tcp://localhost:1883", publisherId);
            // docker pull eclipse-mosquitto with default configuration
            // while running the image via UI select customize and enter port 1883 for local
            // inside the image "cp /mosquitto-no-auth.config /mosquitto/config/mosquitto.conf"
            // (have only two lines in config #1. listener 1883 #2. allow_anonymous true)

            //running from free mosquitto brokers
            //using same client for both pub sub
            //mqttClient = new MqttClient("tcp://test.mosquitto.org:1883", publisherId);

            //can have individual client connects for pub and sub.
            //mqttClientPub = new MqttClient("tcp://test.mosquitto.org:1883", publisherId);
            //mqttClientSub = new MqttClient("tcp://test.mosquitto.org:1883", publisherId);

        } catch (MqttException e) {
            e.printStackTrace();
            return;
        }

        //connect   <<==========================
        System.out.println("Connecting to client");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        options.setConnectionTimeout(10);
        try {
            mqttClient.connect(options);
            //mqttClientPub.connect(options);
            //mqttClientSub.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
            return;
        }

        //Publish   <<==========================
        Runnable pub = new PubThread(mqttClient,TOPIC);
        //Runnable pub = new PubThread(mqttClientPub,TOPIC);
        new Thread(pub).start();

        //Subscribe <<==========================
        //Info CountDownLatch - https://www.geeksforgeeks.org/countdownlatch-in-java/
        CountDownLatch latch = new CountDownLatch(10);
        Runnable sub = new SubThread(mqttClient, TOPIC, latch);
        //Runnable sub = new SubThread(mqttClientSub, TOPIC, latch);
        new Thread(sub).start();

        try {
            latch.await(1,TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            //for graceful closure of this program. In real usage the clients may opt to be continuously active
            mqttClient.disconnect();
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
