/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.SupportedVersionsExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.SupportedVersionsExtensionParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class SupportedVersionsExtensionSerializerTest extends
    AbstractExtensionMessageSerializerTest<SupportedVersionsExtensionMessage, SupportedVersionsExtensionSerializer> {

    public SupportedVersionsExtensionSerializerTest() {
        super(SupportedVersionsExtensionMessage::new, SupportedVersionsExtensionSerializer::new,
            List.of((msg, obj) -> msg.setSupportedVersionsLength((Integer) obj),
                (msg, obj) -> msg.setSupportedVersions((byte[]) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return SupportedVersionsExtensionParserTest.provideTestVectors();
    }
}
