package com.example.rabbitmq.acknack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * 1、首先我们发送五条消息，将每条消息对应的循环下标i放入消息 properties 中作为标记  以便我们在后面的回调方法中识别
 * 2、我们将消费端的channel.basicConsume(queueName,false,consumer);中autoAck 属性设置为 false，如果设置为true的话将正常输出五条消息
 * 3、通过 Thread.sleep(2000)来延时一秒，用来看清结果。我们获取到properties中的num之后
 * 通过channel.basicNack(envelope.getDeliverTag(),false,true);将 num 为 0 的消息设置为nack，即消费失败，
 * 并且将requeue属性设置为true，即消费失败的消息重回队列末端
 */
public class AckAndNackProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_ack_exchange";
        String routingKey = "item.update";

        String msg = "this is ack msg";
        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> headers = new HashMap<>();
            headers.put("num", i);
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .headers(headers)
                    .build();

            String tem = msg + ":" + i;

            channel.basicPublish(exchangeName, routingKey, true, properties, tem.getBytes());
            System.out.println("send message");

        }

        channel.close();
        connection.close();
    }
}


















