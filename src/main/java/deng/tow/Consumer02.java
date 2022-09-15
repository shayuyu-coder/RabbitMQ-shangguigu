package deng.tow;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import deng.utils.RabbitMqUtils;

/**
 * 消费者02（工作线程）
 * @author shayu
 * @Date 2022/9/6
 */
public class Consumer02 {
    //队列的名称
    public static final String QUEUE_NAME = "HELLO WORLD";

    //接受消息
    public static void main(String[] args)  throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

        //消息接收回调
        DeliverCallback deliverCallback=(consumerTag, delivery)->{
            String receivedMessage = new String(delivery.getBody());
            System.out.println("接收到消息:"+receivedMessage);
        };
        //消息接受被取消
        CancelCallback cancelCallback=(consumerTag)->{
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
        };

        /**
         *     消费者消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答 true 代表自动应答 false 手动应答
         * 3.消费者成功消费的回调	(这里注意，老师讲的有瑕疵，看的弹幕)
         * 4.当一个消费者取消订阅时的回调接口;取消消费者订阅队列时
         *		除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         */
//        System.out.println("C1 消费者启动等待消费.................. ");
        System.out.println("C2 消费者启动等待消费.................. ");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);

    }
}
