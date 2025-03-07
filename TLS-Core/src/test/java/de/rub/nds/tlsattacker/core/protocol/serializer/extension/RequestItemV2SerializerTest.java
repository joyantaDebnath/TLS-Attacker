/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.serializer.extension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import de.rub.nds.tlsattacker.core.protocol.message.extension.statusrequestv2.RequestItemV2;
import de.rub.nds.tlsattacker.core.protocol.message.extension.statusrequestv2.ResponderId;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.RequestItemV2ParserTest;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.RequestItemV2Preparator;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.ResponderIdPreparator;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class RequestItemV2SerializerTest {

    private TlsContext context;

    @BeforeEach
    public void setUp() {
        context = new TlsContext();
    }

    public static Stream<Arguments> provideTestVectors() {
        return RequestItemV2ParserTest.provideTestVectors();
    }

    @ParameterizedTest
    @MethodSource("provideTestVectors")
    public void testSerialize(byte[] expectedRequestItemV2Bytes, int providedRequestType, int providedRequestLength,
        int providedResponderListLength, byte[] providedResponderIdListBytes, List<ResponderId> providedResponderIdList,
        int providedRequestExtensionsLength, byte[] providedRequestExtensions) {
        RequestItemV2 item = new RequestItemV2(providedRequestType, providedRequestLength, providedResponderListLength,
            providedRequestExtensionsLength, providedRequestExtensions);
        for (ResponderId id : providedResponderIdList) {
            new ResponderIdPreparator(context.getChooser(), id).prepare();
        }
        new RequestItemV2Preparator(context.getChooser(), item).prepare();
        item.setResponderIdList(providedResponderIdList);
        item.setResponderIdListBytes(providedResponderIdListBytes);
        byte[] actualBytes = new RequestItemV2Serializer(item).serialize();
        assertArrayEquals(expectedRequestItemV2Bytes, actualBytes);
    }
}
