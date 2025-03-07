/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer;

import de.rub.nds.tlsattacker.core.protocol.message.CertificateRequestMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.CertificateRequestTls13ParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class CertificateRequestTls13SerializerTest
    extends AbstractHandshakeMessageSerializerTest<CertificateRequestMessage, CertificateRequestSerializer> {

    public CertificateRequestTls13SerializerTest() {
        super(CertificateRequestMessage::new, CertificateRequestSerializer::new,
            List.of((msg, obj) -> msg.setCertificateRequestContextLength((Integer) obj),
                (msg, obj) -> msg.setCertificateRequestContext((byte[]) obj),
                (msg, obj) -> msg.setExtensionsLength((Integer) obj),
                (msg, obj) -> msg.setExtensionBytes((byte[]) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return CertificateRequestTls13ParserTest.provideTestVectors();
    }
}
