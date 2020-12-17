package com.fiture.mqtt.lib

/**
 *<pre>
 *  author : juneYang
 *  time   : 2020/11 /23
 *  desc   : 配置工具类
 *  version: 1.0
 *</pre>
 */
class MqttConfig {

    private var baseUrl = ""
    private var userName = ""
    private var password = ""

    /**
     * 订阅主题
     */
    private var subscribeTopic = ""

    /**
     * 发布主题
     */
    private var publishTopic = ""

    /**
     * mqtt 端口号
     */
    private var port = 0

    private var clientId = ""
    /**
     * 模拟发送的数据包
     *
     * @param num 次数
     */
    fun getJsonData(num: Int): String {
        return "{\"action\":\"test\", \"num\":" + num + ",\"time\": " + System.currentTimeMillis() + ",\"randomStr\":  \"随机字符串，要求长度60随机字符串，要求长度60随机字符串，要求长度60随机字符串，要求长度60随机字符串，要求长度60随机字符串，要求长度60随机字符串，要求长度60随机字符串，要求长度60随机字符串，要求长度60随机字符串，要求长度60 这里有130个字\"}"
    }

    fun create(): MqttConfig {
        return this
    }

    fun setBaseUrl(baseUrl: String): MqttConfig {
        this.baseUrl = baseUrl
        return this
    }

    fun getBaseUrl(): String {
        return "tcp://" + this.baseUrl + ":" + getPort()
    }

    fun setPort(port: Int): MqttConfig {
        this.port = port
        return this
    }

    private fun getPort(): Int {
        return this.port
    }

    fun setUserName(userName: String): MqttConfig {
        this.userName = userName
        return this
    }

    fun setPassword(password: String): MqttConfig {
        this.password = password
        return this
    }

    fun setSubscribeTopic(subscribeTopic: String): MqttConfig {
        this.subscribeTopic = subscribeTopic
        return this
    }
    fun getSubscribeTopic():String{
        return this.subscribeTopic
    }

    fun setPublishTopic(publishTopic: String): MqttConfig {
        this.publishTopic = publishTopic
        return this
    }

    fun getPublishTopic(): String {
        return this.publishTopic
    }


    fun setClientId(clientId: String): MqttConfig {
        this.clientId = clientId
        return this
    }

    fun getUserName(): String {
        return userName
    }

    //注意格式转化，防止因密码格式对，出现连接失败或断开的问题
    fun getPassword(): String {
        //服务器直接返回
        return password
    }

    fun getClientId(): String {
        return clientId
    }
}
