package com.example.rabbitmq.returnlisten;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Return Listener 用于处理一-些不可路 由的消息!
 *
 * 消息生产者，通过指定一个 Exchange 和 Routingkey，把消息送达到某一个队列中去，然后我们的消费者监听队列，进行消费处理操作!
 *
 * 但是在某些情况下，如果我们在发送消息的时候，当前的 exchange 不存在或者指定的路由 key 路由不到，这个时候如果我们需要监听这种不可达的消息，就要使用 Return Listener !
 *
 * 在基础API中有一个关键的配置项:Mandatory：如果为 true，则监听器会接收到路由不可达的消息，然后进行后续处理，如果为 false，那么 broker 端自动删除该消息!
 */

public class ReturnListeningProducer {

    /**
     *  首先我们先发送三条消息，并故意将第0条消息的 routing key 设置为错误的，让他无法正常连接到消费端
     *  mandatory 设置为 true 路由不可达的消息会被监听到，不会自动删除 即
     *      channel.basicPublish(exchangeName,errRoutingKey,true,null,msg.getBytes());
     *  最后添加监听即可监听到不可路由到的消费端的消息 channel.addReturnListener(ReturnListener r)
     */
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String exchangeName = "test_return_exchange";
        String routingKey = "item.update";
        String errRoutingKey = "error.update";

        //指定消息的投递模式：confirm 确认模式
        channel.confirmSelect();

        //发送
        for (int i = 0; i < 3; i++) {
            String msg = "this is return - listening msg";
            //mandatory 设置为true 路由不可达的消息会被监听到  不会被自动删除
            if (i == 0){
                channel.basicPublish(exchangeName,errRoutingKey,true,null,msg.getBytes());
            } else {
                channel.basicPublish(exchangeName,routingKey,true,null,msg.getBytes());
            }
            System.out.println("send message : "+ msg);
        }

        //添加一个监听确认，这里就不关闭连接了，为了能够保证能收到监听消息
        channel.addConfirmListener(new ConfirmListener() {

            //返回成功的回调函数
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("success ack");
            }

            //返回失败的回调函数
            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("defeat ack");
            }
        });

        //添加一个return 监听
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("return relyCode: " + replyCode);
                System.out.println("return replyText: " + replyText);
                System.out.println("return exchange: " + exchange);
                System.out.println("return routingKey: " + routingKey);
                System.out.println("return properties: " + properties);
                System.out.println("return body: " + new String(body));
            }
        });


    }

}









