/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.protocol.message.extension.sni.ServerNamePair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ServerNamePairParserTest {

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(ArrayConverter.hexStringToByteArray("00000b747769747465722e636f6d"), (byte) 0x00,
            11, ArrayConverter.hexStringToByteArray("747769747465722e636f6d")));
    }

    @ParameterizedTest
    @MethodSource("provideTestVectors")
    public void testParse(byte[] providedServerNamePairBytes, byte expectedServerNameType, int expectedServerNameLength,
        byte[] expectedServerName) {
        ServerNamePairParser parser = new ServerNamePairParser(0, providedServerNamePairBytes);
        ServerNamePair pair = parser.parse();
        assertEquals(expectedServerNameType, pair.getServerNameType().getValue());
        assertEquals(expectedServerNameLength, pair.getServerNameLength().getValue());
        assertArrayEquals(expectedServerName, pair.getServerName().getValue());
    }

}
