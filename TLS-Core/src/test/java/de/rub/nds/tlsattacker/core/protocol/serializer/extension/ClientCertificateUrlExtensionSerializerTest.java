/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.ClientCertificateUrlExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.ClientCertificateUrlExtensionParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class ClientCertificateUrlExtensionSerializerTest extends AbstractExtensionMessageSerializerTest<
    ClientCertificateUrlExtensionMessage, ClientCertificateUrlExtensionSerializer> {

    public ClientCertificateUrlExtensionSerializerTest() {
        super(ClientCertificateUrlExtensionMessage::new, ClientCertificateUrlExtensionSerializer::new);
    }

    public static Stream<Arguments> provideTestVectors() {
        return ClientCertificateUrlExtensionParserTest.provideTestVectors();
    }
}
