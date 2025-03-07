/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.preparator.extension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.tlsattacker.core.constants.AuthzDataFormat;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ClientAuthzExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.ClientAuthzExtensionSerializer;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ClientAuthzExtensionPreparatorTest extends AbstractExtensionMessagePreparatorTest<
    ClientAuthzExtensionMessage, ClientAuthzExtensionSerializer, ClientAuthzExtensionPreparator> {

    public ClientAuthzExtensionPreparatorTest() {
        super(ClientAuthzExtensionMessage::new, ClientAuthzExtensionMessage::new, ClientAuthzExtensionSerializer::new,
            ClientAuthzExtensionPreparator::new);
    }

    @Test
    @Override
    public void testPrepare() {
        List<AuthzDataFormat> authzFormatList = List.of(AuthzDataFormat.X509_ATTR_CERT, AuthzDataFormat.SAML_ASSERTION,
            AuthzDataFormat.X509_ATTR_CERT_URL, AuthzDataFormat.SAML_ASSERTION_URL);
        context.getConfig().setClientAuthzExtensionDataFormat(authzFormatList);

        preparator.prepare();

        assertArrayEquals(ExtensionType.CLIENT_AUTHZ.getValue(), message.getExtensionType().getValue());
        assertEquals(4, (long) message.getAuthzFormatListLength().getValue());
        assertArrayEquals(new byte[] { 0x00, 0x01, 0x02, 0x03 }, message.getAuthzFormatList().getValue());
    }
}
