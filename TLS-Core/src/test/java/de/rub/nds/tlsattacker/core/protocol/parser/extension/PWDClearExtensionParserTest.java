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
import de.rub.nds.tlsattacker.core.protocol.message.extension.PWDClearExtensionMessage;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class PWDClearExtensionParserTest
    extends AbstractExtensionParserTest<PWDClearExtensionMessage, PWDClearExtensionParser> {

    public PWDClearExtensionParserTest() {
        super(PWDClearExtensionParser::new,
            List.of(
                Named.of("PWDClearExtensionMessage::getUsernameLength", PWDClearExtensionMessage::getUsernameLength),
                Named.of("PWDClearExtensionMessage::getUsername", PWDClearExtensionMessage::getUsername)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(ArrayConverter.hexStringToByteArray("001e00050466726564"), List.of(),
            ExtensionType.PWD_CLEAR, 5, List.of(4, "fred")));
    }
}
