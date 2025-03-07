/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import de.rub.nds.tlsattacker.core.protocol.ProtocolMessage;
import de.rub.nds.tlsattacker.core.workflow.action.executor.ReceiveMessageHelper;
import java.util.List;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
public class ForwardMessagesWithPrepareAction extends ForwardMessagesAction {

    private static final Logger LOGGER = LogManager.getLogger();

    public ForwardMessagesWithPrepareAction() {
        withPrepare = true;
        receiveMessageHelper = new ReceiveMessageHelper();
    }

    public ForwardMessagesWithPrepareAction(String receiveFromAlias, String forwardToAlias) {
        this(receiveFromAlias, forwardToAlias, new ReceiveMessageHelper());
        withPrepare = true;
    }

    /**
     * Allow to pass a fake ReceiveMessageHelper helper for testing.
     */
    protected ForwardMessagesWithPrepareAction(String receiveFromAlias, String forwardToAlias,
        ReceiveMessageHelper receiveMessageHelper) {
        super(receiveFromAlias, forwardToAlias, receiveMessageHelper);
        withPrepare = true;
    }

    public ForwardMessagesWithPrepareAction(String receiveFromAlias, String forwardToAlias,
        List<ProtocolMessage> messages) {
        super(receiveFromAlias, forwardToAlias, messages);
        withPrepare = true;
    }

    public ForwardMessagesWithPrepareAction(String receiveFromAlias, String forwardToAlias,
        ProtocolMessage... messages) {
        super(receiveFromAlias, forwardToAlias, messages);
        withPrepare = true;
    }

}
