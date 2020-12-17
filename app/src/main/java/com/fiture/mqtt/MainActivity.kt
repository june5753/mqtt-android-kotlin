package com.fiture.mqtt

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fiture.mqtt.entity.MqttPassportEntity
import com.fiture.mqtt.lib.MqttConfig
import com.fiture.mqtt.lib.MqttManager
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 测试界面
 */
class MainActivity : AppCompatActivity() {

    private var mMqttConfig: MqttConfig? = null

    /**
     * 测试消息数量（以收到消息次数为准）
     */
    private val MAX = 10

    /**
     * 当前的发送消息次数
     */
    private var sendMsgNum = 0

    /**
     * 当前的收到消息次数
     */
    private var receiverMsgNum = 0

    /**
     * 记录收发数据时间戳
     */
    private var startTime = 0L
    private var endTime = 0L

    /**
     * 记录建立联接时间戳
     */
    private var connectStartTime = 0L
    private var connectEndTime = 0L

    /**
     * 记录当次测试周期中的最大最小值
     */
    private var maxTime = Long.MIN_VALUE
    private var minTime = Long.MAX_VALUE

    /**
     * 累计时间差总和（ms）
     */
    private var totalAverTime = 0L


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        MqttManager.getInstance().close()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        //配置信息一般由服务器返回
        val entity = MqttPassportEntity()
        entity.url = "post-cn-6ja1y8mg80d.mqtt.aliyuncs.com"
        entity.port = 1883
        entity.clientId = "GID-Session@@@f0012805-b951-4c08-ab3d-deb825758fc1"
        entity.username = "Signature|LTAI4G9FhcothBFPxkhCTDaB|post-cn-6ja1y8mg80d"
        entity.password = "P1/4tRpmHa9SY5Tg5uvip49+WlI="
        entity.publishTopic = "machine/msg"
        entity.subscribeTopic = "session/f0012805-b951-4c08-ab3d-deb825758fc1"
        // 初始化
        initMqtt(entity)
    }

    @SuppressLint("SetTextI18n")
    private fun initMqtt(entity: MqttPassportEntity) {
        mMqttConfig = MqttConfig()
            .setBaseUrl(entity.url)
            .setPort(entity.port)
            .setClientId(entity.clientId)
            .setUserName(entity.username)
            .setPassword(entity.password)
            .setSubscribeTopic(entity.subscribeTopic)
            .setPublishTopic(entity.publishTopic)
            .create()
        MqttManager.getInstance().init(this, mMqttConfig!!)
        showTips("服务器地址：${MqttManager.getInstance().getServerUrl()}")
        tvClientId.text = "当前Client id: " + MqttManager.getInstance().getClientId()
    }

    @SuppressLint("SetTextI18n")
    private fun setListener() {
        // 连接服务端
        btnConnect.setOnClickListener {
            connectStartTime = System.currentTimeMillis()
            showTips("正在连接中...")
            MqttManager.getInstance().connect {
                onConnectSuccess {
                    connectEndTime = System.currentTimeMillis()
                    tvConnectTime.text = "建立联连耗时（ms）" + (connectEndTime - connectStartTime)
                    showTips("服务器连接成功")
                    toast("服务器连接成功")

                }
                onConnectFailed {
                    showTips("服务器连接失败：${it?.message}")
                    toast("服务器连接失败：${it?.message}")
                }
            }
        }
        btnSubscribe.setOnClickListener {
            showTips("正在订阅中...")
            val subTopic = MqttManager.getInstance().getPublishTopic()
            MqttManager.getInstance().subscribe(subTopic!!) {

                onSubscriberSuccess {
                    showTips("订阅成功")
                }

                onSubscriberFailed {
                    showTips("订阅失败：${it?.message}")
                }

                onMessageArrived { topic, message, qos ->
                    handleArrivedMessage(message.toString())
                }

                onDeliveryComplete {
                    tvPushMessageState.text = "消息推送完毕,已发送的消息：$it"
                }

                onConnectionLost {
                    showTips("连接已断开$it")
                }
            }
        }

        // 推送消息
        btnPublish.setOnClickListener {
            mMqttConfig?.let {
                reset()
                sendMsgNum++
                //p2p相互发送消息
                val message = "消息来自：" + it.getClientId() + it.getJsonData(sendMsgNum)

                //发送消息时对方的订阅的主题，即当前发布的主题一一对应
                MqttManager.getInstance()
                    .publishMessage(MqttManager.getInstance().getPublishTopic()!!, message)
            }
        }

        // 断开连接
        btnClose.setOnClickListener {
            showTips("正在断开中...")
            MqttManager.getInstance().disconnect() {
                onConnectFailed {
                    showTips(it?.message)
                    toast(it?.message)
                }

            }
        }

        btnClear.setOnClickListener {
            reset()
            showTips("已清空日志")
            tvPushMessageState.text = "已发送的消息:"
            tvReceiveMsgNum.text = "收到消息次数：$receiverMsgNum"
            tvSendMsgNum.text = "发送消息次数：$sendMsgNum"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleArrivedMessage(msg: String) {
        receiverMsgNum++
        tvReceiveMsgNum.text = "收到消息次数：$receiverMsgNum"
        tvMessage.text = "收到消息：$msg"
        tvMessageLength.text = "收到消息的长度：" + msg.length
        //时间差计算方式：以先后两次到达的时间差作为当前一个完整的收发周期。
        if (receiverMsgNum % 2 == 1) {
            startTime = System.currentTimeMillis()
        } else {
            endTime = System.currentTimeMillis()
            //当前的时间差
            val currentAverTime = endTime - startTime
            totalAverTime += currentAverTime
            //记录当前最小值和最大值
            minTime = minTime.coerceAtMost(currentAverTime)
            maxTime = maxTime.coerceAtLeast(currentAverTime)

            tvEveryTime.text = "实时收发耗时（ms）$currentAverTime"
            tvMinTime.text = "当前最小值（ms）:$minTime"
            tvMaxTime.text = "当前最大值（ms）:$maxTime"
        }
        if (receiverMsgNum == MAX) {
            val averTime: Long = totalAverTime / (MAX / 2)
            tvAverTime.text = "100次完成后平均一次收发耗时（ms):$averTime"
            reset()
            return
        }
        mMqttConfig?.let {
            //A收到消息后 再向B发送出去
            sendMsgNum++
            val message = "消息来自：" + it.getClientId() + it.getJsonData(sendMsgNum)
            MqttManager.getInstance()
                .publishMessage(MqttManager.getInstance().getPublishTopic()!!, message)
            tvSendMsgNum.text = "发送消息次数：$sendMsgNum"
        }
    }

    private fun showTips(msg: String?) {
        tvMessage?.text = msg
    }

    private fun reset() {
        maxTime = Long.MIN_VALUE
        minTime = Long.MAX_VALUE
        receiverMsgNum = 0
        sendMsgNum = 0
        totalAverTime = 0
    }

    private fun toast(content: String?) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
    }
}