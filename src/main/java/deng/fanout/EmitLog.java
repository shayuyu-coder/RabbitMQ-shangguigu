package deng.fanout;

import com.rabbitmq.client.Channel;
import deng.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 *  (生产者)
 *
 * @author shayu
 * @Date 2022/9/9
 */
public class EmitLog {
    //交换机名称
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /**
        * 声明一个 exchange
        * 1.exchange 的名称
        * 2.exchange的类型
        */
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入信息");
        while (sc.hasNext()) {
            String message = sc.nextLine();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println("生产者发出消息" + message);
             }
    }
}