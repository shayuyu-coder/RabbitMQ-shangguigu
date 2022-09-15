package deng.direct;

import com.rabbitmq.client.Channel;
import deng.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author: shayu
 * @date: 2022年09月14日 13:29
 * @ClassName: DirectLogs
 * @Description:    生产者
 */

public class DirectLogs {
    //交换机名称
    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * 声明一个 exchange
         * 1.exchange 的名称
         * 2.exchange的类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        Scanner sc = new Scanner(System.in);
        System.out.println("请输入信息");
        while (sc.hasNext()) {
            String message = sc.nextLine();
            //发送，根据kek链接来确定  key:error/warning/info
            channel.basicPublish(EXCHANGE_NAME, "warning", null, message.getBytes("UTF-8"));
            System.out.println("生产者发出消息" + message);
        }
    }
}
