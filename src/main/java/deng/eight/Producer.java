package deng.eight;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import deng.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author: shayu
 * @date: 2022年09月15日 9:51
 * @ClassName: Producer
 * @Description:    死信队列生产者代码
 */

public class Producer {
    //普通交换机
    private static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        //死信消息  设置TTL时间 10s=10000ms
//        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 0; i < 11; i++) {
            String message = "info" + i;
            System.out.println("发送消息：info"+i);
            channel.basicPublish(NORMAL_EXCHANGE,"zhangsan",null,message.getBytes(StandardCharsets.UTF_8));
        }
        
    }
}
