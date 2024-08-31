package ua.teri.totp.utils

import ua.teri.totp.data.OtpInfo;
import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun otpAuthFromURL(url: String): OtpInfo? {
    if (!url.startsWith("otpauth"))
        return null
    try {
        val uri = URI(url)

        val queryParams = uri.query?.split("&")?.associate {
            val (key, value) = it.split("=")
            key to URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        } ?: emptyMap()

        val authParts = uri.path.trim('/').split(':')
        if (authParts.size < 2) return null

        return OtpInfo(
            protocol = uri.host,
            platform = authParts[0],
            username = authParts[1],
            secret = queryParams["secret"] ?: return null,
            issuer = queryParams["issuer"] ?: authParts[0],
            algorithm = queryParams["algorithm"] ?: "SHA1",
            digits = queryParams["digits"]?.toInt() ?: 6,
            period = queryParams["period"]?.toInt(),
            counter = queryParams["counter"]?.toInt()
        )
    } catch (e: URISyntaxException) {
        return null
    }

}