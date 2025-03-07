/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer;

import de.rub.nds.tlsattacker.core.protocol.message.CertificateMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.CertificateMessageParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class CertificateMessageSerializerTest
    extends AbstractHandshakeMessageSerializerTest<CertificateMessage, CertificateMessageSerializer> {

    public CertificateMessageSerializerTest() {
        super(CertificateMessage::new, CertificateMessageSerializer::new,
            List.of((msg, obj) -> msg.setCertificatesListLength((int) obj),
                (msg, obj) -> msg.setCertificatesListBytes((byte[]) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return CertificateMessageParserTest.provideTestVectors();
    }
}
