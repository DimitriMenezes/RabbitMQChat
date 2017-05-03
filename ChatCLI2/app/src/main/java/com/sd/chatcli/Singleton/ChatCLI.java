package com.sd.chatcli.Singleton;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import com.sd.chatcli.Models.Message;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


public class ChatCLI {


    private static ChatCLI INSTANCE;

    private boolean durable = true;

    Thread subscribeThread;
    Thread publishThread;

    Context context;

    private BlockingDeque queue;

    ConnectionFactory factory;

    public void send(Message message) {

        try {
            Log.d("","[q] " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void setupConnectionFactory() {
        Log.v("chatcli", "setup");
        factory.setAutomaticRecoveryEnabled(false);
        factory.setUsername("admin");
        factory.setVirtualHost("admin");
        factory.setPassword("admin");
        factory.setHost("34.210.62.22");
    }

    private ChatCLI() {
        queue = new LinkedBlockingDeque();
        factory = new ConnectionFactory();
        setupConnectionFactory();
    }

    public static ChatCLI getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new ChatCLI();
        return INSTANCE;
    }



    public void startService() throws IOException {
//            subscribe();
            publishToAMQP();

    }


    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Log.i("publishThread", "publishThread");

                        Address[] addresses = {
                                new Address("34.210.62.22",5672),
                                new Address("54.68.0.225",5672),
                                new Address("54.70.69.75",5672),
                        };
                        Connection connection = factory.newConnection(addresses);
                        Channel ch = connection.createChannel();
                        ch.confirmSelect();

                        while (true) {
                            Message message = (Message) queue.takeFirst();
                            try{
                                ch.queueDeclare(message.getReceiver(), durable, false, false, null);
                                Gson gson = new Gson();
                                //AQUI
                                ch.basicPublish("amq.direct", message.getReceiver(), MessageProperties.PERSISTENT_TEXT_PLAIN, gson.toJson(message).getBytes());
                                Log.d("", "[s] " + message);
                                ch.waitForConfirmsOrDie();
                            } catch (Exception e){
                                Log.d("","[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(200); //sleep and then try again
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                }
            }
        });
        publishThread.setName("publishThread");
        publishThread.setPriority(Thread.MAX_PRIORITY);
        publishThread.start();
    }



    public void subscribe(final Handler handler)
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {
                    try {
                        Log.i("subscribeThread", "subscribeThread");

                        Address[] addresses = {
                                new Address("34.210.62.22",5672),
                                new Address("54.68.0.225",5672),
                                new Address("54.70.69.75",5672),
                        };

                        Connection connection = factory.newConnection(addresses);
                        Channel channel = connection.createChannel();
                        channel.basicQos(1);
                        AMQP.Queue.DeclareOk q = channel.queueDeclare(Sender.getINSTANCE().getUsername(), true, false, false, null);
                        //AQUI
                        channel.queueBind(q.getQueue(), "amq.default", Sender.getINSTANCE().getUsername());
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(q.getQueue(), true, consumer);

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                            String message = new String(delivery.getBody());
                            Log.d("","[r] " + message);

                            android.os.Message msg = handler.obtainMessage();
                            Bundle bundle = new Bundle();

                            bundle.putString("msg", message);
                            msg.setData(bundle);
                            handler.sendMessage(msg);

                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        Log.e("", "Connection broken: " + e1.getClass().getName());
                        try {
                            Thread.sleep(200); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.setName("subscribeThread");
        subscribeThread.setPriority(Thread.MAX_PRIORITY);
        subscribeThread.start();
    }

    public void setContext(Context context) {
        destroy();
        reset();
        INSTANCE.context = context;

    }


    public void reset(){
        INSTANCE = new ChatCLI();
    }

    public void destroy(){
        if(publishThread!=null)
            publishThread.interrupt();
        if(subscribeThread!=null)
            subscribeThread.interrupt();
    }
}
