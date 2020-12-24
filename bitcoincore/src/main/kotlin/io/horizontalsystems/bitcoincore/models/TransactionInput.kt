package io.horizontalsystems.bitcoincore.models

import android.arch.persistence.room.*
import io.horizontalsystems.bitcoincore.storage.WitnessConverter

/**
 * Transaction input
 *
 *  Size        Field                Description
 *  ===         =====                ===========
 *  32 bytes    OutputHash           Double SHA-256 hash of the transaction containing the output to be used by this input
 *  4 bytes     OutputIndex          Index of the output within the transaction
 *  VarInt      InputScriptLength    Script length
 *  Variable    InputScript          Script
 *  4 bytes     InputSeqNumber       Input sequence number (irrelevant unless transaction LockTime is non-zero)
 *
 *  Note: In order to enable nLockTime and disable Replace-By-Fee, nSequenceNumber must be set to 0xfffffffe (BIP-125)
 *
 */

@Entity(primaryKeys = ["previousOutputTxHash", "previousOutputIndex"],
        foreignKeys = [ForeignKey(
                entity = Transaction::class,
                parentColumns = ["hash"],
                childColumns = ["transactionHash"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE,
                deferred = true)
        ])

class TransactionInput {

    var previousOutputTxHash: ByteArray = byteArrayOf()
    var previousOutputIndex: Long = 0
    var sigScript: ByteArray
    var sequence: Long
    var transactionHash: ByteArray? = null
    var keyHash: ByteArray? = null
    var address: String? = ""

    constructor(previousOutputTxHash: ByteArray, previousOutputIndex: Long, sigScript: ByteArray = byteArrayOf(), sequence: Long = 0xfffffffe) {
        this.previousOutputTxHash = previousOutputTxHash
        this.previousOutputIndex = previousOutputIndex
        this.sigScript = sigScript
        this.sequence = sequence
        this.transactionHash = byteArrayOf()
        this.witness = listOf()
    }

    @Ignore
    constructor(previousOutPoint : TransactionOutPoint, sigScript: ByteArray, sequence : Long) {
        this.previousOutPoint = previousOutPoint
        this.sigScript = sigScript
        this.sequence = sequence
    }
    


    @TypeConverters(WitnessConverter::class)
    lateinit var witness: List<ByteArray>

    @TypeConverters(OutPointTypeConverter::class)
    lateinit var previousOutPoint: TransactionOutPoint
}

class OutPointTypeConverter {

    @TypeConverter
    fun fromOutPoint(outpoint: TransactionOutPoint): ByteArray {
        return outpoint.serialize()
    }

    @TypeConverter
    fun toOutPoint(data: ByteArray): TransactionOutPoint = when {
        data.isEmpty() -> TransactionOutPoint()
        else -> TransactionOutPoint.deserialize(data)
    }
}
