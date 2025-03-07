/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.preparator.extension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.rub.nds.tlsattacker.core.constants.CertificateType;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.protocol.message.extension.ServerCertificateTypeExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.serializer.extension.ServerCertificateTypeExtensionSerializer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class ServerCertificateTypeExtensionPreparatorTest
    extends AbstractExtensionMessagePreparatorTest<ServerCertificateTypeExtensionMessage,
        ServerCertificateTypeExtensionSerializer, ServerCertificateTypeExtensionPreparator> {

    public ServerCertificateTypeExtensionPreparatorTest() {
        super(ServerCertificateTypeExtensionMessage::new, ServerCertificateTypeExtensionMessage::new,
            ServerCertificateTypeExtensionSerializer::new, ServerCertificateTypeExtensionPreparator::new);
    }

    @Test
    @Override
    public void testPrepare() {
        List<CertificateType> certList = Arrays.asList(CertificateType.OPEN_PGP, CertificateType.X509);
        context.getConfig().setServerCertificateTypeDesiredTypes(certList);

        preparator.prepare();

        assertArrayEquals(ExtensionType.SERVER_CERTIFICATE_TYPE.getValue(), message.getExtensionType().getValue());
        assertEquals(3, message.getExtensionLength().getValue());
        assertArrayEquals(CertificateType.toByteArray(certList), message.getCertificateTypes().getValue());
        assertEquals(2, message.getCertificateTypesLength().getValue());
    }

}
