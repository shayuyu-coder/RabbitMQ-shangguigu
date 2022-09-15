package deng.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 连接RabbitMQ工厂，创建信道的工具类
 * @author shayu
 * @Date  2022/09/06
 */
public class RabbitMqUtils {
        //得到一个连接的 channel
        public static Channel getChannel() throws Exception{
            //创建一个连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("127.0.0.1");
            factory.setUsername("guest");
            factory.setPassword("guest");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            return channel;
        }
}