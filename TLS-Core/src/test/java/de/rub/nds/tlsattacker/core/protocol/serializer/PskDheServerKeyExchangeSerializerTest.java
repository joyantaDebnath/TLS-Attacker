/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer;

import de.rub.nds.tlsattacker.core.protocol.message.PskDheServerKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.PskDheServerKeyExchangeParserTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class PskDheServerKeyExchangeSerializerTest
    extends AbstractHandshakeMessageSerializerTest<PskDheServerKeyExchangeMessage, PskDheServerKeyExchangeSerializer> {

    public PskDheServerKeyExchangeSerializerTest() {
        super(PskDheServerKeyExchangeMessage::new, PskDheServerKeyExchangeSerializer::new,
            List.of((msg, obj) -> msg.setIdentityHintLength((Integer) obj),
                (msg, obj) -> msg.setIdentityHint((byte[]) obj), (msg, obj) -> msg.setModulusLength((Integer) obj),
                (msg, obj) -> msg.setModulus((byte[]) obj), (msg, obj) -> msg.setGeneratorLength((Integer) obj),
                (msg, obj) -> msg.setGenerator((byte[]) obj), (msg, obj) -> msg.setPublicKeyLength((Integer) obj),
                (msg, obj) -> msg.setPublicKey((byte[]) obj),
                (msg, obj) -> msg.setSignatureAndHashAlgorithm((byte[]) obj)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return PskDheServerKeyExchangeParserTest.provideTestVectors();
    }
}
