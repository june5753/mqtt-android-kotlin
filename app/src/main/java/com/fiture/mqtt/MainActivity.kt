package com.fiture.mqtt

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fiture.mqtt.lib.MqttConfig
import com.fiture.mqtt.lib.MqttLoger
import com.fiture.mqtt.lib.MqttManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val subscriptionTopic = "AndroidTopic"
    private val publishTopic = subscriptionTopic
    private var mMqttConfig: MqttConfig? = null

    /**
     * 默认ClientID
     */
    private var defaultClientId = "12345"

    /**
     * 测试消息数量（以收到消息次数为准）
     */
    private val MAX = 100

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
        // 初始化
        mMqttConfig = MqttConfig().create()
        MqttManager.getInstance().init(this, mMqttConfig!!) {
            //收到消息
            onMessageArrived { topic, message, qos ->
                handleArrivedMessage(message.toString())
            }
            //连接失败
            onConnectionLost {
                showTips("连接已断开${it?.message.toString()}")
                toast("连接已断开${it?.message.toString()}")
            }
            onDeliveryComplete {
                tvPushMessageState.text = "已发送的消息：$it"
            }

            onConnectFailed {
                toast("onConnectFailed${it?.message.toString()}")
            }
        }
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
        // 订阅主题（阿里云订阅不需要）
        btnSubscribe.setOnClickListener {
            showTips("正在订阅中...")
            MqttManager.getInstance().subscribe(subscriptionTopic) {

                onSubscriberSuccess {
                    showTips("订阅成功")
                }

                onSubscriberFailed {
                    showTips("订阅失败：${it?.message}")
                }

                onMessageArrived { topic, message, qos ->

                }

                onDeliveryComplete {
                    showTips("消息推送完毕：$it")
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
                MqttManager.getInstance()
                    .publishMessage(MqttManager.getInstance().getTopic()!!, message)
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
                .publishMessage(MqttManager.getInstance().getTopic()!!, message)
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