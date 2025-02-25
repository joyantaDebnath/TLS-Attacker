/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import de.rub.nds.tlsattacker.core.protocol.message.extension.TrustedCaIndicationExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.trustedauthority.TrustedAuthority;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.TrustedCaIndicationExtensionParserTest;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.TrustedAuthorityPreparator;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class TrustedCaIndicationExtensionSerializerTest extends AbstractExtensionMessageSerializerTest<
    TrustedCaIndicationExtensionMessage, TrustedCaIndicationExtensionSerializer> {

    private TlsContext context;

    public TrustedCaIndicationExtensionSerializerTest() {
        // noinspection unchecked
        super(TrustedCaIndicationExtensionMessage::new, TrustedCaIndicationExtensionSerializer::new,
            List.of((msg, obj) -> msg.setTrustedAuthoritiesLength((Integer) obj),
                (msg, obj) -> msg.setTrustedAuthorities((List<TrustedAuthority>) obj)));
        context = new TlsContext();
    }

    public static Stream<Arguments> provideTestVectors() {
        return TrustedCaIndicationExtensionParserTest.provideTestVectors();
    }

    @Override
    protected void setExtensionMessageSpecific(List<Object> providedAdditionalValues,
        List<Object> providedMessageSpecificValues) {
        @SuppressWarnings("unchecked")
        List<TrustedAuthority> trustedAuthorities = (List<TrustedAuthority>) providedMessageSpecificValues.get(1);
        for (TrustedAuthority trustedAuthority : trustedAuthorities) {
            new TrustedAuthorityPreparator(context.getChooser(), trustedAuthority).prepare();
        }
        super.setExtensionMessageSpecific(providedAdditionalValues, providedMessageSpecificValues);
    }
}
