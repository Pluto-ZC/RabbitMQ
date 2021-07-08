package com.example.rabbitmq.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DirectConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {

        //1. 创建一个 ConnectionFactory 并进行设置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setAutomaticRecoveryEnabled(true);  //网络异常  自动连接恢复  true  恢复  false 不恢复
        factory.setNetworkRecoveryInterval(3000);   //连接恢复间隔时间 默认5000  毫秒  每3秒尝试一次连接

        //2. 通过连接工厂来创建连接
        Connection connection = factory.newConnection();

        //3. 通过 Connection 来创建 Channel
        Channel channel = connection.createChannel();

        //4. 声明
        String exchangeName = "test_direct_exchange";
        String queueName = "test_direct_queue";
        String routingKey = "item.direct";
        channel.exchangeDeclare(exchangeName,"direct",true,false,null);
        channel.queueDeclare(queueName,false,false,false,null);

        //一般不用代码绑定，在管理界面手动绑定
        channel.queueBind(queueName,exchangeName,routingKey);

        //5 创建消费者并接收消息
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("received::"+message);
            }
        };

        //6 设置 channel  消费者绑定队列
        channel.basicConsume(queueName,true,consumer);
    }
}



















