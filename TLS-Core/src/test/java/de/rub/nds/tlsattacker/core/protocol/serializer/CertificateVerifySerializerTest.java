/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer;

import de.rub.nds.tlsattacker.core.protocol.message.CertificateVerifyMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.CertificateVerifyParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class CertificateVerifySerializerTest
    extends AbstractHandshakeMessageSerializerTest<CertificateVerifyMessage, CertificateVerifySerializer> {

    public CertificateVerifySerializerTest() {
        super(CertificateVerifyMessage::new, CertificateVerifySerializer::new,
            List.of((msg, obj) -> msg.setSignatureHashAlgorithm((byte[]) obj),
                (msg, obj) -> msg.setSignatureLength((Integer) obj), (msg, obj) -> msg.setSignature((byte[]) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return CertificateVerifyParserTest.provideTestVectors();
    }
}
