import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.nio.aWrite
import kotlinx.coroutines.experimental.runBlocking
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


fun main(args: Array<String>) {
    runBlocking {
        run()
    }
}

suspend fun run() {
    try {
        val oauthConsumerKey = "7rCaLgFSAmlAElbhyE03C21bR"
        val oauthConsumerSecret = ""
        val oauthToken = ""
        val oauthTokenSecret = ""
        val authenticator = TwitterAuthenticator(
            oauthConsumerKey, oauthConsumerSecret, oauthToken, oauthTokenSecret)

        val url = "https://api.twitter.com/1.1/users/show.json"
        val parameters = listOf(Pair("screen_name", "belk_bl"))

        val request = Fuel.get(url, parameters)
        authenticator.authenticateRequest(request)
        request.responseString { req, response, result ->
            //do something with response
            when (result) {
                is Result.Failure -> {
                    println(req.toString())
                    println(String(response.data))
                    val ex = result.getException()
                    throw ex
                }
                is Result.Success -> {
                    println(result.get())
                }
            }
        }

        return

        val (_, response, result) = request.awaitResponse()
        val (dataBytes, _) = result
        val data = dataBytes.toString()

        val channel = AsynchronousFileChannel.open(
            Paths.get("response.json"), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        channel.aWrite(ByteBuffer.wrap(dataBytes), 0)
        println(data)
    } catch (e: Exception) {
        println("exception: $e")
        throw e
    }
}