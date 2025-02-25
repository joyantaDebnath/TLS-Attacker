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

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.extension.SupportedVersionsExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.SupportedVersionsExtensionSerializer;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

public class SupportedVersionsExtensionPreparatorTest extends AbstractExtensionMessagePreparatorTest<
    SupportedVersionsExtensionMessage, SupportedVersionsExtensionSerializer, SupportedVersionsExtensionPreparator> {

    public SupportedVersionsExtensionPreparatorTest() {
        super(SupportedVersionsExtensionMessage::new, SupportedVersionsExtensionMessage::new,
            SupportedVersionsExtensionSerializer::new, SupportedVersionsExtensionPreparator::new);
    }

    /**
     * Test of prepare method, of class SupportedVersionsExtensionPreparator.
     */
    @Test
    @Override
    public void testPrepare() {
        LinkedList<ProtocolVersion> supportedVersions = new LinkedList<>();
        supportedVersions.add(ProtocolVersion.TLS13);
        supportedVersions.add(ProtocolVersion.TLS12);
        context.getConfig().setSupportedVersions(supportedVersions);
        preparator.prepare();
        assertArrayEquals(message.getSupportedVersions().getValue(),
            ArrayConverter.concatenate(ProtocolVersion.TLS13.getValue(), ProtocolVersion.TLS12.getValue()));
        assertEquals(4, message.getSupportedVersionsLength().getValue());
    }
}
