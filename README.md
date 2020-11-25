MQTT Demo说明：

1. 测试时分别运行Client和Server端。用false和true区分。修改`MqttConfig.kt`中的:

```groovy
   private var isServer = true
```

测试消息数量：在`MainActivity`中修改 MAX的值，默认100次。

2. 测试数据说明:

测试数据边界说明:

| 限制项         | 限制值      | 说明                                                         |
| :------------- | :---------- | :----------------------------------------------------------- |
| Topic 长度     | 3~64 个字符 | 使用微消息队列 MQTT 版收发消息时，Topic 长度不得低于或超过最值限制，否则会导致无法发送或者订阅。 |
| Client ID 长度 | 64 个字符   | 使用微消息队列 MQTT 版收发消息时，Client ID 长度不得超过该限制，否则会导致连接被断开。 |
| 消息大小       | 64 KB 字节  | 消息负载不得超过该限制，否则消息会被丢弃（企业铂金版可定制）。 |

[其他测试数据使用限制说明链接](https://help.aliyun.com/document_detail/63620.html?spm=a2c4g.11186623.6.554.1b93ae8aEDDoz4)

参考：[使用限制](https://help.aliyun.com/document_detail/63620.html?spm=a2c4g.11186623.6.554.1b93ae8aEDDoz4)

3. 测试时 在数据收发过程中不要一直点发消息消息或清空日志，否则统计数据会清零重新计算导致不准确。

4. 详细文档与测试结果可参考：[阿里云MQTT调研情况初稿](http://confluence.fiture.com/pages/viewpage.action?pageId=27564821)

5. Demo修改自阿里云Android Demo.

参考：[imq-android-demo](https://code.aliyun.com/aliware_mqtt/mqtt-demo/tree/master?spm=a2c4g.11186623.2.38.2a6d6fc61PlS3K)
