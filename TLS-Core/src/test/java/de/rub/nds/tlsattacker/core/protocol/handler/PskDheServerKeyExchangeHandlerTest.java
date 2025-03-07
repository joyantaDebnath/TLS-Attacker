/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import static org.junit.jupiter.api.Assertions.assertNull;

import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.protocol.message.PskDheServerKeyExchangeMessage;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class PskDheServerKeyExchangeHandlerTest
    extends AbstractTlsMessageHandlerTest<PskDheServerKeyExchangeMessage, PskDheServerKeyExchangeHandler> {

    public PskDheServerKeyExchangeHandlerTest() {
        super(PskDheServerKeyExchangeMessage::new, PskDheServerKeyExchangeHandler::new);
    }

    /**
     * Test of adjustTLSContext method, of class PskDheServerKeyExchangeHandler.
     */
    @Test
    @Override
    public void testAdjustTLSContext() {
        PskDheServerKeyExchangeMessage message = new PskDheServerKeyExchangeMessage();
        message.setModulus(BigInteger.TEN.toByteArray());
        message.setGenerator(BigInteger.ONE.toByteArray());
        message.setPublicKey(new byte[] { 0, 1, 2, 3 });
        context.setSelectedCipherSuite(CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA);
        message.prepareComputations();
        message.getComputations().setPrivateKey(BigInteger.ZERO);
        handler.adjustTLSContext(message);
        assertNull(context.getPreMasterSecret());
    }

    @Test
    public void testAdjustTLSContextWithoutComputations() {
        PskDheServerKeyExchangeMessage message = new PskDheServerKeyExchangeMessage();
        message.setModulus(BigInteger.TEN.toByteArray());
        message.setGenerator(BigInteger.ONE.toByteArray());
        message.setPublicKey(new byte[] { 0, 1, 2, 3 });
        handler.adjustTLSContext(message);
        assertNull(context.getPreMasterSecret());
        assertNull(context.getMasterSecret());
    }
}
