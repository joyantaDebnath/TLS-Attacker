/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.extension.KeyShareExtensionMessage;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class KeyShareExtensionParserTest
    extends AbstractExtensionParserTest<KeyShareExtensionMessage, KeyShareExtensionParser> {

    public KeyShareExtensionParserTest() {
        super(KeyShareExtensionParser::new,
            List.of(
                Named.of("KeyShareExtensionMessage::getKeyShareListLength",
                    KeyShareExtensionMessage::getKeyShareListLength),
                Named.of("KeyShareExtensionMessage::getKeyShareListBytes",
                    KeyShareExtensionMessage::getKeyShareListBytes)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(
            ArrayConverter.hexStringToByteArray(
                "002800260024001D00202a981db6cdd02a06c1763102c9e741365ac4e6f72b3176a6bd6a3523d3ec0f4c"),
            List.of(), ExtensionType.KEY_SHARE_OLD, 38, List.of(36, ArrayConverter
                .hexStringToByteArray("001D00202a981db6cdd02a06c1763102c9e741365ac4e6f72b3176a6bd6a3523d3ec0f4c"))));
    }
}
