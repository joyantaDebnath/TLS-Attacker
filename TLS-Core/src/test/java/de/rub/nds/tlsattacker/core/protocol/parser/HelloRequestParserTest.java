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
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.HelloRequestMessage;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class HelloRequestParserTest
    extends AbstractHandshakeMessageParserTest<HelloRequestMessage, HelloRequestParser> {

    public HelloRequestParserTest() {
        super(HelloRequestParser::new);
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(ProtocolVersion.TLS12, ArrayConverter.hexStringToByteArray("00000000"),
            List.of(HandshakeMessageType.HELLO_REQUEST.getValue(), 0)));
    }
}
