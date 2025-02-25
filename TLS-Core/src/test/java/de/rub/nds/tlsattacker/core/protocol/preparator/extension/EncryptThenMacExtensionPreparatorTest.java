/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.preparator.extension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.extension.EncryptThenMacExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.EncryptThenMacExtensionSerializer;
import org.junit.jupiter.api.Test;

public class EncryptThenMacExtensionPreparatorTest extends AbstractExtensionMessagePreparatorTest<
    EncryptThenMacExtensionMessage, EncryptThenMacExtensionSerializer, EncryptThenMacExtensionPreparator> {

    public EncryptThenMacExtensionPreparatorTest() {
        super(EncryptThenMacExtensionMessage::new, EncryptThenMacExtensionMessage::new,
            EncryptThenMacExtensionSerializer::new, EncryptThenMacExtensionPreparator::new);
    }

    @Test
    @Override
    public void testPrepare() {
        preparator.prepare();

        assertArrayEquals(ExtensionType.ENCRYPT_THEN_MAC.getValue(), message.getExtensionType().getValue());
        assertEquals(0, (long) message.getExtensionLength().getValue());
    }
}
