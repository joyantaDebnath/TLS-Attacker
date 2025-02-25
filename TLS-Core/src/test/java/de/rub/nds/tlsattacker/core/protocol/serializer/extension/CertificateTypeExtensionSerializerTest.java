/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.CertificateTypeExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.CertificateTypeExtensionParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class CertificateTypeExtensionSerializerTest extends
    AbstractExtensionMessageSerializerTest<CertificateTypeExtensionMessage, CertificateTypeExtensionSerializer> {

    public CertificateTypeExtensionSerializerTest() {
        super(CertificateTypeExtensionMessage::new, CertificateTypeExtensionSerializer::new, List.of((msg, obj) -> {
            if (obj != null) {
                msg.setCertificateTypesLength((Integer) obj);
            }
        }, (msg, obj) -> msg.setCertificateTypes((byte[]) obj), (msg, obj) -> msg.setIsClientMessage((Boolean) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return CertificateTypeExtensionParserTest.provideTestVectors();
    }
}
