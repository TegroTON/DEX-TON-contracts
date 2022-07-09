package money.tegro.dex.converter

import io.micronaut.core.convert.ConversionContext
import io.micronaut.data.model.runtime.convert.AttributeConverter
import jakarta.inject.Singleton
import org.ton.block.MsgAddressInt
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@Singleton
class MsgAddressIntAttributeConverter : AttributeConverter<MsgAddressInt, ByteArray> {
    override fun convertToPersistedValue(entityValue: MsgAddressInt?, context: ConversionContext): ByteArray? =
        entityValue?.let { BagOfCells(CellBuilder.createCell { storeTlb(msgAddressIntCodec, it) }).toByteArray() }

    override fun convertToEntityValue(persistedValue: ByteArray?, context: ConversionContext): MsgAddressInt? =
        persistedValue?.let { BagOfCells(it).roots.first().parse { loadTlb(msgAddressIntCodec) } }

    companion object {
        val msgAddressIntCodec = MsgAddressInt.tlbCodec()
    }
}
