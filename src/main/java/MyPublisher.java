import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;
import java.util.concurrent.Callable;

public class MyPublisher implements Callable<String> {
    IMqttClient client;
    String TOPIC;

    MyPublisher(IMqttClient client, String TOPIC) {
        this.client = client;
        this.TOPIC = TOPIC;
    }

    @Override
    public String call() throws Exception {
        if ( !client.isConnected()) {
            return null;
        }
        MqttMessage msg = readSensorValue();
        msg.setQos(0);
        msg.setRetained(true);
        client.publish(TOPIC,msg);
        return "Published : "+msg;
    }

    static int counter=0;
    private MqttMessage readSensorValue() {
        Random rnd = new Random();
        //double value =  80 + rnd.nextDouble() * 20.0;
        //byte[] payload = String.format("T:%04.2f",value).getBytes();
        byte[] payload = String.format("%d",counter++).getBytes();
        return new MqttMessage(payload);
    }
}
