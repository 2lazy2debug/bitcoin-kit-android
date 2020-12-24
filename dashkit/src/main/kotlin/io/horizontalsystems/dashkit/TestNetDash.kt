package io.horizontalsystems.dashkit

import io.horizontalsystems.bitcoincore.models.Block
import io.horizontalsystems.bitcoincore.network.Network
import io.horizontalsystems.bitcoincore.storage.BlockHeader
import io.horizontalsystems.bitcoincore.utils.HashUtils

class TestNetDash : Network() {

    override val protocolVersion = 70214

    override var port: Int = 19999

    override var magic: Long = 0xffcae2ce
    override var bip32HeaderPub: Int = 0x0488B21E   // The 4 byte header that serializes in base58 to "xpub".
    override var bip32HeaderPriv: Int = 0x0488ADE4  // The 4 byte header that serializes in base58 to "xprv"
    override var addressVersion: Int = 140
    override var addressSegwitHrp: String = "bc"
    override var addressScriptVersion: Int = 19
    override var coinType: Int = 1

    override val maxBlockSize = 1_000_000
    override val dustRelayTxFee = 1000 // https://github.com/dashpay/dash/blob/master/src/policy/policy.h#L36

    override var dnsSeeds = listOf(
            "testnet-seed.dashdot.io",
            "test.dnsseed.masternode.io"
    )

    override val bip44CheckpointBlock = Block(BlockHeader(
            version = 1,
            previousBlockHeaderHash = HashUtils.toBytesAsLE("0000000000000000000000000000000000000000000000000000000000000000"),
            merkleRoot = HashUtils.toBytesAsLE("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"),
            timestamp = 1231006505,
            bits = 486604799,
            nonce = 2083236893,
            hash = HashUtils.toBytesAsLE("00000bafbc94add76cb75e2ec92894837288a481e5c005f6563d91623bf8bc2c")
    ), 0)

    override val lastCheckpointBlock = Block(BlockHeader(
            version = 536870912,
            previousBlockHeaderHash = HashUtils.toBytesAsLE("000007bec14c122412335571908232996015c0eb49cb475796fac40154853b8f"),
            merkleRoot = HashUtils.toBytesAsLE("c018ec01fe4187701059d959e443cb7b653672106d727cd1a2e73bc1dc9ef69b"),
            timestamp = 1573118434,
            bits = 504216786,
            nonce = 8013,
            hash = HashUtils.toBytesAsLE("000008fd74af527d0278b11ebdfed0e729e5cb97009e0cf1d60ae2559bf19b5a")
    ), 206472)

}
