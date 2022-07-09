package money.tegro.dex.converter

import io.micronaut.core.convert.ConversionContext
import io.micronaut.data.model.runtime.convert.AttributeConverter
import jakarta.inject.Singleton
import org.ton.block.AddrStd
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@Singleton
class AddrStdAttributeConverter : AttributeConverter<AddrStd, ByteArray> {
    override fun convertToPersistedValue(entityValue: AddrStd?, context: ConversionContext): ByteArray? =
        entityValue?.let { BagOfCells(CellBuilder.createCell { storeTlb(addrStdCodec, it) }).toByteArray() }

    override fun convertToEntityValue(persistedValue: ByteArray?, context: ConversionContext): AddrStd? =
        persistedValue?.let { BagOfCells(it).roots.first().parse { loadTlb(addrStdCodec) } }

    companion object {
        val addrStdCodec = AddrStd.tlbCodec()
    }
}
