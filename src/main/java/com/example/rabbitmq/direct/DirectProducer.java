package com.example.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DirectProducer {
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

        //4 声明
        String exchangeName = "test_direct_exchange";
        String routingKey = "item.direct";

        //5 发送
        String msg = "this is direct msg";
        channel.basicPublish(exchangeName,routingKey,null,msg.getBytes());
        System.out.println("send message:" + msg);

        //6. 关闭连接
        channel.close();
        connection.close();
    }
}






