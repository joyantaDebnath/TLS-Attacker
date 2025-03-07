/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer;

import de.rub.nds.tlsattacker.core.protocol.message.ServerHelloDoneMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.ServerHelloDoneParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class ServerHelloDoneSerializerTest
    extends AbstractHandshakeMessageSerializerTest<ServerHelloDoneMessage, ServerHelloDoneSerializer> {

    public ServerHelloDoneSerializerTest() {
        super(ServerHelloDoneMessage::new, ServerHelloDoneSerializer::new);
    }

    public static Stream<Arguments> provideTestVectors() {
        return ServerHelloDoneParserTest.provideTestVectors();
    }
}
