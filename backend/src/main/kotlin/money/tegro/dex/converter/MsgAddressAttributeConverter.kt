package money.tegro.dex.converter

import io.micronaut.core.convert.ConversionContext
import io.micronaut.data.model.runtime.convert.AttributeConverter
import jakarta.inject.Singleton
import org.ton.block.MsgAddress
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@Singleton
class MsgAddressAttributeConverter : AttributeConverter<MsgAddress, ByteArray> {
    override fun convertToPersistedValue(entityValue: MsgAddress?, context: ConversionContext): ByteArray? =
        entityValue?.let { BagOfCells(CellBuilder.createCell { storeTlb(MsgAddress, it) }).toByteArray() }

    override fun convertToEntityValue(persistedValue: ByteArray?, context: ConversionContext): MsgAddress? =
        persistedValue?.let { BagOfCells(it).roots.first().parse { loadTlb(MsgAddress) } }
}
