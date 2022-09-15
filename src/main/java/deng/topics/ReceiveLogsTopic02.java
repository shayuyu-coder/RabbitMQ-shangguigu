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

public class ReceiveLogsTopic02 {

    //交换机
    private static final String EXCHANGE_NAME = "topics_logs";

    public static void main(String[] args)throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //生成队列  队列名 Q2
        channel.queueDeclare("Q2", false, false, false, null);
        //Binding连接，key通配符
        channel.queueBind("Q2", EXCHANGE_NAME, "*.*.rabbit");
        channel.queueBind("Q2", EXCHANGE_NAME, "lazy.#");
        System.out.println("等待接收消息........... ");
        //消息确认回调消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("接受队列：Q2，绑定键：" + delivery.getEnvelope().getRoutingKey()+"；消息为："+message);
        };
        //接受消息
        channel.basicConsume("Q2", true, deliverCallback, consumerTag -> {});
    }
}
