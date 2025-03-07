/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import de.rub.nds.tlsattacker.core.protocol.message.CertificateVerifyMessage;
import org.junit.jupiter.api.Test;

public class CertificateVerifyHandlerTest
    extends AbstractTlsMessageHandlerTest<CertificateVerifyMessage, CertificateVerifyHandler> {

    public CertificateVerifyHandlerTest() {
        super(CertificateVerifyMessage::new, CertificateVerifyHandler::new);
    }

    /**
     * Test of adjustTLSContext method, of class CertificateVerifyHandler.
     */
    @Test
    @Override
    public void testAdjustTLSContext() {
        CertificateVerifyMessage message = new CertificateVerifyMessage();
        handler.adjustTLSContext(message);
        // TODO make sure that nothing changed
    }
}
