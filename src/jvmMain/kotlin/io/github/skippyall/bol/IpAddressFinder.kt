package io.github.skippyall.bol

import java.net.Inet6Address
import java.net.NetworkInterface

fun findLocalIp(): List<String> {
    return NetworkInterface.networkInterfaces()
        .filter { it.isUp && !it.isLoopback && !it.isVirtual }
        .flatMap { it.inetAddresses() }
        .filter { !it.isLoopbackAddress && !it.isLinkLocalAddress }
        .map {
            if(it is Inet6Address)
                "[${it.hostAddress.split("%")[0]}]"
            else
                it.hostAddress
        }
        .toList()
}