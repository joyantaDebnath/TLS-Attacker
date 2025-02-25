/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.UnknownExtensionMessage;
import org.junit.jupiter.api.Test;

public class UnknownExtensionHandlerTest
    extends AbstractExtensionMessageHandlerTest<UnknownExtensionMessage, UnknownExtensionHandler> {

    public UnknownExtensionHandlerTest() {
        super(UnknownExtensionMessage::new, UnknownExtensionHandler::new);
    }

    /**
     * Test of adjustTLSContext method, of class UnknownExtensionHandler.
     */
    @Test
    @Override
    public void testAdjustTLSContext() {
        UnknownExtensionMessage msg = new UnknownExtensionMessage();
        handler.adjustTLSContext(msg);
        // TODO Check that context does not change
    }
}
