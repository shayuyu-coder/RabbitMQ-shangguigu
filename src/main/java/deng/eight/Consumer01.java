package deng.eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import deng.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: DengGanWen
 * @date: 2022年09月14日 16:39
 * @ClassName: Consumer01
 * @Description:    死信 消费者01
 */

public class Consumer01 {

    //普通交换机
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机
    private static final String DEAD_EXCHANGE = "dead_exchange";
    //普通队列
    private static final String NORMAL_QUEUE = "normal_queue";
    //死信队列
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws  Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机  普通和死信
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);


        //声明队列  普通和死信   普通队列链接死信队列
        Map<String, Object> arguments = new HashMap<>();
        //过期时间 10s
//        arguments.put("x-message-ttl",100000);
        //正常队列设置死信交换机
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //设置死信链接key Routingkey=lisi
        arguments.put("x-dead-letter-routing-key","lisi");
        //设置正常队列的长度限制
//        arguments.put("x-max-length",6);


        /**
         * 生成一个队列(queueDeclare参数解析)
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true可以多个消费者消费
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         *          5.其他参数（死信交换机链接和设置）
         */
        channel.queueDeclare(NORMAL_QUEUE,false,false,true,arguments);
        channel.queueDeclare(DEAD_QUEUE,false,false,true,null);

        //绑定普通交换机和普通队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");
        //绑定死信交换机和死信队列
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");
        System.out.println("等待接受消息。。。。。");

        DeliverCallback deliverCallback=(consumerTag,delivery)->{
            String message = new String(delivery.getBody(),"UTF-8");
            if(message.equals("info5")){
                System.out.println("Consumer01接受的消息是： "+ message+"：此消息被拒");
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(),false);
            }else {
                /**
                 * 1.消息标记 tag
                 * 2.是否批量应答未应答消息
                 */
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                System.out.println("Consumer01接受的消息是：" + message);
            }
        };

        //消息被拒绝，开启手动应答 false
        channel.basicConsume(NORMAL_QUEUE,false, deliverCallback,consumerTag->{});

    }
}
