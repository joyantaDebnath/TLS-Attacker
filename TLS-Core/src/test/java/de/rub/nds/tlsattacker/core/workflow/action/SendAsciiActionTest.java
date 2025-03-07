/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.core.unittest.helper.FakeTransportHandler;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import org.junit.jupiter.api.Test;

public class SendAsciiActionTest extends AbstractActionTest<SendAsciiAction> {

    public SendAsciiActionTest() {
        super(new SendAsciiAction("STARTTLS", "US-ASCII"), SendAsciiAction.class);
        TlsContext context = state.getTlsContext();
        context.setTransportHandler(new FakeTransportHandler(ConnectionEndType.CLIENT));
    }

    /**
     * Test of getAsciiString method, of class SendAsciiAction.
     */
    @Test
    public void testGetAsciiString() {
        assertEquals("STARTTLS", action.getAsciiText());
    }
}
