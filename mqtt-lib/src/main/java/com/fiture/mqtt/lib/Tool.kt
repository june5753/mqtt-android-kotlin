package com.fiture.mqtt.lib

import android.util.Base64
import java.nio.charset.Charset
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 *<pre>
 *  author : juneYang
 *  time   : 2020/11/24 3:30 PM
 *  desc   :MQTT 密码签名工具类
 *  version: 1.0
 *</pre>
 */
object Tool {
    /**
     * @param text      要签名的文本
     * @param secretKey 阿里云MQ secretKey
     * @return 加密后的字符串
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    @Throws(InvalidKeyException::class, NoSuchAlgorithmException::class)
    fun macSignature(text: String, secretKey: String): String {
        val charset = Charset.forName("UTF-8")
        val algorithm = "HmacSHA1"
        val mac = Mac.getInstance(algorithm)
        mac.init(SecretKeySpec(secretKey.toByteArray(charset), algorithm))
        val bytes = mac.doFinal(text.toByteArray(charset))
        // android的base64编码注意换行符情况, 使用NO_WRAP
        return String(Base64.encode(bytes, Base64.NO_WRAP), charset)
    }
}