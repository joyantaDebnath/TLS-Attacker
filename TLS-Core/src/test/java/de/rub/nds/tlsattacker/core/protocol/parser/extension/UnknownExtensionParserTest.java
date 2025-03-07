/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.protocol.message.extension.UnknownExtensionMessage;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class UnknownExtensionParserTest
    extends AbstractExtensionParserTest<UnknownExtensionMessage, UnknownExtensionParser> {

    public UnknownExtensionParserTest() {
        super(UnknownExtensionParser::new,
            List.of(Named.of("UnknownExtensionMessage::getExtensionData", UnknownExtensionMessage::getExtensionData)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(
            Arguments.of(ArrayConverter.hexStringToByteArray("00230000"), List.of(),
                ArrayConverter.hexStringToByteArray("0023"), 0, Collections.singletonList(null)),
            Arguments.of(ArrayConverter.hexStringToByteArray("000f000101"), List.of(),
                ArrayConverter.hexStringToByteArray("000f"), 1, List.of(ArrayConverter.hexStringToByteArray("01"))),
            Arguments.of(ArrayConverter.hexStringToByteArray("00000000"), List.of(),
                ArrayConverter.hexStringToByteArray("0000"), 0, Collections.singletonList(null)),
            Arguments.of(ArrayConverter.hexStringToByteArray("0000FFFF"), List.of(),
                ArrayConverter.hexStringToByteArray("0000"), 0xFFFF, Collections.singletonList(null)));
    }
}
