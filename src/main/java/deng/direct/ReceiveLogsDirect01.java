package deng.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import deng.utils.RabbitMqUtils;

/**
 *
 * @author shayu
 * @Date 2022/9/14
 */
public class ReceiveLogsDirect01 {
    //交换机
    private static final java.lang.String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //生成队列  console队列名
        channel.queueDeclare("console", false, false, false, null);

        //Binding连接，key=info
        channel.queueBind("console", EXCHANGE_NAME, "info");
        channel.queueBind("console", EXCHANGE_NAME, "warning");
        System.out.println("等待接收消息........... ");
        //消息确认回调消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            java.lang.String message = new java.lang.String(delivery.getBody(), "UTF-8");
            System.out.println("ReceiveLogsDirect01控制台打印接收到的消息"+message);
        };
        //接受消息
        channel.basicConsume("console", true, deliverCallback, consumerTag -> {});
    }

}