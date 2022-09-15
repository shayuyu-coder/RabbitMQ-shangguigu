package deng.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import deng.utils.RabbitMqUtils;

/**
 *      （消费者）
 * @author shayu
 * @Date 2022/9/9
 */
public class ReceiveLogs01 {
    //交换机名称
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        /**
        * 生成一个临时的队列 队列的名称是随机的
        * 当消费者断开和该队列的连接时 队列自动删除
        */
        String queueName = channel.queueDeclare().getQueue();

        //把该临时队列绑定我们的 exchange 其中 routingkey(也称之为 binding key)为空字符串
        // 空字符串  fanout类型下是不会判断key的   会把消息发送给所有人
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println("等待接收消息,把接收到的消息打印在屏幕........... ");

        //消息确认回调消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("ReceiveLogs01控制台打印接收到的消息"+message);
        };

        //接受消息
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}