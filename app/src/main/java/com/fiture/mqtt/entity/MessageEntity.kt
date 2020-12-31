package com.fiture.mqtt.entity

import java.io.Serializable

/**
 *<pre>
 *  author : juneYang
 *  time   : 2020/12/15 2:51 PM
 *  desc   : mqtt 发送和接收的消息实体类
 *  version: 1.0
 *</pre>
 */
class MessageEntity : Serializable {
    /**
     * 版本号
     */
    var v = 1

    /**
     * 会话ID
     */
    var sessionId = "JungYang"

    /**
     * 消息类型
     * HI - 首次建立连接时
     * MSG、
     * ACK、
     * HB 在线状态续期
     * CLOSE 清理Session，下线
     * RESET 用于Machine反馈消息发送方连接过期，重建连接（极端场景出现，发送方的sessionId过期或缓存被清空）
     * NOTFOUND destType + destId兑换目标的sessionId失败时 即消息接收方不在线或断网。及时反馈给消息发送方"提示设备不在线"
     */
    var msgType = ""

    /**
     * 客户端原消息ID
     */
    var msgId = "testID"

    /**
     * 客户原目标类型
     */
    var destType = "dddd"

    /**
     * 客户原目标ID
     */
    var destId = "xxx"

    var tag = "3333"

    //业务数据（魔镜指令协议)json数据 透传给上层的业务数据
    var payload = "ddddd"

    var ack = 1

    /**
     * 时间戳
     */
    var ts = 1L

    /**
     * 参考签名公式：
     * 签名公式：md5(v + sessionId + msgType + msgId + destType + destId +
     * tag + payload + ack + ts + 服务端指派secret)，若值为空则忽略，该例子tag、ack的value为null
     */
    var sign = "signtest"
}