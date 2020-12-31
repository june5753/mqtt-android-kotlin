package com.fiture.mqtt.entity

import java.io.Serializable

/**
 *<pre>
 *  author : juneYang
 *  time   : 2020/12/14 2:51 PM
 *  desc   : mqtt 连接时配置信息实体类
 *  version: 1.0
 *</pre>
 */
class ConnectEntity : Serializable {

    /**
     * 会话id
     */
    var sessionId = ""

    /**
     * secret用于MD5签名校验
     */
    var secret = ""

    /**
     * MQTT 连接信息
     */
    var mqttPassport = MqttPassportEntity()

    /**
     * Session 通讯节点, MQTT Server Node
     */
    var node = ""

    /**
     * 版本
     */
    var version = 1

    /**
     * 业务类型[MALL_APPLET:电商小程序, SLIM:魔镜] 允许值: MALL_APPLET, SLIM
     */
    var businessType = ""

    /**
     * 业务id
     */
    var businessId = ""
}

class MqttPassportEntity : Serializable {
    /**
     *MQTT Server连接地址
     */
    var host = ""

    /**
     * MQTT 端口
     */
    var mqttPort = 0

    /**
     *WebSocket 端口(小程序端用)
     */
    var webSocketPort = 0

    /**
     * MQTT 连接账号
     */
    var username = ""

    /**
     * MQTT 通讯密码
     */
    var password = ""

    /**
     * MQTT ClientId，与授权相关 需要保证唯一性
     */
    var clientId = ""

    /**
     * 客户端专属 Topic 订阅主题
     */
    var subscribeTopic = ""

    /**
     * 客户端推送Topic 发布主题
     */
    var publishTopic = ""
}