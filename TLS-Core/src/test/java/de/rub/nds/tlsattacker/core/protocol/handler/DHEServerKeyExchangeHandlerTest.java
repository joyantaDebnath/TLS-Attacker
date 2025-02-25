/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.NamedGroup;
import de.rub.nds.tlsattacker.core.crypto.ffdh.FFDHEGroup;
import de.rub.nds.tlsattacker.core.crypto.ffdh.GroupFactory;
import de.rub.nds.tlsattacker.core.protocol.message.DHEServerKeyExchangeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigInteger;

public class DHEServerKeyExchangeHandlerTest extends
    AbstractTlsMessageHandlerTest<DHEServerKeyExchangeMessage, ServerKeyExchangeHandler<DHEServerKeyExchangeMessage>> {

    DHEServerKeyExchangeHandlerTest() {
        super(DHEServerKeyExchangeMessage::new, DHEServerKeyExchangeHandler::new);
    }

    /**
     * Test of adjustTLSContext method, of class DHEServerKeyExchangeHandler.
     */
    @Test
    @Override
    public void testAdjustTLSContext() {
        DHEServerKeyExchangeMessage message = new DHEServerKeyExchangeMessage();
        message.setModulus(BigInteger.TEN.toByteArray());
        message.setGenerator(BigInteger.ONE.toByteArray());
        message.setPublicKey(new byte[] { 1, 2, 3 });
        context.setSelectedCipherSuite(CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA);
        message.prepareComputations();
        message.getComputations().setPrivateKey(BigInteger.ZERO);
        handler.adjustTLSContext(message);
        assertEquals(BigInteger.TEN, context.getServerDhModulus());
        assertEquals(BigInteger.ONE, context.getServerDhGenerator());
        assertArrayEquals(new byte[] { 1, 2, 3 }, context.getServerDhPublicKey().toByteArray());
    }

    @Test
    public void testAdjustTLSContextWithoutComputations() {
        DHEServerKeyExchangeMessage message = new DHEServerKeyExchangeMessage();
        message.setModulus(BigInteger.TEN.toByteArray());
        message.setGenerator(BigInteger.ONE.toByteArray());
        message.setPublicKey(new byte[] { 1, 2, 3 });
        handler.adjustTLSContext(message);
        assertEquals(BigInteger.TEN, context.getServerDhModulus());
        assertEquals(BigInteger.ONE, context.getServerDhGenerator());
        assertArrayEquals(new byte[] { 1, 2, 3 }, context.getServerDhPublicKey().toByteArray());
    }

    @ParameterizedTest
    @EnumSource(value = NamedGroup.class, names = "^FFDHE[0-9]*", mode = EnumSource.Mode.MATCH_ANY)
    public void testAdjustTlsContextWithFFDHEGroup(NamedGroup providedNamedGroup) {
        DHEServerKeyExchangeMessage message = new DHEServerKeyExchangeMessage();
        FFDHEGroup group = GroupFactory.getGroup(providedNamedGroup);
        message.setModulus(group.getP().toByteArray());
        message.setGenerator(group.getG().toByteArray());
        message.setPublicKey(new byte[] { 1, 2, 3 });
        handler.adjustTLSContext(message);
        assertEquals(group.getG(), context.getServerDhGenerator());
        assertEquals(group.getP(), context.getServerDhModulus());
        assertArrayEquals(new byte[] { 1, 2, 3 }, context.getServerDhPublicKey().toByteArray());
        assertEquals(context.getSelectedGroup(), providedNamedGroup);
    }
}
