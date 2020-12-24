package io.horizontalsystems.bitcoinkit

import io.horizontalsystems.bitcoincore.models.Block
import io.horizontalsystems.bitcoincore.network.Network
import io.horizontalsystems.bitcoincore.storage.BlockHeader
import io.horizontalsystems.bitcoincore.utils.HashUtils

class MainNet : Network() {

    override var port: Int = 20102

    override var magic: Long = 0xf7a77effL
    override var bip32HeaderPub: Int = 0x022d2533   // The 4 byte header that serializes in base58 to "xpub".
    override var bip32HeaderPriv: Int = 0x0221312b  // The 4 byte header that serializes in base58 to "xprv"
    override var addressVersion: Int = 30
    override var addressSegwitHrp: String = "vg"
    override var addressScriptVersion: Int = 5
    override var coinType: Int = 77

    override val maxBlockSize = 1_000_000
    override val dustRelayTxFee = 3000 // https://github.com/bitcoin/bitcoin/blob/c536dfbcb00fb15963bf5d507b7017c241718bf6/src/policy/policy.h#L50

    override var dnsSeeds = listOf(
        "185.162.9.97",
        "104.131.144.82",
        "192.241.187.222",
        "105.228.198.44",
        "46.127.57.167",
        "98.5.123.15",
        "81.147.68.236",
        "77.67.46.100",
        "95.46.99.96",
        "138.201.91.159",
        "159.89.202.56",
        "163.158.20.118",
        "99.45.88.147",
        "114.145.237.35",
        "73.247.117.99",
        "145.239.0.122" 
    )

    override val bip44CheckpointBlock = Block(BlockHeader(
            version = 2,
            previousBlockHeaderHash = HashUtils.toBytesAsLE("00000000000000006bcf448b771c8f4db4e2ca653474e3b29504ec08422b3fba"),
            merkleRoot = HashUtils.toBytesAsLE("4ea18e999a57fc55fb390558dbb88a7b9c55c71c7de4cec160c045802ee587d2"),
            timestamp = 1397755646,
            bits = 419470732,
            nonce = 2160181286,
            hash = HashUtils.toBytesAsLE("00000000000000003decdbb5f3811eab3148fbc29d3610528eb3b50d9ee5723f")
    ), 296352)

    override val lastCheckpointBlock = Block(BlockHeader(
            version = 536879104,
            previousBlockHeaderHash = HashUtils.toBytesAsLE("0000000000000000000676463abf3771ea01e0f8c948d1c93658a1d82d95df5a"),
            merkleRoot = HashUtils.toBytesAsLE("24415cca3979b6f1b6a19c63160bf732030b1031b5615d98efe8c2cee083175e"),
            timestamp = 1571866973,
            bits = 387223263,
            nonce = 1093878582,
            hash = HashUtils.toBytesAsLE("0000000000000000000983a15a2735b4f37861c4019d97200820cd190f28dba5")
    ), 600768)
}
