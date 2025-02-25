/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer;

import de.rub.nds.tlsattacker.core.protocol.message.ClientHelloMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.ClientHelloParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class ClientHelloSerializerTest
    extends AbstractHandshakeMessageSerializerTest<ClientHelloMessage, ClientHelloSerializer> {

    public ClientHelloSerializerTest() {
        super(ClientHelloMessage::new, ClientHelloSerializer::new,
            List.of((msg, obj) -> msg.setProtocolVersion((byte[]) obj), (msg, obj) -> msg.setUnixTime((byte[]) obj),
                (msg, obj) -> msg.setRandom((byte[]) obj), (msg, obj) -> msg.setSessionIdLength((Integer) obj),
                (msg, obj) -> msg.setSessionId((byte[]) obj), (msg, obj) -> msg.setCipherSuiteLength((Integer) obj),
                (msg, obj) -> msg.setCipherSuites((byte[]) obj), (msg, obj) -> msg.setCompressionLength((Integer) obj),
                (msg, obj) -> msg.setCompressions((byte[]) obj), (msg, obj) -> msg.setExtensionsLength((Integer) obj),
                (msg, obj) -> msg.setExtensionBytes((byte[]) obj)
            // Cookie setters are not required as of now
            ));
    }

    public static Stream<Arguments> provideTestVectors() {
        return ClientHelloParserTest.provideTestVectors();
    }
}
