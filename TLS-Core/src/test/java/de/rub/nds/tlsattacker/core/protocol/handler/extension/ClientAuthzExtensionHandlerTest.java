/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.constants.AuthzDataFormat;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ClientAuthzExtensionMessage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ClientAuthzExtensionHandlerTest
    extends AbstractExtensionMessageHandlerTest<ClientAuthzExtensionMessage, ClientAuthzExtensionHandler> {

    private final byte[] authzFormatListAsBytes = ArrayConverter.hexStringToByteArray("00010203");
    private final List<AuthzDataFormat> authzFormatList = Arrays.asList(AuthzDataFormat.X509_ATTR_CERT,
        AuthzDataFormat.SAML_ASSERTION, AuthzDataFormat.X509_ATTR_CERT_URL, AuthzDataFormat.SAML_ASSERTION_URL);

    public ClientAuthzExtensionHandlerTest() {
        super(ClientAuthzExtensionMessage::new, ClientAuthzExtensionHandler::new);
    }

    @Test
    @Override
    public void testAdjustTLSContext() {
        ClientAuthzExtensionMessage msg = new ClientAuthzExtensionMessage();
        msg.setAuthzFormatList(authzFormatListAsBytes);

        handler.adjustTLSContext(msg);

        assertEquals(authzFormatList, context.getClientAuthzDataFormatList());
    }
}
