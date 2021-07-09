package com.example.rabbitmq.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DlxProducer {
    public static void main(String[] args) throws IOException, TimeoutException {

        //1. 创建一个 ConnectionFactory 并进行设置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");

        //2. 通过连接工厂来创建连接
        Connection connection = factory.newConnection();

        //3. 通过 Connection 来创建 Channel
        Channel channel = connection.createChannel();

        //设置连接以及创建 channel
        String exchangeName = "test_dlx_exchange";
        String routingKey = "item.update";

        String msg = "this is dlx msg";

        //我们设置消息过期时间，10秒后再消费 让消息进入死信队列
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .expiration("10000")
                .build();

        channel.basicPublish(exchangeName, routingKey, true, properties, msg.getBytes());
        System.out.println("Send message : " + msg);

        channel.close();
        connection.close();
    }
}
