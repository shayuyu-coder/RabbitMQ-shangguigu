package deng.autoAnswer;

import com.rabbitmq.client.*;
import deng.utils.RabbitMqUtils;
import deng.utils.SleepUtils;

import java.nio.charset.StandardCharsets;

/**
 * 消费者01（工作线程）
 * 手动应答时消息是不会丢失的，丢失会重新放回队列中消费
 * @author shayu
 * @Date 2022/9/6
 */
public class Consumer01 {
    //队列的名称
    public static final String ACK_QUEUE_NAME = "ACK-AUTO-ANSWER";

    //接受消息
    public static void main(String[] args)  throws Exception{
//        Channel channel = RabbitMqUtils.getChannel();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //手动应答,消息接收,回调
        DeliverCallback deliverCallback=(consumerTag, delivery)->{

            //沉睡一秒，模拟业务处理
            SleepUtils.sleep(1);
            //沉睡十秒，模拟业务处理
//            SleepUtils.sleep(10);
            System.out.println("接收到消息:"+ new String(delivery.getBody(), StandardCharsets.UTF_8));
            //手动应答
            /**
             * 1.消息标记 tag
             * 2.是否批量应答未应答消息
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
        };
        //消息接受被取消
        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
        };

        //设置不公平分发
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);

        /**
         *     采用手动应答  消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者成功消费的回调	(这里注意，老师讲的有瑕疵，看的弹幕)
         * 4.当一个消费者取消订阅时的回调接口;取消消费者订阅队列时
         *		除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         */
        System.out.println("C1 消费者启动等待消费（快速版1S）.................. ");
//        System.out.println("C2 消费者启动等待消费（慢速版10S）.................. ");
        channel.basicConsume(ACK_QUEUE_NAME,false,deliverCallback,cancelCallback);

    }
}
