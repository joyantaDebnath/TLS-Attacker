/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.preparator.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.SignatureAndHashAlgorithmsExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.SignatureAndHashAlgorithmsExtensionSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class SignatureAndHashAlgorithmsExtensionPreparatorTest
    extends AbstractExtensionMessagePreparatorTest<SignatureAndHashAlgorithmsExtensionMessage,
        SignatureAndHashAlgorithmsExtensionSerializer, SignatureAndHashAlgorithmsExtensionPreparator> {

    public SignatureAndHashAlgorithmsExtensionPreparatorTest() {
        super(SignatureAndHashAlgorithmsExtensionMessage::new, SignatureAndHashAlgorithmsExtensionMessage::new,
            SignatureAndHashAlgorithmsExtensionSerializer::new, SignatureAndHashAlgorithmsExtensionPreparator::new);
    }

    /**
     * Test of prepareExtensionContent method, of class SignatureAndHashAlgorithmsExtensionPreparator.
     */
    @Test
    @Disabled("Not implemented")
    @Override
    public void testPrepare() {
    }
}
