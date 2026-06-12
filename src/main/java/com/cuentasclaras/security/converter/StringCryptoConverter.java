package com.cuentasclaras.security.converter;

import com.cuentasclaras.security.CryptoService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Cifra/descifra automáticamente columnas de texto sensibles al persistir y leer.
 * Aplicar con @Convert(converter = StringCryptoConverter.class) sobre el campo.
 */
@Converter
public class StringCryptoConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return CryptoService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return CryptoService.decrypt(dbData);
    }
}
