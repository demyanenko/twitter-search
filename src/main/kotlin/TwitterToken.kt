import com.github.kittinunf.fuel.core.Request
import java.net.URL

class TwitterAuthenticator(
    private val consumerKey: String, private val consumerSecret: String,
    private val oauthToken: String, private val oauthTokenSecret: String) {

    fun authenticateRequest(request: Request) {
        val oauthParameters = mutableMapOf(
            "oauth_consumer_key" to consumerKey,
            "oauth_nonce" to OAuthUtil.urlEncode(OAuthUtil.getNonce()),
            "oauth_signature_method" to "HMAC-SHA1",
            "oauth_timestamp" to OAuthUtil.getTimeStamp(),
            "oauth_version" to "1.0",
            "oauth_token" to oauthToken
        )

        val signatureParameters = oauthParameters.plus(
            request.parameters.map { p -> Pair(p.first, OAuthUtil.urlEncode(p.second.toString())) })

        val signatureParametersString = signatureParameters
            .map { h -> "${h.key}=${h.value}" }
            .sorted()
            .joinToString("&")

        val urlWithoutQuery = URL(request.url.protocol, request.url.authority, request.url.path)
        val encodedUrl = OAuthUtil.urlEncode(urlWithoutQuery.toString())
        val encodedParametersString = OAuthUtil.urlEncode(signatureParametersString)
        val signatureBaseString = "GET&$encodedUrl&$encodedParametersString"
        val keyString = "$consumerSecret&$oauthTokenSecret"
        val signature = OAuthUtil.getSignature(signatureBaseString, keyString)

        oauthParameters["oauth_signature"] = signature!!

        val oauthHeader = oauthParameters
            .map { h -> "${h.key}=\"${h.value}\""}
            .sorted()
            .joinToString(", ")

        request.headers["Authorization"] = "OAuth $oauthHeader"
    }
}