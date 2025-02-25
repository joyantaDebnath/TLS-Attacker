/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.supplementaldata;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.protocol.message.supplementaldata.SupplementalDataEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class SupplementalDataEntryParserTest {

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(ArrayConverter.hexStringToByteArray("4002000a0008010005aaaaaaaaaa"), 16386, 10,
            ArrayConverter.hexStringToByteArray("0008010005aaaaaaaaaa")));
    }

    @ParameterizedTest
    @MethodSource("provideTestVectors")
    public void testParse(byte[] providedSupplementalDataEntry, int expectedSupplementalDataEntryType,
        int expectedSupplementalDataEntryLength, byte[] expectedSupplementalDataEntry) {
        SupplementalDataEntryParser parser = new SupplementalDataEntryParser(0, providedSupplementalDataEntry);
        SupplementalDataEntry entry = parser.parse();
        assertEquals(expectedSupplementalDataEntryType, (int) entry.getSupplementalDataEntryType().getValue());
        assertEquals(expectedSupplementalDataEntryLength, (int) entry.getSupplementalDataEntryLength().getValue());
        assertArrayEquals(expectedSupplementalDataEntry, entry.getSupplementalDataEntry().getValue());
    }

}
