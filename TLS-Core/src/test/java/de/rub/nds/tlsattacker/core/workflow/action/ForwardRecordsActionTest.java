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
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.rub.nds.tlsattacker.core.connection.InboundConnection;
import de.rub.nds.tlsattacker.core.connection.OutboundConnection;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.exceptions.WorkflowExecutionException;
import de.rub.nds.tlsattacker.core.record.layer.TlsRecordLayer;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.core.unittest.helper.FakeTransportHandler;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTraceSerializer;
import de.rub.nds.tlsattacker.core.workflow.filter.DefaultFilter;
import de.rub.nds.tlsattacker.core.workflow.filter.Filter;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ForwardRecordsActionTest extends AbstractActionTest<ForwardRecordsAction> {

    private static final String ctx1Alias = "ctx1";
    private static final String ctx2Alias = "ctx2";

    public ForwardRecordsActionTest() {
        super(new ForwardRecordsAction(ctx1Alias, ctx2Alias), ForwardRecordsAction.class);

        TlsContext ctx1 = state.getTlsContext(ctx1Alias);
        TlsContext ctx2 = state.getTlsContext(ctx2Alias);

        FakeTransportHandler th = new FakeTransportHandler(ConnectionEndType.SERVER);
        byte[] alertMsg = new byte[] { 0x15, 0x03, 0x03, 0x00, 0x02, 0x02, 0x32 };
        th.setFetchableByte(alertMsg);
        ctx1.setSelectedCipherSuite(CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA);
        ctx1.setRecordLayer(new TlsRecordLayer(ctx1));
        ctx1.setTransportHandler(th);

        ctx2.setSelectedCipherSuite(CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA);
        ctx2.setRecordLayer(new TlsRecordLayer(ctx2));
        ctx2.setTransportHandler(new FakeTransportHandler(ConnectionEndType.CLIENT));
    }

    @Override
    protected void createWorkflowTraceAndState() {
        trace = new WorkflowTrace();
        trace.addTlsAction(action);
        trace.addConnection(new OutboundConnection(ctx1Alias));
        trace.addConnection(new InboundConnection(ctx2Alias));
        state = new State(config, trace);
    }

    @Test
    public void executingWithNullAliasThrowsException() throws Exception {
        ForwardRecordsAction action = new ForwardRecordsAction(null, ctx2Alias);
        assertThrows(WorkflowExecutionException.class, () -> action.execute(state));
    }

    @Test
    public void executingWithEmptyAliasThrowsException() throws Exception {
        ForwardRecordsAction action = new ForwardRecordsAction("", ctx2Alias);
        assertThrows(WorkflowExecutionException.class, () -> action.execute(state));
    }

    @Test
    @Override
    public void testMarshalingEmptyActionYieldsMinimalOutput() throws JAXBException, IOException {
        // used PrintWriter and not StringBuilder as it offers
        // OS-independent functionality for printing new lines
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            pw.println("<workflowTrace>");
            pw.println("    <OutboundConnection>");
            pw.println("        <alias>ctx1</alias>");
            pw.println("    </OutboundConnection>");
            pw.println("    <InboundConnection>");
            pw.println("        <alias>ctx2</alias>");
            pw.println("    </InboundConnection>");
            pw.println("    <ForwardRecords>");
            pw.println("        <actionOptions/>");
            pw.println("        <from>ctx1</from>");
            pw.println("        <to>ctx2</to>");
            pw.println("    </ForwardRecords>");
            pw.println("</workflowTrace>");
        }
        String expected = sw.toString();

        Filter filter = new DefaultFilter(config);
        filter.applyFilter(trace);
        filter.postFilter(trace, state.getOriginalWorkflowTrace());
        String actual = WorkflowTraceSerializer.write(trace);
        assertEquals(expected, actual);
    }
}
