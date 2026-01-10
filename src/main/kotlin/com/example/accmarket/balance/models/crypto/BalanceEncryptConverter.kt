package com.example.accmarket.balance.models.crypto

import com.example.accmarket.balance.models.crypto.BalanceCrypto
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.math.BigDecimal

@Converter
class BalanceEncryptConverter : AttributeConverter<BigDecimal, String> {

    override fun convertToDatabaseColumn(attribute: BigDecimal?): String? =
        attribute?.let { BalanceCrypto.encrypt(it.toPlainString()) }

    override fun convertToEntityAttribute(dbData: String?): BigDecimal? =
        dbData?.let { BigDecimal(BalanceCrypto.decrypt(it)) }
}