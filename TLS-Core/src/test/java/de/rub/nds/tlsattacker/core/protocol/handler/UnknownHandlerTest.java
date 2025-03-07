/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import de.rub.nds.tlsattacker.core.constants.ProtocolMessageType;
import de.rub.nds.tlsattacker.core.protocol.message.UnknownMessage;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import org.junit.jupiter.api.Test;

public class UnknownHandlerTest extends AbstractTlsMessageHandlerTest<UnknownMessage, UnknownMessageHandler> {

    public UnknownHandlerTest() {
        super(UnknownMessage::new,
            (TlsContext context) -> new UnknownMessageHandler(context, ProtocolMessageType.UNKNOWN));
    }

    /**
     * Test of adjustTLSContext method, of class UnknownHandler.
     */
    @Test
    @Override
    public void testAdjustTLSContext() {
        UnknownMessage message = new UnknownMessage(context.getConfig(), ProtocolMessageType.UNKNOWN);
        handler.adjustTLSContext(message);
    }

}
