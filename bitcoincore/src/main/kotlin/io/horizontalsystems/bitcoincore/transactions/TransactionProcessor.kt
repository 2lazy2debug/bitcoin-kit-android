package io.horizontalsystems.bitcoincore.transactions

import io.horizontalsystems.bitcoincore.blocks.IBlockchainDataListener
import io.horizontalsystems.bitcoincore.core.IStorage
import io.horizontalsystems.bitcoincore.core.inTopologicalOrder
import io.horizontalsystems.bitcoincore.managers.AddressManager
import io.horizontalsystems.bitcoincore.managers.BloomFilterManager
import io.horizontalsystems.bitcoincore.models.Block
import io.horizontalsystems.bitcoincore.models.Transaction
import io.horizontalsystems.bitcoincore.storage.FullTransaction
import io.horizontalsystems.bitcoincore.transactions.scripts.ScriptType
import java.util.*

class TransactionProcessor(
        private val storage: IStorage,
        private val extractor: TransactionExtractor,
        private val linker: TransactionLinker,
        private val addressManager: AddressManager,
        private val dataListener: IBlockchainDataListener) {

    fun processOutgoing(transaction: FullTransaction) {
        if (storage.getTransaction(transaction.header.hashHexReversed) != null) {
            throw TransactionCreator.TransactionAlreadyExists("hashHexReversed = ${transaction.header.hashHexReversed}")
        }

        process(transaction)

        storage.addTransaction(transaction)
        dataListener.onTransactionsUpdate(listOf(transaction.header), listOf(), null)
    }

    @Throws(BloomFilterManager.BloomFilterExpired::class)
    fun processIncoming(transactions: List<FullTransaction>, block: Block?, skipCheckBloomFilter: Boolean) {
        var needToUpdateBloomFilter = false

        val inserted = mutableListOf<Transaction>()
        val updated = mutableListOf<Transaction>()

        // when the same transaction came in merkle block and from another peer's mempool we need to process it serial
        synchronized(this) {
            for ((index, transaction) in transactions.inTopologicalOrder().withIndex()) {
                val transactionInDB = storage.getTransaction(transaction.header.hashHexReversed)
                if (transactionInDB != null) {
                    relay(transactionInDB, index, block)
                    storage.updateTransaction(transactionInDB)

                    updated.add(transactionInDB)
                    continue
                }

                process(transaction)

                if (transaction.header.isMine) {
                    relay(transaction.header, index, block)

                    storage.addTransaction(transaction)
                    inserted.add(transaction.header)

                    if (!skipCheckBloomFilter) {
                        needToUpdateBloomFilter = needToUpdateBloomFilter || addressManager.gapShifts() || hasUnspentOutputs(transaction)
                    }
                }
            }
        }

        if (inserted.isNotEmpty() || updated.isNotEmpty()) {
            dataListener.onTransactionsUpdate(inserted, updated, block)
        }

        if (needToUpdateBloomFilter) {
            throw BloomFilterManager.BloomFilterExpired
        }
    }

    private fun process(transaction: FullTransaction) {
        extractor.extractOutputs(transaction)
        linker.handle(transaction)

        if (transaction.header.isMine) {
            extractor.extractAddress(transaction)
            extractor.extractInputs(transaction)
        }
    }

    private fun relay(transaction: Transaction, order: Int, block: Block?) {
        transaction.blockHashReversedHex = block?.headerHashReversedHex
        transaction.status = Transaction.Status.RELAYED
        transaction.timestamp = block?.timestamp ?: (Date().time / 1000)
        transaction.order = order
    }

    private fun hasUnspentOutputs(transaction: FullTransaction): Boolean {
        return transaction.outputs.any { output ->
            (output.scriptType == ScriptType.P2PK || output.scriptType == ScriptType.P2WPKH) && output.publicKey(storage) != null
        }
    }

}