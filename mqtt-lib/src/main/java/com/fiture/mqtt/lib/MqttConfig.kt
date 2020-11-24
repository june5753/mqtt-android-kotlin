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
    /**
     * 服务器地址
     */
    private var baseUrl = "tcp://mqtt-cn-nif1wrghs0n.mqtt.aliyuncs.com:1883"

    private var userName = ""
    private var password = ""

    companion object {
        const val instanceId = "mqtt-cn-nif1wrghs0n"
        const val accessKey = "LTAI4GGosRrU2AwobeYgrNqK"
        const val secretKey = "w1Kr9gx6Vbtl1L5ev9py00DTZ1646f"
    }

    /**
     * TODO:是否是模拟服务端
     * A -Client :设置为false
     * B- Server: 设置为true
     */
    private var isServer = true

    // ClientId中的"GID_P2P_test_1@@@"由阿里云平台按规则配置，后面的参数自定义实现。
    private var clientId = "GID_P2P_test_1@@@Slim004"
    private var clientIdServer = "GID_P2P_test_1@@@Slim004"
    private var clientIdClient = "GID_P2P_test_1@@@XiaoMi_Client004"

    private var customerClientId = "12345"

    /**
     * 想发送给谁 目标ClientId
     */
    private var desClientId = "54321"

    /**
     * 该主题由阿里云平台按规则配置
     */
    private var parentTopic = "tesTopic"

    // p2p规则：格式topic+"/p2p/GID_xxxx@@@xxx";
    var topic_server: String = "$parentTopic/p2p/$clientIdServer"
    var topic_Client: String = "$parentTopic/p2p/$clientIdClient"

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

    fun setUserName(userName: String): MqttConfig {
        this.userName = userName
        return this
    }

    fun setPassword(password: String): MqttConfig {
        this.password = password
        return this
    }

    fun setCustomerId(customerId: String): MqttConfig {
        this.customerClientId = customerId
        return this
    }

    fun getCustomerId(): String {
        return this.customerClientId
    }

    fun setDesClientId(desClientId: String): MqttConfig {
        this.desClientId = desClientId
        return this
    }

    fun getDesClientId(): String {
        return clientIdServer + this.desClientId
    }

    fun setClientId(clientId: String): MqttConfig {
        this.clientId = this.clientId + clientId
        return this
    }

    fun getBaseUrl(): String {
        return this.baseUrl
    }

    fun getUserName(): String {
        // 参考 https://help.aliyun.com/document_detail/54225.html
        // Signature 方式
        return "Signature|$accessKey|$instanceId"
    }

    //注意格式转化，防止因密码格式对，出现连接失败或断开的问题
    fun getPassword(): CharArray {
        var pwd: CharArray = charArrayOf()
        try {
            pwd = Tool.macSignature(clientId, secretKey).toCharArray()
        } catch (e: Exception) {
            MqttLoger.e("exception setPassword：$e")
        }
        return pwd
    }

    fun getServer(): Boolean {
        return isServer
    }

    fun setServer(isServer: Boolean): MqttConfig {
        this.isServer = isServer
        return this
    }

    fun getClientId(): String {
        clientId = if (isServer) {
            clientIdServer
        } else {
            clientIdClient
        }
        return clientId
    }
}
