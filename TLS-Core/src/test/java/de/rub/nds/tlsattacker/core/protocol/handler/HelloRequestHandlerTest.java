/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import de.rub.nds.tlsattacker.core.protocol.message.HelloRequestMessage;
import org.junit.jupiter.api.Test;

public class HelloRequestHandlerTest extends AbstractTlsMessageHandlerTest<HelloRequestMessage, HelloRequestHandler> {

    public HelloRequestHandlerTest() {
        super(HelloRequestMessage::new, HelloRequestHandler::new);
    }

    /**
     * Test of adjustTLSContext method, of class HelloRequestHandler.
     */
    @Test
    @Override
    public void testAdjustTLSContext() {
        HelloRequestMessage message = new HelloRequestMessage();
        handler.adjustTLSContext(message);
        // TODO make sure nothing changed
    }

}
