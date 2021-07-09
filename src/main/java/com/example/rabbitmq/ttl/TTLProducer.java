package com.example.rabbitmq.ttl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 *  消息的ttl
 *      代码
 *      进入后台管理页面进入Exchange发送消息指定expiration
 *  队列的ttl
 *      在后台管理界面新增queue，创建时可以设置ttl，对于队列中超过该时间的消息将会被移除
 */
public class TTLProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        //1 创建一个ConnectionFactory 并进行设置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");

        //2 通过连接工厂创建连接
        Connection connection = factory.newConnection();

        //3 通过Connection 来创建Channel
        Channel channel = connection.createChannel();

        //4. 声明 使用默认交换机 以队列名作为 routing key
        String queueName = "msg_queue";

        //5 发送
        Map<String, Object> headers = new HashMap<>();
        headers.put("myheader1", "111");
        headers.put("myheader2", "222");

        AMQP.BasicProperties properties = new AMQP.BasicProperties()
                .builder()
                .deliveryMode(2)            //消息持久化  1 不持久化  2 持久化
                .contentEncoding("UTF-8")
                .expiration("10000")        //消息的有效期  超过10秒没有别消费者接收后会被自动删除
                .headers(headers)
                .build();

        String msg = "test message";
        channel.basicPublish("", queueName, properties, msg.getBytes());
        System.out.println("send message:" + msg);

        //6 关闭连接
        channel.close();
        connection.close();
    }
}
