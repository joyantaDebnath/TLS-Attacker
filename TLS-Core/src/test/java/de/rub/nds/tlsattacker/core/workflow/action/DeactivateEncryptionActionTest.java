/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.record.layer.TlsRecordLayer;
import de.rub.nds.tlsattacker.core.state.TlsContext;

public class DeactivateEncryptionActionTest extends AbstractActionTest<DeactivateEncryptionAction> {

    public DeactivateEncryptionActionTest() {
        super(new DeactivateEncryptionAction(), DeactivateEncryptionAction.class);
        TlsContext context = state.getTlsContext();
        context.setSelectedCipherSuite(CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA);
        context.setRecordLayer(new TlsRecordLayer(context));
    }

    // TODO: Override testExecute and check that decryption gets disabled
}
