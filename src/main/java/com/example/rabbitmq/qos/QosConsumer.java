package com.example.rabbitmq.qos;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * qos  服务质量保证  服务端限流
 *      在非自动确认消息的前提下，如果一定数目的消息（通过基于consumer或者channel设置qos的值）未被确认的前提下，不消费新信息
 *
 * 方法：void basicQos(int prefetchSize, int prefetchCount, boolean global) throws IOException;
 *      prefetchSize：0  单条消息大小限制  0 代表不限制
 *      prefetchCount：一次性消费的消息数量，会告诉rabbitmq 不要同时给一个消费者推送多余N个消息
 *                  即一旦有N个消息还没有ack，则该consumer将 block 掉，直到有消息ack
 *      global：true、false、是否将上面的设置应用于channel，就是上面的是channel级别还是consumer级别
 *              当我们设置false的时候生效，设置为true的时候没有了限流功能
 * 注意：prefetchSize和global这两项，rabbitmq没有实现，暂且不研究，
 *      特别注意一点：prefetchCount在   no_ack =false 的情况下才生效，即在自动应答的情况下这两个值是不生效的
 *
 */
public class QosConsumer {
    /**
     * 首先第一步，我们既然要使用消费端限流，我们需要关闭自动 ack，将 autoAck 设置为 falsechannel.basicConsume(queueName, false, consumer);
     *
     * 第二步我们来设置具体的限流大小以及数量。channel.basicQos(0, 15, false);
     *
     * 第三步在消费者的 handleDelivery 消费方法中手动 ack，并且设置批量处理 ack 回应为 truechannel.basicAck(envelope.getDeliveryTag(), true);
     */
    public static void main(String[] args) throws IOException, TimeoutException {
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
        final Channel channel = connection.createChannel();

        //4. 声明
        String exchangeName = "test_qos_exchange";
        String queueName = "test_qos_queue";
        String routingKey = "item.#";
        channel.exchangeDeclare(exchangeName, "topic", true, false, null);
        channel.queueDeclare(queueName, true, false, false, null);

        channel.basicQos(0, 3, false);

        //一般不用代码绑定，在管理界面手动绑定
        channel.queueBind(queueName, exchangeName, routingKey);

        //5. 创建消费者并接收消息
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String message = new String(body, "UTF-8");
                System.out.println("[x] Received '" + message + "'");

                channel.basicAck(envelope.getDeliveryTag(), true);
            }
        };
        //6. 设置 Channel 消费者绑定队列
        channel.basicConsume(queueName, false, consumer);

    }
}

















