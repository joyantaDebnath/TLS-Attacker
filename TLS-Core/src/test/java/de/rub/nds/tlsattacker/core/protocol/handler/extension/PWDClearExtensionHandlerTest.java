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
import de.rub.nds.tlsattacker.core.protocol.message.extension.PWDClearExtensionMessage;
import org.junit.jupiter.api.Test;

public class PWDClearExtensionHandlerTest
    extends AbstractExtensionMessageHandlerTest<PWDClearExtensionMessage, PWDClearExtensionHandler> {

    public PWDClearExtensionHandlerTest() {
        super(PWDClearExtensionMessage::new, PWDClearExtensionHandler::new);
    }

    @Test
    @Override
    public void testAdjustTLSContext() {
        PWDClearExtensionMessage message = new PWDClearExtensionMessage();
        message.setUsername("jens");
        handler.adjustTLSContext(message);
        assertTrue(context.isExtensionProposed(ExtensionType.PWD_CLEAR));
    }
}