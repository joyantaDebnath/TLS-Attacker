/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import de.rub.nds.tlsattacker.core.constants.HeartbeatMode;
import de.rub.nds.tlsattacker.core.protocol.message.extension.HeartbeatExtensionMessage;
import org.junit.jupiter.api.Test;

public class HeartbeatExtensionHandlerTest
    extends AbstractExtensionMessageHandlerTest<HeartbeatExtensionMessage, HeartbeatExtensionHandler> {

    public HeartbeatExtensionHandlerTest() {
        super(HeartbeatExtensionMessage::new, HeartbeatExtensionHandler::new);
    }

    /**
     * Test of adjustTLSContext method, of class HeartbeatExtensionHandler.
     */
    @Test
    @Override
    public void testAdjustTLSContext() {
        HeartbeatExtensionMessage msg = new HeartbeatExtensionMessage();
        msg.setHeartbeatMode(new byte[] { 1 });
        handler.adjustTLSContext(msg);
        assertSame(HeartbeatMode.PEER_ALLOWED_TO_SEND, context.getHeartbeatMode());
    }

    @Test
    public void testAdjustUnspecifiedMode() {
        HeartbeatExtensionMessage msg = new HeartbeatExtensionMessage();
        msg.setHeartbeatMode(new byte[] { (byte) 0xFF });
        handler.adjustTLSContext(msg);
        assertNull(context.getHeartbeatMode());
    }

}
