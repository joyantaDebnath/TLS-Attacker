/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ClientCertificateUrlExtensionMessage;
import org.junit.jupiter.api.Test;

public class ClientCertificateUrlExtensionHandlerTest extends
    AbstractExtensionMessageHandlerTest<ClientCertificateUrlExtensionMessage, ClientCertificateUrlExtensionHandler> {

    public ClientCertificateUrlExtensionHandlerTest() {
        super(ClientCertificateUrlExtensionMessage::new, ClientCertificateUrlExtensionHandler::new);
    }

    @Test
    @Override
    public void testAdjustTLSContext() {
        ClientCertificateUrlExtensionMessage message = new ClientCertificateUrlExtensionMessage();
        handler.adjustTLSContext(message);
        assertTrue(context.isExtensionProposed(ExtensionType.CLIENT_CERTIFICATE_URL));
    }
}
