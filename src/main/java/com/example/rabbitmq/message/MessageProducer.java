package com.example.rabbitmq.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Message#
 * 消息，服务器和应用程序之间传送的数据，由 Properties 和 Body 组成。Properties 可以对消息进行修饰，比如消息的优先级、延迟等高级特性;
 * ，Body 则就 是消息体内容
 *
 * properties 中我们可以设置消息过期时间以及是否持久化等，也可以传入自定义的map属性，这些在消费端也都可以获取到。
 */

public class MessageProducer {
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
        headers.put("myheader1","111");
        headers.put("myheader2","222");

        AMQP.BasicProperties properties = new AMQP.BasicProperties()
                .builder()
                .deliveryMode(2)            //消息持久化  1 不持久化  2 持久化
                .contentEncoding("UTF-8")   //消息内容的编码格式
                .expiration("10000")        //消息的有效期  超过10秒没有别消费者接收后会被自动删除
                .headers(headers)           //自定义的一些属性
                .build();

        String msg = "test message";
        channel.basicPublish("",queueName,properties,msg.getBytes());
        System.out.println( "send message:" + msg );

        //6 关闭连接
        channel.close();
        connection.close();

    }
}

















