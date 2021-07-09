package com.example.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产端 confirm消息确认机制
 *  第一步：在channel上开启确认模式 channel.confirmSelect()
 *  第二步：在channel上添加监听:channel.addConfirmListener(ConfirmListener listener)
 *      监听成功和失败的返回结果，根据具体的结果对消息进行重新发送、记录日志等后续操作
 *
 *  注意：
 *      我们采用的是异步 confirm 模式：提供一个回调方法，服务端 confirm 了一条或者多条消息后 Client 端会回调这个方法。
 *      除此之外还有单条同步 confirm 模式、批量同步 confirm 模式，由于现实场景中很少使用我们在此不做介绍，
 *      如有兴趣直接参考官方文档。
 *
 *      我们运行生产端会发现每次运行结果都不一样,会有多种情况出现，因为 Broker 会进行优化，有时会批量一次性 confirm ，
 *      有时会分开几条 confirm。
 */

public class ConfirmProducer {
    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "test_confirm_exchange";
        String routingKey = "item.update";

        //指定消息的投递模式：confirm确认模式
        channel.confirmSelect();

        //发送
        final long start = System.currentTimeMillis();
        for (int i = 0; i < 5 ; i++) {
            String msg = "this is confirm msg ";
            channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
            System.out.println("Send message : " + msg);
        }

        //添加一个确认监听，这里不关闭连接了，确保能收到监听消息
        channel.addConfirmListener(new ConfirmListener() {
            /**
             * 返回成功回调的函数
             */
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("succuss ack");
                System.out.println(multiple);
                System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
            }

            /**
             * 返回失败回调的函数
             */
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.printf("defeat ack");
                System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
            }
        });

    }
}















