package deng.topics;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import deng.utils.RabbitMqUtils;

/**
 * @author: shayu
 * @date: 2022年09月14日 14:29
 * @ClassName: ReceiveLogsTopic01
 * @Description:    消费2
 */

public class ReceiveLogsTopic01 {

    //交换机
    private static final java.lang.String EXCHANGE_NAME = "topics_logs";

    public static void main(String[] args)throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //生成队列  队列名 Q1
        channel.queueDeclare("Q1", false, false, false, null);
        //Binding连接，key通配符
        channel.queueBind("Q1", EXCHANGE_NAME, "*.orange.*");
        System.out.println("等待接收消息........... ");
        //消息确认回调消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            java.lang.String message = new java.lang.String(delivery.getBody(), "UTF-8");
            System.out.println("接受队列：Q1，绑定键：" + delivery.getEnvelope().getRoutingKey()+"；消息为："+message);
        };
        //接受消息
        channel.basicConsume("Q1", true, deliverCallback, consumerTag -> {});
    }
}
