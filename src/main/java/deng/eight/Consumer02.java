package deng.eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import deng.utils.RabbitMqUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: DengGanWen
 * @date: 2022年09月14日 16:39
 * @ClassName: Consumer02
 * @Description:    死信 消费者02
 */

public class Consumer02 {


    //死信交换机
    private static final String DEAD_EXCHANGE = "dead_exchange";
    //死信队列
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws  Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机  普通和死信
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        /**
         * 生成一个队列(queueDeclare参数解析)
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true可以多个消费者消费
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         *          5.其他参数（死信交换机链接和设置）
         */
        channel.queueDeclare(DEAD_QUEUE,false,false,true,null);

        //绑定死信交换机和死信队列
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
        System.out.println("等待接受消息。。。。。");

        DeliverCallback deliverCallback=(consumerTag,delivery)->{
            String message= new String(delivery.getBody());
            System.out.println("Consumer02接受的消息是：" + message);
        };

        channel.basicConsume(DEAD_QUEUE,true, deliverCallback,consumerTag->{});

    }
}
