/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.rub.nds.tlsattacker.core.connection.OutboundConnection;
import de.rub.nds.tlsattacker.core.constants.AlertDescription;
import de.rub.nds.tlsattacker.core.constants.AlertLevel;
import de.rub.nds.tlsattacker.core.protocol.message.AlertMessage;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import org.junit.jupiter.api.Test;

public class AlertHandlerTest extends AbstractTlsMessageHandlerTest<AlertMessage, AlertHandler> {

    public AlertHandlerTest() {
        super(AlertMessage::new, AlertHandler::new);
    }

    /**
     * Test of adjustTLSContext method, of class AlertHandler.
     */
    @Test
    @Override
    public void testAdjustTLSContext() {
        context.setConnection(new OutboundConnection());
        context.setTalkingConnectionEndType(ConnectionEndType.SERVER);
        AlertMessage message = new AlertMessage();
        message.setDescription(AlertDescription.ACCESS_DENIED.getValue());
        message.setLevel(AlertLevel.WARNING.getValue());
        handler.adjustTLSContext(message);
        assertFalse(context.isReceivedFatalAlert());
        message.setLevel(AlertLevel.FATAL.getValue());
        handler.adjustTLSContext(message);
        assertTrue(context.isReceivedFatalAlert());
    }

}
