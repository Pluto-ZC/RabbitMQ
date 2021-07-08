package com.example.rabbitmq.easy;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 简单队列模式
 * 这种简单队列的模式，系统会为每个队列隐式地绑定一个默认交换机，交换机名称为" (AMQP default)"，类型为直连 direct，
 * 当你手动创建一个队列时，系统会自动将这个队列绑定到一个名称为空的 Direct 类型的交换机上，绑定的路由键 routing key 与队列名称相同，
 * 相当于channel.queueBind(queue:"QUEUE_NAME", exchange:"(AMQP default)“, routingKey:"QUEUE_NAME");
 * 虽然实例没有显式声明交换机，但是当路由键和队列名称一样时，就会将消息发送到这个默认的交换机中。这种方式比较简单，
 * 但是无法满足复杂的业务需求，所以通常在生产环境中很少使用这种方式。
 */

public class MyConsumer {

    private static final String QUEUE_NAME = "ITEM_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        //1 创建一个 ConnetionFactory 并进行设置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");

        //2 通过连接工厂来创建连接
        Connection connection = factory.newConnection();

        //3 通过连接来创建channel
        Channel channel = connection.createChannel();

        //4 声明一个队列
        /**
         * 参数
         *      name：队列名字
         *      durable：是否持久化，队列的声明默认是存放到内存中，如果rabbitmq重启会丢失，如果想要重启之后还存在就要使队列持久化
         *              保存到erlang自带的Mnesia数据库汇中，当rabbitmq重启之后会读取该数据库
         *      exclusive：是否排外  两个作用：1、当连接关闭connection。close（）该队列是否自动删除  2、该队列是否是私有的private
         *              如果不是排外的。可以使两个消费者都访问同一个队列，如果是排外的，会对当前队列加锁，其他channel是不能访问的
         *              一般true的话用于一个队列只能有一个消费者的场景
         *      autoDelete：队列中的数据消费完成之后是否自动删除 可以通过rabbitmq management 查看某个队列的消费者数量
         *              当consumer=0 时，队列自动删除
         *      args：相关参数  一般为null
         *
         */
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //5 创建消费者并接收消息
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };

        //6 设置channel 消费者绑定队列
        /**
         * autoAck：接收消息后是否自动恢复ack确认
         *      true：自动应答，即消费者获取到消息，该消息就从队列汇总删除
         *      false：手动应答，当消费者从队列中取出消息后，需要程序员手动应答，如果没有应答，该消息还会被放进队列中
         */
        channel.basicConsume(QUEUE_NAME,true,consumer);
    }

}












