package deng.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生产者：发消息
 * @author shayu
 * @Date  2022/09/06
 */
public class Producer {
    // 消息的队列名称
    public static final String QUEUE_NAME = "HELLO WORLD";

    //发消息
    public static void main(String[] args) throws Exception{
        //创建连接的工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP连接Rabbit MQ的队列
        factory.setHost("127.0.0.1");
        //用户名
        factory.setUsername("guest");
        //密码
        factory.setPassword("guest");

        //创建连接
        Connection connection =  factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        /**
         * 生成一个队列(queueDeclare参数解析)
         * 1.队列名称
         * 2.队列里面的消息是否持久化 默认消息存储在内存中
         * 3.该队列是否只供一个消费者进行消费 是否进行共享 true可以多个消费者消费
         * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
         * 5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //消息
        String message="hello world";
        /**
         * 发送一个消息（basicPublish参数解析）
         * 1.发送到那个交换机
         * 2.路由的 key是哪个 本次队列的名称
         * 3.其他的参数信息
         * 4.发送消息的消息体
         */
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
        System.out.println("消息发送完毕");
    }
}
