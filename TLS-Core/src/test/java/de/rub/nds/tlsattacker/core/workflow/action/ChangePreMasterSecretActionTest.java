/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

public class ChangePreMasterSecretActionTest extends AbstractChangeActionTest<ChangePreMasterSecretAction> {

    public ChangePreMasterSecretActionTest() {
        super(new ChangePreMasterSecretAction(new byte[] { 0, 1 }), ChangePreMasterSecretAction.class);
    }

    /**
     * Test of setNewValue method, of class ChangeClientRandomAction.
     */
    @Test
    @Override
    public void testSetNewValue() {
        assertArrayEquals(action.getNewValue(), new byte[] { 0, 1 });
        action.setNewValue(new byte[] { 0 });
        assertArrayEquals(action.getNewValue(), new byte[] { 0 });
    }

    /**
     * Test of getNewValue method, of class ChangeClientRandomAction.
     */
    @Test
    @Override
    public void testGetNewValue() {
        assertArrayEquals(action.getNewValue(), new byte[] { 0, 1 });
    }

    /**
     * Test of getOldValue method, of class ChangeClientRandomAction.
     */
    @Test
    @Override
    public void testGetOldValue() {
        context.setPreMasterSecret(new byte[] { 3 });
        action.execute(state);
        assertArrayEquals(action.getOldValue(), new byte[] { 3 });
    }

    /**
     * Test of execute method, of class ChangeClientRandomAction.
     */
    @Test
    @Override
    public void testExecute() throws Exception {
        context.setPreMasterSecret(new byte[] { 3 });
        super.testExecute();
        assertArrayEquals(action.getOldValue(), new byte[] { 3 });
        assertArrayEquals(action.getNewValue(), new byte[] { 0, 1 });
        assertArrayEquals(context.getPreMasterSecret(), new byte[] { 0, 1 });

    }
}
