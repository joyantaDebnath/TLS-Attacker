/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.SSL2ClientHelloMessage;
import de.rub.nds.tlsattacker.core.protocol.message.SSL2HandshakeMessage;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SSL2ClientHelloParserTest
    extends AbstractHandshakeMessageParserTest<SSL2ClientHelloMessage, SSL2ClientHelloParser> {

    public SSL2ClientHelloParserTest() {
        super(SSL2ClientHelloParser::new,
            List.of(Named.of("SSL2HandshakeMessage::getMessageLength", SSL2HandshakeMessage::getMessageLength),
                Named.of("SSL2ClientHelloMessage::getProtocolVersion", SSL2ClientHelloMessage::getProtocolVersion),
                Named.of("SSL2ClientHelloMessage::getCipherSuiteLength", SSL2ClientHelloMessage::getCipherSuiteLength),
                Named.of("SSL2ClientHelloMessage::getSessionIdLength", SSL2ClientHelloMessage::getSessionIdLength),
                Named.of("SSL2ClientHelloMessage::getChallengeLength", SSL2ClientHelloMessage::getChallengeLength),
                Named.of("SSL2ClientHelloMessage::getCipherSuites", SSL2ClientHelloMessage::getCipherSuites),
                Named.of("SSL2ClientHelloMessage::getSessionId", SSL2ClientHelloMessage::getSessionId),
                Named.of("SSL2ClientHelloMessage::getChallenge", SSL2ClientHelloMessage::getChallenge)));
    }

    public static Stream<Arguments> provideTestVectors() {
        return Stream.of(Arguments.of(ProtocolVersion.SSL2,
            ArrayConverter.hexStringToByteArray(
                "802b0100020012000000100100800700c0030080060040020080040080bc4c7de14f6fc8bff4428f159fb24f2b"),
            Arrays.asList(HandshakeMessageType.CLIENT_HELLO.getValue(), null, 43, ProtocolVersion.SSL2.getValue(), 18,
                0, 16, ArrayConverter.hexStringToByteArray("0100800700c0030080060040020080040080"), new byte[0],
                ArrayConverter.hexStringToByteArray("bc4c7de14f6fc8bff4428f159fb24f2b"))));
    }
}
