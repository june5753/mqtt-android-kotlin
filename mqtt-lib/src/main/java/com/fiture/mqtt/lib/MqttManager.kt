package com.fiture.mqtt.lib

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

/**
 *<pre>
 *  author : juneYang
 *  time   : 2020/11/23
 *  desc   : 消息队列管理器
 * 使用流程：
 * 1，配置MqttConfig，设置账号和密码，MqttConfig().create()。
 * 2，初始化Mqtt客户端，MqttManager.getInstance().init(activity, MqttConfig().create())
 * 3，连接Mqtt客户端，MqttManager.getInstance().connect()，建议在init方法后调用。
 * 4，订阅Topic，MqttManager.getInstance().subscribe(topic, subscriber)，并在subscriber中处理消息的回调。
 * 5，发布消息，MqttManager.getInstance().publishMessage(topic,content)。
 * 6，退订Topic，MqttManager.getInstance().unsubscribe(topic)，建议在页面消失时调用。
 * 7，关闭Mqtt，MqttManager.getInstance().close()，建议在MainActivity的onDestroy中调用。
 *  version: 1.0
 *</pre>
 */
class MqttManager {

    private var mConfig: MqttConfig? = null
    private var mMqttClient: MqttAndroidClient? = null
    private val mSubscribers = LinkedHashMap<String, MqttSubscriber>()

    /**
     *
     * 初始化MQtt客户端，建议在MainActivity的onCreate中调用
     */
    fun init(
        context: Context,
        config: MqttConfig
    ) {
        mConfig = config
        mMqttClient = MqttAndroidClient(context, config.getBaseUrl(), config.getClientId())

        mMqttClient?.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String) {
                if (reconnect) {
                    MqttLoger.e("----> mqtt reconnect complete, serverUrl = $serverURI")
                } else {
                    MqttLoger.e("----> mqtt connect complete, serverUrl = $serverURI")
                }
            }

            override fun connectionLost(cause: Throwable?) {
                mSubscribers.entries.forEach {
                    it.value.connectLost?.invoke(cause)
                }
                MqttLoger.e("----> mqtt connect lost, cause = ${cause?.message}")
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                val subscriber = mSubscribers[topic]
                subscriber?.messageArrived?.invoke(topic, String(message.payload), message.qos)
                MqttLoger.e("----> mqtt message arrived, topic = $topic, message = ${String(message.payload)}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                mSubscribers.entries.forEach {
                    it.value.deliveryComplete?.invoke(token.message.toString())
                }
                MqttLoger.e("----> mqtt delivery complete, token = ${token.message}")
            }
        })
    }

    fun getMqttClient(): MqttAndroidClient? {
        return mMqttClient
    }

    /**
     * 连接服务器
     * @param subscriber 表示当前方法的回调，并不会作用到全局
     */
    fun connect(subscriber: (MqttSubscriber.() -> Unit)? = null) {
        if (mMqttClient == null) {
            MqttLoger.e("----> mqtt connect failed, please init mqtt first.")
            return
        }
        val callback = MqttSubscriber()
        subscriber?.let { callback.it() }
        try {
            mMqttClient?.connect(generateConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    MqttLoger.e("connect success")
                    callback.connectSuccess?.invoke()
                    mMqttClient?.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    MqttLoger.e("connect fail: ${exception.toString()}")
                    callback.connectFailed?.invoke(exception)
                }
            })
        } catch (exception: MqttException) {
            MqttLoger.e("connect fail: ${exception}")
        }
    }

    /**
     * 订阅一个话题
     */
    fun subscribe(topic: String, subscriber: (MqttSubscriber.() -> Unit)? = null) {
        if (mMqttClient == null) {
            MqttLoger.e("----> mqtt subscribe failed, please init mqtt first.")
            return
        }
        if (isConnected()) {
            performSubscribe(topic, subscriber)
        } else {
            // 如果没有连接，就先去连接
            connect {
                onConnectSuccess { performSubscribe(topic, subscriber) }

                onConnectionLost {
                    // do nothing
                }
            }
        }
    }

    /**
     * 退订一个topic
     */
    fun unsubscribe(topic: String) {
        mSubscribers.remove(topic)
        mMqttClient?.unsubscribe(topic, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                MqttLoger.e("----> mqtt unsubscribe success, topic = $topic")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                MqttLoger.e("----> mqtt unsubscribe failed, exception = ${exception?.message}")
            }
        })
    }

    /**
     * 订阅实现
     */
    private fun performSubscribe(topic: String, subscriber: (MqttSubscriber.() -> Unit)? = null) {
        // 判断是否已经订阅
        if (mSubscribers.containsKey(topic)) {
            MqttLoger.e("----> mqtt subscribe contains key,had Subscribers, topic = $topic,return")
            return
        }

        val callback = MqttSubscriber()
        subscriber?.let { callback.it() }
        mSubscribers[topic] = callback
        try {
            mMqttClient?.subscribe(topic, QOS_LEVEL, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    callback.subscriberSuccess?.invoke()
                    MqttLoger.e("----> mqtt subscribe success, topic = $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {
                    callback.subscriberFailed?.invoke(exception)
                    MqttLoger.e("----> mqtt subscribe failed, exception = ${exception?.message}")
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    /**
     * 生成默认的连接配置
     */
    private val generateConnectOptions: MqttConnectOptions by lazy {
        MqttConnectOptions().apply {
            // 设置超时时间 单位为秒
            connectionTimeout = 30
            //设置会话心跳时间 单位为秒 服务器会每隔15秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            keepAliveInterval = 15
            isAutomaticReconnect = false
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            isCleanSession = true
            userName = mConfig?.getUserName()
            password = mConfig?.getPassword()?.toCharArray()
        }
    }

    private val disconnectedBufferOptions: DisconnectedBufferOptions by lazy {
        DisconnectedBufferOptions().apply {
            isBufferEnabled = true
            bufferSize = 100
            isPersistBuffer = false
            isDeleteOldestMessages = false
        }
    }

    /**
     * 发布消息
     */
    fun publishMessage(topic: String, content: String) {
        if (mMqttClient == null) {
            MqttLoger.e("----> mqtt publish message failed, please init mqtt first.")
            return
        }
        if (isConnected()) {
            performPublishMessage(topic, content)
        } else {
            connect {
                onConnectSuccess {
                    performPublishMessage(topic, content)
                }
                onConnectFailed {
                    //do nothing here
                }
            }
        }
    }

    private fun performPublishMessage(topic: String, content: String) {
        try {
            val message = MqttMessage()
            message.payload = content.toByteArray()
            message.qos = QOS_LEVEL
            mMqttClient?.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    MqttLoger.d("publish success，topic:$topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    MqttLoger.d("publish fail:" + exception.toString())
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * 主动断开连接，不会自动重连
     */
    fun disconnect(subscriber: (MqttSubscriber.() -> Unit)) {

        val callback = MqttSubscriber()
        subscriber.let { callback.it() }

        try {
            if (!isConnected()) {
                MqttLoger.e("当前设备已断开连接")
                callback.connectFailed?.invoke(Throwable("当前设备已断开连接"))
                return
            }
            mMqttClient?.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
            callback.connectFailed?.invoke(e)
        }
    }

    /**
     * 判断连接是否断开
     */
    fun isConnected(): Boolean {
        try {
            return mMqttClient?.isConnected ?: false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 关闭MQTT客户端，建议在MainActivity的onDestroy中调用
     */
    fun close() {
        try {
            mMqttClient?.close()
            mMqttClient?.disconnect()
            mMqttClient?.unregisterResources()
            clear()
            MqttLoger.e("----> mqtt close success.")
        } catch (e: Exception) {
            MqttLoger.e("----> mqtt close failed.")
            e.printStackTrace()
        }
    }

    fun getServerUrl(): String? {
        return mConfig?.getBaseUrl()
    }

    fun getClientId(): String? {
        return mConfig?.getClientId()
    }


    private fun getSubscribers(): LinkedHashMap<String, MqttSubscriber> {
        return mSubscribers
    }

    /**
     * 清空订阅事件
     */
    fun clear() {
        getSubscribers().clear()
    }

    fun getPublishTopic(): String? {
        return mConfig?.getPublishTopic()
    }

    companion object {
        fun getInstance(): MqttManager {
            return Holder.instance
        }

        /**
         * qos为0:至多一次（会发生消息丢失或重复）
         * qos为1：至少一次（确保消息到达，但消息重复可能会发生）
         * qos为2：只有一次，确保消息到达一次。
         */
        private const val QOS_LEVEL = 0
    }

    object Holder {
        val instance = MqttManager()
    }
}
