package deng.autoAnswer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import deng.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 *  自动应答
 *  生产者：发送消息，手动应答时消息是不会丢失的，丢失会重新放回队列中消费
 * @author shayu
 * @Date 2022/9/7
 */
public class Producer01 {
    //队列的名称
    public static final String ACK_QUEUE_NAME = "ACK-AUTO-ANSWER";

    //发送大量消息
    public static void main(String[] args) throws  Exception{
        //连接
        Channel channel = RabbitMqUtils.getChannel();

        //开启发布确认
        channel.confirmSelect();

        //队列声明  队列持久化已开启
        /**
         * 生成一个队列(queueDeclare参数解析)
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true可以多个消费者消费
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
        channel.queueDeclare(ACK_QUEUE_NAME,true,false,false,null);

        //从控制台中接受信息,并发送
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            /**
             * 发送一个消息（basicPublish参数解析）
             * 1.发送到那个交换机
             * 2.路由的 key是哪个 本次队列的名称
             * 3.其他的参数信息，消息持久化已开启
             * 4.发送消息的消息体
             */
            channel.basicPublish("",ACK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println("发送消息完成：" + message);
        }

    }
}
