/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.AlpnExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.AlpnExtensionParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class AlpnExtensionSerializerTest
    extends AbstractExtensionMessageSerializerTest<AlpnExtensionMessage, AlpnExtensionSerializer> {

    public AlpnExtensionSerializerTest() {
        super(AlpnExtensionMessage::new, AlpnExtensionSerializer::new,
            List.of((msg, obj) -> msg.setProposedAlpnProtocolsLength((Integer) obj),
                (msg, obj) -> msg.setProposedAlpnProtocols((byte[]) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return AlpnExtensionParserTest.provideTestVectors();
    }
}
