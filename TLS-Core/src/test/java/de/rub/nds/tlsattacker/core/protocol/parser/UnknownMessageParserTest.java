/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.UnknownMessage;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class UnknownMessageParserTest extends AbstractTlsMessageParserTest<UnknownMessage, UnknownMessageParser> {

    public UnknownMessageParserTest() {
        super(UnknownMessageParser::new);
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(ProtocolVersion.TLS12, ArrayConverter.hexStringToByteArray("00010203"), List.of(
            // Only used during serializer test
            ArrayConverter.hexStringToByteArray("00010203"))));
    }
}
