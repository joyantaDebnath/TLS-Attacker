/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.TokenBindingKeyParameters;
import de.rub.nds.tlsattacker.core.constants.TokenBindingVersion;
import de.rub.nds.tlsattacker.core.protocol.message.extension.TokenBindingExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.TokenBindingExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.TokenBindingExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.TokenBindingExtensionSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.util.ArrayList;

public class TokenBindingExtensionHandler extends ExtensionHandler<TokenBindingExtensionMessage> {

    public TokenBindingExtensionHandler(TlsContext context) {
        super(context);
    }

    @Override
    public TokenBindingExtensionParser getParser(byte[] message, int pointer, Config config) {
        return new TokenBindingExtensionParser(pointer, message, config);
    }

    @Override
    public TokenBindingExtensionPreparator getPreparator(TokenBindingExtensionMessage message) {
        return new TokenBindingExtensionPreparator(context.getChooser(), message, getSerializer(message));
    }

    @Override
    public TokenBindingExtensionSerializer getSerializer(TokenBindingExtensionMessage message) {
        return new TokenBindingExtensionSerializer(message);
    }

    @Override
    public void adjustTLSExtensionContext(TokenBindingExtensionMessage message) {
        context
            .setTokenBindingVersion(TokenBindingVersion.getExtensionType(message.getTokenBindingVersion().getValue()));
        ArrayList<TokenBindingKeyParameters> tokenbindingKeyParameters = new ArrayList<>();
        for (byte kp : message.getTokenBindingKeyParameters().getValue()) {
            tokenbindingKeyParameters.add(TokenBindingKeyParameters.getTokenBindingKeyParameter(kp));
        }
        context.setTokenBindingKeyParameters(tokenbindingKeyParameters);
        if (context.getTalkingConnectionEndType() == context.getChooser().getMyConnectionPeer()) {
            context.setTokenBindingNegotiatedSuccessfully(true);
        }
    }
}
