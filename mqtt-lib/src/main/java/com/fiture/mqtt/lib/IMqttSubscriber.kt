package com.fiture.mqtt.lib

/**
 *<pre>
 *  author : juneYang
 *  time   : 2020/11/23 3:07 PM
 *  desc   : MQTT接口
 *  version: 1.0
 *</pre>
 */
interface IMqttSubscriber {

    /**
     * 收到消息
     *
     * @param messageArrived  形参函数
     * @param qos 发布服务质量
     * qos为0:至多一次（会发生消息丢失或重复）
     * qos为1：至少一次（确保消息到达，但消息重复可能会发生）
     * qos为2：只有一次，确保消息到达一次。
     */
    fun onMessageArrived(messageArrived: (topic: String, message: String?, qos: Int) -> Unit)

    /**
     * 消息发送完成
     */
    fun onDeliveryComplete(deliveryComplete: (message: String?) -> Unit)

    /**
     * 服务器连接成功
     */
    fun onConnectSuccess(connectSuccess: () -> Unit)

    /**
     * 服务器连接断开
     */
    fun onConnectionLost(connectLost: (throwable: Throwable?) -> Unit)



    /**
     * 服务器连接失败
     */
    fun onConnectFailed(connectFailed: (throwable: Throwable?) -> Unit)

    /**
     * 订阅成功
     */
    fun onSubscriberSuccess(subscriberSuccess: () -> Unit)

    /**
     * 订阅失败
     */
    fun onSubscriberFailed(subscriberFailed: (exception: Throwable?) -> Unit)
}