package deng.one;

import com.rabbitmq.client.*;

/**
 * 消费者:接收消息
 * @author shayu
 * @Date 2022/9/6
 */
public class Consumer {
    //队列的名称
    public static final String QUEUE_NAME = "HELLO WORLD";

    //接收消息
    public static void main(String[] args) throws Exception {
        //创建工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");   //端口5672 是rabbitMQ,端口15672是RabbitMQ可视化页面的端口
        factory.setUsername("guest");
        factory.setPassword("guest");
        //创建连接，创建信道
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        System.out.println("等待接收消息.........");


        //推送的消息如何进行消费的接口回调  (简单来说就是消息接受到了，在这里可以进行操作，并要回调告诉生产者)
        DeliverCallback deliverCallback=(consumerTag,delivery)->{
            String message= new String(delivery.getBody());
            System.out.println(message);
        };

        //取消消费的一个回调接口 如在消费的时候队列被删除掉了
        CancelCallback cancelCallback=(consumerTag)->{
        System.out.println("消息消费被中断");
    };
        /**
         * 消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者成功消费的回调     (这里注意，老师讲的有瑕疵)
         * 4.当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}

