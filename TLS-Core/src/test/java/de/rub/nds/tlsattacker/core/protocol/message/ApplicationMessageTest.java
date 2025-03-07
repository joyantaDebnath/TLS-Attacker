/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.message;

import org.junit.jupiter.params.provider.Arguments;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class ApplicationMessageTest extends AbstractMessageTest<ApplicationMessage> {

    public ApplicationMessageTest() {
        super(ApplicationMessage::new, "ApplicationMessage:\n" + "  Data: %s");
    }

    public static Stream<Arguments> provideToStringTestVectors() {
        BiConsumer<ApplicationMessage, Object[]> messagePreparator = (message, values) -> {
            message.setData((byte[]) values[0]);
        };
        return Stream.of(Arguments.of(new Object[] { null }, null),
            Arguments.of(new Object[] { new byte[] { 123 } }, messagePreparator));
    }
}
