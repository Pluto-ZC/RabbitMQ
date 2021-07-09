# RabbitMQ

参考
https://www.cnblogs.com/haixiang/p/10826710.html

AMQP 协议#
    AMQP: Advanced Message Queuing Protocol 高级消息队列协议
    AMQP定义:是具有现代特征的二进制协议。是一个提供统一消息服务的应用层标准高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。
    Erlang语言最初在于交换机领域的架构模式，这样使得RabbitMQ在Broker之间进行数据交互的性能是非常优秀的
    Erlang的优点: Erlang有着和原生Socket一样的延迟。
    RabbitMQ是一个开源的消息代理和队列服务器，用来通过普通协议在完全不同的应用之间共享数据, RabbitMQ是使用Erlang语言来编写的，并且RabbitMQ是基于AMQP协议的。

Message#
    消息，服务器和应用程序之间传送的数据，由 Properties 和 Body 组成。Properties 可以对消息进行修饰，比如消息的优先级、延迟等高级特性;，Body 则就 是消息体内容。
    properties 中我们可以设置消息过期时间以及是否持久化等，也可以传入自定义的map属性，这些在消费端也都可以获取到。

Exchange#
1. 简介#
   Exchange 就是交换机，接收消息，根据路由键转发消息到绑定的队列。有很多的 Message 进入到 Exchange 中，Exchange 根据 Routing key 将 Message 分发到不同的 Queue 中。

2. 类型#
   RabbitMQ 中的 Exchange 有多种类型，类型不同，Message 的分发机制不同，如下：

    fanout：广播模式。这种类型的 Exchange 会将 Message 分发到绑定到该 Exchange 的所有 Queue。
    direct：这种类型的 Exchange 会根据 Routing key（精确匹配，将Message分发到指定的Queue。
    Topic：这种类型的 Exchange 会根据 Routing key（模糊匹配，将Message分发到指定的Queue。
    headers: 主题交换机有点相似，但是不同于主题交换机的路由是基于路由键，头交换机的路由值基于消息的header数据。 主题交换机路由键只有是字符串,而头交换机可以是整型和哈希值 .

3. 属性#

      Exchange.DeclareOk exchangeDeclare(
                String exchange,
                String type,boolean durable,
                boolean autoDelete,
                boolean internal,
                Map<String, Object> arguments
            ) throws IOException;

    Name: 交换机名称
    Type: 交换机类型direct、topic、 fanout、 headers
    Durability: 是否需要持久化，true为持久化
    Auto Delete: 当最后一个绑定到Exchange. 上的队列删除后，自动删除该Exchange
    Internal: 当前Exchange是否用于RabbitMQ内部使用，默认为False
    Arguments: 扩展参数，用于扩展AMQP协议自制定化使用\
   
Direct 模式#
   所有发送到 Direct Exchange 的消息被转发到 RouteKey 中指定的 Queue。
   Direct 模式可以使用 RabbitMQ 自带的 Exchange: default Exchange，所以不需要将 Exchange 进行任何绑定(binding)操作。
   消息传递时，RouteKey 必须完全匹配才会被队列接收，否则该消息会被抛弃，

Topic 模式#
    可以使用通配符进行模糊匹配
    符号'#" 匹配一个或多个词
    符号"*”匹配不多不少一个词
    例如:
    'log.#"能够匹配到'log.info.oa"
    "log.*"只会匹配到"log.erro“

Fanout 模式#
    不处理路由键，只需要简单的将队列绑定到交换机上发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。
    Fanout交换机转发消息是最快的。


