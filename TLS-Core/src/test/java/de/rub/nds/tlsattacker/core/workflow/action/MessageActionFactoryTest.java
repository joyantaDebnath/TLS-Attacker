/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.connection.AliasedConnection;
import de.rub.nds.tlsattacker.core.connection.InboundConnection;
import de.rub.nds.tlsattacker.core.connection.OutboundConnection;
import de.rub.nds.tlsattacker.core.protocol.ProtocolMessage;
import de.rub.nds.tlsattacker.core.protocol.message.AlertMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ChangeCipherSpecMessage;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class MessageActionFactoryTest {

    private Config config;
    private AliasedConnection clientConnection;
    private AliasedConnection serverConnection;

    @BeforeEach
    public void setUp() {
        config = Config.createConfig();
        clientConnection = new OutboundConnection();
        serverConnection = new InboundConnection();
    }

    /**
     * Test of createAction method, of class MessageActionFactory.
     */
    @Test
    public void testCreateActionOne() {
        MessageAction action = MessageActionFactory.createAction(config, clientConnection, ConnectionEndType.CLIENT,
            new AlertMessage(config));
        assertEquals(SendAction.class, action.getClass());
        action = MessageActionFactory.createAction(config, clientConnection, ConnectionEndType.SERVER,
            new AlertMessage(config));
        assertEquals(ReceiveAction.class, action.getClass());
        action = MessageActionFactory.createAction(config, serverConnection, ConnectionEndType.CLIENT,
            new AlertMessage(config));
        assertEquals(ReceiveAction.class, action.getClass());
        action = MessageActionFactory.createAction(config, serverConnection, ConnectionEndType.SERVER,
            new AlertMessage(config));
        assertEquals(SendAction.class, action.getClass());
        assertEquals(1, action.messages.size());
    }

    /**
     * Test of createAction method, of class MessageActionFactory.
     */
    @Test
    public void testCreateActionMultiple() {
        List<ProtocolMessage> messages = new LinkedList<>();
        messages.add(new ChangeCipherSpecMessage());
        messages.add(new AlertMessage(config));
        MessageAction action =
            MessageActionFactory.createAction(config, clientConnection, ConnectionEndType.CLIENT, messages);
        assertEquals(SendAction.class, action.getClass());
        action = MessageActionFactory.createAction(config, clientConnection, ConnectionEndType.SERVER, messages);
        assertEquals(ReceiveAction.class, action.getClass());
        action = MessageActionFactory.createAction(config, serverConnection, ConnectionEndType.CLIENT, messages);
        assertEquals(ReceiveAction.class, action.getClass());
        action = MessageActionFactory.createAction(config, serverConnection, ConnectionEndType.SERVER, messages);
        assertEquals(SendAction.class, action.getClass());
        assertEquals(2, action.messages.size());
    }

    /**
     * Test of createAsciiAction method, of class MessageActionFactory.
     */
    @Test
    public void testCreateAsciiAction() {
        AsciiAction action = MessageActionFactory.createAsciiAction(clientConnection, ConnectionEndType.CLIENT, "", "");
        assertEquals(SendAsciiAction.class, action.getClass());
        action = MessageActionFactory.createAsciiAction(clientConnection, ConnectionEndType.SERVER, "", "");
        assertEquals(GenericReceiveAsciiAction.class, action.getClass());
        action = MessageActionFactory.createAsciiAction(serverConnection, ConnectionEndType.CLIENT, "", "");
        assertEquals(GenericReceiveAsciiAction.class, action.getClass());
        action = MessageActionFactory.createAsciiAction(serverConnection, ConnectionEndType.SERVER, "", "");
        assertEquals(SendAsciiAction.class, action.getClass());
    }
}
