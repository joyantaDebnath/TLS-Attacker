/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.preparator;

import de.rub.nds.tlsattacker.core.protocol.message.ServerHelloDoneMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ServerHelloDonePreparatorTest
    extends AbstractTlsMessagePreparatorTest<ServerHelloDoneMessage, ServerHelloDonePreparator> {

    public ServerHelloDonePreparatorTest() {
        super(ServerHelloDoneMessage::new, ServerHelloDoneMessage::new, ServerHelloDonePreparator::new);
    }

    /**
     * Test of prepareHandshakeMessageContents method, of class ServerHelloDonePreparator.
     */
    @Test
    @Disabled("Not implemented")
    @Override
    public void testPrepare() {
    }
}
