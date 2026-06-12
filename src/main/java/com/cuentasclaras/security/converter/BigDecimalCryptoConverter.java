package com.cuentasclaras.security.converter;

import com.cuentasclaras.security.CryptoService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.math.BigDecimal;

/**
 * Cifra/descifra montos al persistir y leer. El valor se guarda como texto cifrado,
 * por lo que la columna deja de ser numérica (DECIMAL) y pasa a ser VARCHAR.
 * Consecuencia: no se puede sumar/ordenar por estos montos en SQL; los cálculos
 * se hacen en memoria tras descifrar.
 */
@Converter
public class BigDecimalCryptoConverter implements AttributeConverter<BigDecimal, String> {

    @Override
    public String convertToDatabaseColumn(BigDecimal attribute) {
        return attribute == null ? null : CryptoService.encrypt(attribute.toPlainString());
    }

    @Override
    public BigDecimal convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        return new BigDecimal(CryptoService.decrypt(dbData));
    }
}
