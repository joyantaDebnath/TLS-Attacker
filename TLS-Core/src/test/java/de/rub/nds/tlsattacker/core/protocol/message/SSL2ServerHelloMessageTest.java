/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.message;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class SSL2ServerHelloMessageTest extends AbstractMessageTest<SSL2ServerHelloMessage> {

    public SSL2ServerHelloMessageTest() {
        super(SSL2ServerHelloMessage::new, "SSL2ServerHelloMessage:\n" + "  Protocol Version: %s\n" + "  Type: %s\n"
            + "  Supported CipherSuites: %s\n" + "  SessionIdHit: %s\n" + "  Certificate: %s\n" + "  SessionID: %s");
    }

    public static Stream<Arguments> provideToStringTestVectors() {
        return Stream.of(Arguments.of(new Object[] { null, null, null, null, null, null }, null));
    }
}
