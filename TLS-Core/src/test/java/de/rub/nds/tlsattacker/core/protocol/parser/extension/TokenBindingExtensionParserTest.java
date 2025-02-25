/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.constants.TokenBindingKeyParameters;
import de.rub.nds.tlsattacker.core.constants.TokenBindingVersion;
import de.rub.nds.tlsattacker.core.protocol.message.extension.TokenBindingExtensionMessage;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class TokenBindingExtensionParserTest
    extends AbstractExtensionParserTest<TokenBindingExtensionMessage, TokenBindingExtensionParser> {

    public TokenBindingExtensionParserTest() {
        super(TokenBindingExtensionParser::new,
            List.of(
                Named.of("TokenBindingExtensionMessage::getTokenBindingVersion",
                    TokenBindingExtensionMessage::getTokenBindingVersion),
                Named.of("TokenBindingExtensionMessage::getParameterListLength",
                    TokenBindingExtensionMessage::getParameterListLength),
                Named.of("TokenBindingExtensionMessage::getTokenBindingKeyParameters",
                    TokenBindingExtensionMessage::getTokenBindingKeyParameters)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(new byte[] { 0x00, 0x18, 0x00, 0x04, 0x00, 0x0d, 0x01, 0x02 }, List.of(),
            ExtensionType.TOKEN_BINDING, 4, List.of(TokenBindingVersion.DRAFT_13.getByteValue(), 1,
                new byte[] { TokenBindingKeyParameters.ECDSAP256.getValue() })));
    }
}
