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

import de.rub.nds.tlsattacker.core.protocol.message.extension.PaddingExtensionMessage;
import org.junit.jupiter.api.Test;

public class PaddingExtensionHandlerTest
    extends AbstractExtensionMessageHandlerTest<PaddingExtensionMessage, PaddingExtensionHandler> {

    private final byte[] extensionPayload = new byte[] { 0, 0, 0, 0, 0, 0 };

    public PaddingExtensionHandlerTest() {
        super(PaddingExtensionMessage::new, PaddingExtensionHandler::new);
    }

    /**
     * Test of adjustTLSContext method, of class PaddingExtensionHandler.
     */

    @Test
    @Override
    public void testAdjustTLSContext() {
        PaddingExtensionMessage msg = new PaddingExtensionMessage();
        msg.setPaddingBytes(extensionPayload);
        handler.adjustTLSContext(msg);
        assertArrayEquals(context.getPaddingExtensionBytes(), extensionPayload);
    }
}
