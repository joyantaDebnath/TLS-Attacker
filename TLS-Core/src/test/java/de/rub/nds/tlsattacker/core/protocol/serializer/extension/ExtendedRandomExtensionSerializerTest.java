/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.ExtendedRandomExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.ExtendedRandomExtensionParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class ExtendedRandomExtensionSerializerTest
    extends AbstractExtensionMessageSerializerTest<ExtendedRandomExtensionMessage, ExtendedRandomExtensionSerializer> {

    public ExtendedRandomExtensionSerializerTest() {
        super(ExtendedRandomExtensionMessage::new, ExtendedRandomExtensionSerializer::new,
            List.of((msg, obj) -> msg.setExtendedRandomLength((Integer) obj),
                (msg, obj) -> msg.setExtendedRandom((byte[]) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return ExtendedRandomExtensionParserTest.provideTestVectors();
    }
}
