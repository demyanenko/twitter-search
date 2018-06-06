import java.net.URLEncoder
import java.util.*
import java.util.UUID.randomUUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object OAuthUtil {
    fun getTimeStamp(): String {
        return (System.currentTimeMillis() / 1000).toString()
    }

    fun getNonce(): String {
        return randomUUID().toString().replace("-", "")
    }

    fun getSignature(signatureBaseString: String, keyString: String): String? {
        val algorithm = "HmacSHA1"
        val mac = Mac.getInstance(algorithm)
        val key = SecretKeySpec(keyString.toByteArray(), algorithm)

        mac.init(key)
        val digest = mac.doFinal(signatureBaseString.toByteArray())

        return urlEncode(String(Base64.getEncoder().encode(digest)))
    }

    fun urlEncode(beforeEncode: String): String {
        return URLEncoder.encode(beforeEncode, "UTF-8").replace("+", "%20")
    }

}
