/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.UserMappingExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.UserMappingExtensionParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class UserMappingExtensionSerializerTest
    extends AbstractExtensionMessageSerializerTest<UserMappingExtensionMessage, UserMappingExtensionSerializer> {

    public UserMappingExtensionSerializerTest() {
        super(UserMappingExtensionMessage::new, UserMappingExtensionSerializer::new,
            List.of((msg, obj) -> msg.setUserMappingType((Byte) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return UserMappingExtensionParserTest.provideTestVectors();
    }
}
