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
import de.rub.nds.tlsattacker.core.protocol.message.extension.MaxFragmentLengthExtensionMessage;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class MaxFragmentLengthExtensionParserTest
    extends AbstractExtensionParserTest<MaxFragmentLengthExtensionMessage, MaxFragmentLengthExtensionParser> {

    public MaxFragmentLengthExtensionParserTest() {
        super(MaxFragmentLengthExtensionParser::new,
            List.of(Named.of("MaxFragmentLengthExtensionMessage::getMaxFragmentLength",
                MaxFragmentLengthExtensionMessage::getMaxFragmentLength)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(ArrayConverter.hexStringToByteArray("0001000102"), List.of(),
            ExtensionType.MAX_FRAGMENT_LENGTH, 1, List.of(new byte[] { 0x02 })));
    }
}
