package io.horizontalsystems.bitcoincore.models

import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

class TransactionOutPoint {
    var hash : ByteArray? = null
    var index: Int = 0

    //normally useless
    constructor()

    constructor(txHash : ByteArray, i: Int) {
        hash = txHash
        index = i
    }

    fun serialize() : ByteArray {
        val indexBA = BigInteger.valueOf(index.toLong()).toByteArray()
        val result = ByteArray(indexBA.size + hash!!.size)
        System.arraycopy(hash!!, 0, result, 0, hash!!.size)
        System.arraycopy(indexBA, 0, result, hash!!.size, indexBA.size)
        return result
    }


    companion object {
        fun deserialize (data : ByteArray) : TransactionOutPoint {
            return TransactionOutPoint(Arrays.copyOfRange(data,0,31), ByteBuffer.wrap(Arrays.copyOfRange(data, 32, 35)).int)
        }
    }
}