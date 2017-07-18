/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.tlsattacker.core.constants.HandshakeByteLength;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.exceptions.AdjustmentException;
import de.rub.nds.tlsattacker.core.protocol.message.extension.SupportedVersionsExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.parser.extension.SupportedVersionsExtensionParser;
import de.rub.nds.tlsattacker.core.protocol.preparator.extension.SupportedVersionsExtensionPreparator;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.SupportedVersionsExtensionSerializer;
import de.rub.nds.tlsattacker.core.workflow.TlsContext;
import de.rub.nds.tlsattacker.core.workflow.chooser.DefaultChooser;
import java.util.List;

/**
 * This handler processes the SupportedVersions extensions, as defined in
 * https://tools.ietf.org/html/draft-ietf-tls-tls13-21#section-4.2.1
 * 
 * @author Nurullah Erinola <nurullah.erinola@rub.de>
 */
public class SupportedVersionsExtensionHandler extends ExtensionHandler<SupportedVersionsExtensionMessage> {

    public SupportedVersionsExtensionHandler(TlsContext context) {
        super(context);
    }

    @Override
    public SupportedVersionsExtensionParser getParser(byte[] message, int pointer) {
        return new SupportedVersionsExtensionParser(pointer, message);
    }

    @Override
    public SupportedVersionsExtensionPreparator getPreparator(SupportedVersionsExtensionMessage message) {
        return new SupportedVersionsExtensionPreparator(context.getChooser(), message);
    }

    @Override
    public SupportedVersionsExtensionSerializer getSerializer(SupportedVersionsExtensionMessage message) {
        return new SupportedVersionsExtensionSerializer(message);
    }

    @Override
    public void adjustTLSContext(SupportedVersionsExtensionMessage message) {
        byte[] versionBytes = message.getSupportedVersions().getValue();
        if (versionBytes.length % HandshakeByteLength.VERSION != 0) {
            throw new AdjustmentException("Could not create resonable ProtocolVersions from VersionBytes");
        }
        List<ProtocolVersion> versionList = ProtocolVersion.getProtocolVersions(versionBytes);
        context.setClientSupportedProtocolVersions(versionList);
        context.setHighestClientProtocolVersion(ProtocolVersion.getHighestProtocolVersion(versionList));
    }

}
