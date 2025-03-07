/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.parser.extension;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.ExtensionByteLength;
import de.rub.nds.tlsattacker.core.protocol.message.extension.TokenBindingExtensionMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TokenBindingExtensionParser extends ExtensionParser<TokenBindingExtensionMessage> {

    private static final Logger LOGGER = LogManager.getLogger();

    public TokenBindingExtensionParser(int startposition, byte[] array, Config config) {
        super(startposition, array, config);
    }

    @Override
    public void parseExtensionMessageContent(TokenBindingExtensionMessage msg) {
        msg.setTokenBindingVersion(parseByteArrayField(ExtensionByteLength.TOKENBINDING_VERSION));
        LOGGER
            .debug("The token binding extension parser parsed the version: " + msg.getTokenBindingVersion().toString());
        msg.setParameterListLength(parseByteField(ExtensionByteLength.TOKENBINDING_KEYPARAMETER_LENGTH));
        LOGGER.debug(
            "The token binding extension parser parsed the KeyParameterLength : " + msg.getParameterListLength());
        msg.setTokenBindingKeyParameters(parseByteArrayField(msg.getParameterListLength().getValue()));
        LOGGER.debug("The token binding extension parser parsed the KeyParameters : "
            + msg.getTokenBindingKeyParameters().toString());
    }

    @Override
    protected TokenBindingExtensionMessage createExtensionMessage() {
        return new TokenBindingExtensionMessage();
    }

}
