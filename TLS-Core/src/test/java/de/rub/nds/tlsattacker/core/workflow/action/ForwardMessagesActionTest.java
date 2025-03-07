/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.tlsattacker.core.connection.InboundConnection;
import de.rub.nds.tlsattacker.core.connection.OutboundConnection;
import de.rub.nds.tlsattacker.core.constants.AlertDescription;
import de.rub.nds.tlsattacker.core.constants.AlertLevel;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.exceptions.WorkflowExecutionException;
import de.rub.nds.tlsattacker.core.protocol.ProtocolMessage;
import de.rub.nds.tlsattacker.core.protocol.message.AlertMessage;
import de.rub.nds.tlsattacker.core.protocol.message.ApplicationMessage;
import de.rub.nds.tlsattacker.core.record.layer.TlsRecordLayer;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.core.unittest.helper.FakeTransportHandler;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTraceSerializer;
import de.rub.nds.tlsattacker.core.workflow.action.executor.FakeReceiveMessageHelper;
import de.rub.nds.tlsattacker.core.workflow.filter.DefaultFilter;
import de.rub.nds.tlsattacker.core.workflow.filter.Filter;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ForwardMessagesActionTest extends AbstractActionTest<ForwardMessagesAction> {

    private static final String ctx1Alias = "ctx1";
    private static final String ctx2Alias = "ctx2";
    private final AlertMessage alert;

    public ForwardMessagesActionTest() {
        super(new ForwardMessagesAction(ctx1Alias, ctx2Alias), ForwardMessagesAction.class);

        alert = new AlertMessage(config);
        alert.setConfig(AlertLevel.FATAL, AlertDescription.DECRYPT_ERROR);
        alert.setDescription(AlertDescription.DECODE_ERROR.getValue());
        alert.setLevel(AlertLevel.FATAL.getValue());

        TlsContext ctx1 = state.getTlsContext(ctx1Alias);
        TlsContext ctx2 = state.getTlsContext(ctx2Alias);

        FakeTransportHandler th = new FakeTransportHandler(ConnectionEndType.SERVER);
        byte[] alertMsg = new byte[] { 0x15, 0x03, 0x03, 0x00, 0x02, 0x02, 50 };
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
    @Override
    public void testExecute() throws Exception {
        action.setMessages(alert);
        super.testExecute();
    }

    @Test
    public void testExecuteWithNullAliasThrowsException() {
        final ForwardMessagesAction action1 = new ForwardMessagesAction(null, ctx2Alias, alert);
        WorkflowExecutionException exception =
            assertThrows(WorkflowExecutionException.class, () -> action1.execute(state));
        assertTrue(exception.getMessage()
            .startsWith("Can't execute ForwardMessagesAction with empty receive alias (if using XML: add <from/>"));

        final ForwardMessagesAction action2 = new ForwardMessagesAction(ctx1Alias, null, alert);
        exception = assertThrows(WorkflowExecutionException.class, () -> action2.execute(state));
        assertTrue(exception.getMessage()
            .startsWith("Can't execute ForwardMessagesAction with empty forward alias (if using XML: add <to/>"));
    }

    @Test
    public void testExecuteWithEmptyAliasThrowsException() throws Exception {
        final ForwardMessagesAction action1 = new ForwardMessagesAction("", ctx2Alias, alert);
        WorkflowExecutionException exception =
            assertThrows(WorkflowExecutionException.class, () -> action1.execute(state));
        assertTrue(exception.getMessage()
            .startsWith("Can't execute ForwardMessagesAction with empty receive alias (if using XML: add <from/>"));

        final ForwardMessagesAction action2 = new ForwardMessagesAction(ctx1Alias, "", alert);
        exception = assertThrows(WorkflowExecutionException.class, () -> action2.execute(state));
        assertTrue(exception.getMessage()
            .startsWith("Can't execute ForwardMessagesAction with empty forward alias (if using XML: add <to/>"));
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
            pw.println("    <ForwardMessages>");
            pw.println("        <actionOptions/>");
            pw.println("        <from>ctx1</from>");
            pw.println("        <to>ctx2</to>");
            pw.println("    </ForwardMessages>");
            pw.println("</workflowTrace>");
        }
        String expected = sw.toString();

        Filter filter = new DefaultFilter(config);
        filter.applyFilter(trace);
        filter.postFilter(trace, state.getOriginalWorkflowTrace());
        String actual = WorkflowTraceSerializer.write(trace);

        assertEquals(expected, actual);
    }

    @Test
    public void testForwardApplicationMessageAdoptsData() throws Exception {
        ApplicationMessage msg = new ApplicationMessage(state.getConfig());
        String receivedData = "Forward application message test";
        msg.setData(receivedData.getBytes());
        msg.setCompleteResultingMessage(receivedData.getBytes());
        List<ProtocolMessage> receivedMsgs = new ArrayList<>();
        receivedMsgs.add(msg);
        FakeReceiveMessageHelper fakeReceiveHelper = new FakeReceiveMessageHelper();
        fakeReceiveHelper.setMessagesToReturn(receivedMsgs);

        ForwardMessagesAction action = new ForwardMessagesAction(ctx1Alias, ctx2Alias, fakeReceiveHelper);
        action.setMessages(new ApplicationMessage());

        action.execute(state);
        assertTrue(action.isExecuted());
        assertTrue(action.executedAsPlanned());

        ProtocolMessage forwardedMsgRaw = action.getSendMessages().get(0);
        assertEquals("APPLICATION", forwardedMsgRaw.toCompactString());

        ApplicationMessage forwardedMsg = (ApplicationMessage) forwardedMsgRaw;
        String forwardedData = new String(forwardedMsg.getData().getValue());
        assertEquals(receivedData, forwardedData);
    }
}
