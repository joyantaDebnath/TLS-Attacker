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

public class CertificateRequestMessageTest extends AbstractMessageTest<CertificateRequestMessage> {

    public CertificateRequestMessageTest() {
        super(CertificateRequestMessage::new,
            "CertificateRequestMessage:\n" + "  Certificate Types Count: %s\n" + "  Certificate Types: %s\n"
                + "  Signature Hash Algorithms Length: %s\n" + "  Signature Hash Algorithms: %s\n"
                + "  Distinguished Names Length: %s");
    }

    public static Stream<Arguments> provideToStringTestVectors() {
        return Stream.of(Arguments.of(new Object[] { null, null, null, null, null }, null));
    }

}
