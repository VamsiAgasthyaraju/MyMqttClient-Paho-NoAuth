import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class SubThread implements Runnable {

    IMqttClient mqttClient;
    String TOPIC;
    CountDownLatch counter;

    SubThread(IMqttClient mqttClient, String TOPIC, CountDownLatch counter) {
        this.mqttClient = mqttClient;
        this.TOPIC = TOPIC;
        this.counter = counter;
    }
    @Override
    public void run() {
        try {
            mqttClient.subscribe(TOPIC, 2, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    System.out.println("Subscribed \t TOPIC: "+ topic +"\t MSG: "+ message+ " :: "+ new Date());
                    counter.countDown();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
