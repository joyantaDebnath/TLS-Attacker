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

import de.rub.nds.tlsattacker.core.connection.OutboundConnection;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import org.junit.jupiter.api.Test;

import java.util.Set;

abstract class AbstractCopyActionTest<T extends CopyContextFieldAction> extends AbstractActionTest<T> {

    protected final TlsContext src;

    protected final TlsContext dst;

    AbstractCopyActionTest(T action, Class<T> actionClass) {
        super(action, actionClass);
        src = state.getTlsContext("src");
        dst = state.getTlsContext("dst");
    }

    @Override
    protected void createWorkflowTraceAndState() {
        trace = new WorkflowTrace();
        trace.addTlsAction(action);
        trace.addConnection(new OutboundConnection("src"));
        trace.addConnection(new OutboundConnection("dst"));
        state = new State(config, trace);
    }

    @Override
    public void testMarshalingEmptyActionYieldsMinimalOutput() {
    }

    @Test
    public void testGetSrcContextAlias() {
        assertEquals("src", action.getSrcContextAlias());
    }

    @Test
    public void testGetDstContextAlias() {
        assertEquals("dst", action.getDstContextAlias());
    }

    @Test
    public void testGetAllAliases() {
        Set<String> expected = Set.of("dst", "src");
        assertEquals(expected, action.getAllAliases());
    }

    @Test
    public abstract void testAliasesSetProperlyErrorSrc();

    @Test
    public abstract void testAliasesSetProperlyErrorDst();
}
