/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import de.rub.nds.tlsattacker.core.protocol.message.extension.SRPExtensionMessage;
import org.junit.jupiter.api.Test;

public class SrpExtensionHandlerTest
    extends AbstractExtensionMessageHandlerTest<SRPExtensionMessage, SrpExtensionHandler> {

    private static final byte[] SRP_IDENTIFIER = new byte[] { 0x00, 0x01, 0x02, 0x03 };
    private static final int SRP_IDENTIFIER_LENGTH = 4;

    public SrpExtensionHandlerTest() {
        super(SRPExtensionMessage::new, SrpExtensionHandler::new);
    }

    @Test
    @Override
    public void testAdjustTLSContext() {
        SRPExtensionMessage msg = new SRPExtensionMessage();
        msg.setSrpIdentifier(SRP_IDENTIFIER);
        msg.setSrpIdentifierLength(SRP_IDENTIFIER_LENGTH);
        handler.adjustTLSContext(msg);
        assertArrayEquals(SRP_IDENTIFIER, context.getSecureRemotePasswordExtensionIdentifier());
    }

}
