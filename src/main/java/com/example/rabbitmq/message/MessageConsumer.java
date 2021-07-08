package com.example.rabbitmq.message;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;

public class MessageConsumer {
    public static void main(String[] args) throws Exception{
        //1. 创建一个 ConnectionFactory 并进行设置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(3000);

        //2. 通过连接工厂来创建连接
        Connection connection = factory.newConnection();

        //3. 通过 Connection 来创建 Channel
        Channel channel = connection.createChannel();

        //4. 声明
        String queueName = "msg_queue";
        channel.queueDeclare(queueName, false, false, false, null);

        //5. 创建消费者并接收消息
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                Map<String, Object> headers = properties.getHeaders();
                System.out.println("head: " + headers.get("myheader1"));
                System.out.println(" [x] Received '" + message + "'");
                System.out.println("expiration : "+ properties.getExpiration());
            }
        };

        //6. 设置 Channel 消费者绑定队列
        channel.basicConsume(queueName, true, consumer);
    }
}
