import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class PubThread implements Runnable{
    IMqttClient mqttClient;
    String TOPIC;

    PubThread(IMqttClient mqttClient, String TOPIC) {
        this.mqttClient = mqttClient;
        this.TOPIC = TOPIC;
    }

    @Override
    public void run() {
        //based on tutorial @ https://www.baeldung.com/java-mqtt-client

        //Info Callable - https://www.edureka.co/blog/callable-interface-in-java/
        ExecutorService executor = Executors.newFixedThreadPool(10);
        //create a list to hold the Future object associated with Callable
        List<Future<String>> list = new ArrayList<Future<String>>();
        //Create MyCallable instance
        Callable<String> callable = new MyPublisher(mqttClient, TOPIC);
        for(int i=0; i< 10; i++){
            //submit Callable tasks to be executed by thread pool
            Future<String> future = executor.submit(callable);
            //add Future to the list, we can get return value using Future
            list.add(future);
        }
        for(Future<String> fut : list){
            try {
                //print the return value of Future, notice the output delay in console
                // because Future.get() waits for task to get completed
                System.out.println(fut.get()+ " :: "+new Date());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        executor.shutdown();
    }
}
