package deng.puback;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import deng.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 *  发布确认模式
 *      1.单个确认
 *      2.批量确认
 *      3.异步批量确认
 * @author shayu
 * @Date 2022/9/7
 */
public class ConfirmMessage {

    //批量发送  消息  的个数
    public static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception{
        //      1.单个确认
//        ConfirmMessage.publishMessageIndividually();
        //      2.批量确认
//        ConfirmMessage.publishMessageBatch();
        //      3.异步批量确认
        ConfirmMessage.publishMessageAsync();
    }

    //单个确认
    public  static  void publishMessageIndividually() throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //队列声明（队列名称）现在用uuid自动生产
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //启动发布确认
        channel.confirmSelect();
        //记录开始时间
        long start = System.currentTimeMillis();

        //发送消息（批量）
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes(StandardCharsets.UTF_8));

            //单个消息进行发布确认
            boolean flag = channel.waitForConfirms();
            if (flag){
                System.out.println("消息发送成功");
            }
        }

        //记录结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT +"个单个确认消息，耗时："+ (end - start) + "ms");
    }


    //批量发布确认(真·单个确认加强版)
    public static void publishMessageBatch() throws  Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //队列声明（队列名称）现在用uuid自动生产
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);

        //启动发布确认
        channel.confirmSelect();
        //记录开始时间
        long start = System.currentTimeMillis();

        //批量确认消息大小
        int batchSize = 100;
        //未确认消息个数
        //批量发送消息  批量发布确认
        for (int i = 0; i < MESSAGE_COUNT; i++) {

            String message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes(StandardCharsets.UTF_8));
            //当消息发送100条时发布确认一次
            if (i%batchSize == 0){
                //发布确认
                channel.confirmSelect();
            }

        }
        //记录结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT +"个批量确认消息，耗时："+ (end - start) + "ms");
    }


        //异步发布确认
        public static void publishMessageAsync() throws  Exception{
            Channel channel = RabbitMqUtils.getChannel();
            //队列声明（队列名称）现在用uuid自动生产
            String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, true, false, false, null);

            //启动发布确认
            channel.confirmSelect();

            /**
             * 线程安全有序的一个哈希表 用于高并发的情况下
             *  1.轻松的将序号与消息进行关联
             *  2.轻松的删除，只需要序号
             *  3.支持并发访问
             */
            ConcurrentSkipListMap<Long,String> outstandingConfirms = new ConcurrentSkipListMap<>();
            //broker异步  准备消息监听器，监听那些消息成功了，那些消息失败了
            //消息确认成功，回调函数
            /**
             * deliveryTag 消息标记
             * multiple  是否批量确认
             */
            ConfirmCallback ackCallback = (deliveryTag, multiple)->{
                if (multiple){
                    //②：删除已经确认的消息  剩下的就是未确认的消息
                    ConcurrentNavigableMap<Long, String> confirmsMap
                            = outstandingConfirms.headMap(deliveryTag);
                    confirmsMap.clear();
                }else {
                    outstandingConfirms.headMap(deliveryTag);
                }
                System.out.println("确认的消息：" + deliveryTag);

            };
            //消息确认失败，回调函数
            ConfirmCallback nackCallback = (deliveryTag, multiple)->{
                //③：未确认消息有哪些
                String message = outstandingConfirms.get(deliveryTag);
                System.out.println("未确认的消息：" + message + "标记：" + deliveryTag);
            };
            //addConfirmListener 两个参数一个是消息确认成功的操作，一个是消息确认失败的操作
            channel.addConfirmListener(ackCallback,nackCallback);


            //记录开始时间(因为异步)
            long start = System.currentTimeMillis();

            //异步的批量发送消息
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = i + "";
                channel.basicPublish("",queueName,null,message.getBytes(StandardCharsets.UTF_8));
                //①：此处记录下所有要发的消息，消息的总和
                outstandingConfirms.put(channel.getNextPublishSeqNo(),message);

            }

            //记录结束时间
            long end = System.currentTimeMillis();
            System.out.println("发布" + MESSAGE_COUNT +"个异步确认消息，耗时："+ (end - start) + "ms");
        }

}
