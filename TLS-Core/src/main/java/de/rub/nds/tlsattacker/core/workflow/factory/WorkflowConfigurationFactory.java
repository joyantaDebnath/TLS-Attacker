/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.workflow.factory;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.connection.AliasedConnection;
import de.rub.nds.tlsattacker.core.constants.AlertDescription;
import de.rub.nds.tlsattacker.core.constants.AlertLevel;
import de.rub.nds.tlsattacker.core.constants.AlgorithmResolver;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.ExtensionType;
import de.rub.nds.tlsattacker.core.constants.HandshakeMessageType;
import de.rub.nds.tlsattacker.core.constants.KeyExchangeAlgorithm;
import de.rub.nds.tlsattacker.core.constants.RunningModeType;
import de.rub.nds.tlsattacker.core.constants.StarttlsMessage;
import de.rub.nds.tlsattacker.core.constants.StarttlsType;
import de.rub.nds.tlsattacker.core.exceptions.ConfigurationException;
import de.rub.nds.tlsattacker.core.https.HttpsRequestMessage;
import de.rub.nds.tlsattacker.core.https.HttpsResponseMessage;
import de.rub.nds.tlsattacker.core.protocol.ProtocolMessage;
import de.rub.nds.tlsattacker.core.protocol.message.*;
import de.rub.nds.tlsattacker.core.protocol.message.extension.EarlyDataExtensionMessage;
import de.rub.nds.tlsattacker.core.protocol.message.extension.PreSharedKeyExtensionMessage;
import de.rub.nds.tlsattacker.core.record.BlobRecord;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTraceUtil;
import de.rub.nds.tlsattacker.core.workflow.action.BufferedGenericReceiveAction;
import de.rub.nds.tlsattacker.core.workflow.action.BufferedSendAction;
import de.rub.nds.tlsattacker.core.workflow.action.ClearBuffersAction;
import de.rub.nds.tlsattacker.core.workflow.action.CopyBuffersAction;
import de.rub.nds.tlsattacker.core.workflow.action.CopyPreMasterSecretAction;
import de.rub.nds.tlsattacker.core.workflow.action.EsniKeyDnsRequestAction;
import de.rub.nds.tlsattacker.core.workflow.action.FlushSessionCacheAction;
import de.rub.nds.tlsattacker.core.workflow.action.ForwardMessagesAction;
import de.rub.nds.tlsattacker.core.workflow.action.ForwardRecordsAction;
import de.rub.nds.tlsattacker.core.workflow.action.MessageAction;
import de.rub.nds.tlsattacker.core.workflow.action.MessageActionFactory;
import de.rub.nds.tlsattacker.core.workflow.action.PopAndSendAction;
import de.rub.nds.tlsattacker.core.workflow.action.PopBufferedMessageAction;
import de.rub.nds.tlsattacker.core.workflow.action.PopBufferedRecordAction;
import de.rub.nds.tlsattacker.core.workflow.action.PopBuffersAction;
import de.rub.nds.tlsattacker.core.workflow.action.PrintLastHandledApplicationDataAction;
import de.rub.nds.tlsattacker.core.workflow.action.PrintSecretsAction;
import de.rub.nds.tlsattacker.core.workflow.action.ReceiveAction;
import de.rub.nds.tlsattacker.core.workflow.action.ReceiveTillAction;
import de.rub.nds.tlsattacker.core.workflow.action.RemBufferedChCiphersAction;
import de.rub.nds.tlsattacker.core.workflow.action.RemBufferedChExtensionsAction;
import de.rub.nds.tlsattacker.core.workflow.action.RenegotiationAction;
import de.rub.nds.tlsattacker.core.workflow.action.ResetConnectionAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendDynamicClientKeyExchangeAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendDynamicServerCertificateAction;
import de.rub.nds.tlsattacker.core.workflow.action.SendDynamicServerKeyExchangeAction;
import de.rub.nds.tlsattacker.core.workflow.action.TlsAction;
import de.rub.nds.tlsattacker.core.workflow.action.executor.ActionOption;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Create a WorkflowTrace based on a Config instance. */
public class WorkflowConfigurationFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final Config config;
    private RunningModeType mode;

    public WorkflowConfigurationFactory(Config config) {
        this.config = config;
    }

    public WorkflowTrace createWorkflowTrace(WorkflowTraceType type, RunningModeType mode) {
        this.mode = mode;
        if (type == null) {
            throw new RuntimeException("Cannot create WorkflowTrace from NULL type");
        }
        switch (type) {
            case HELLO:
                return createHelloWorkflow();
            case FULL:
                return createFullWorkflow();
            case HANDSHAKE:
                return createHandshakeWorkflow();
            case SHORT_HELLO:
                return createShortHelloWorkflow();
            case SSL2_HELLO:
                return createSsl2HelloWorkflow();
            case CLIENT_RENEGOTIATION_WITHOUT_RESUMPTION:
                return createClientRenegotiationWorkflow();
            case CLIENT_RENEGOTIATION:
                return createClientRenegotiationWithResumptionWorkflow();
            case SERVER_RENEGOTIATION:
                return createServerRenegotiationWorkflow();
            case DYNAMIC_CLIENT_RENEGOTIATION_WITHOUT_RESUMPTION:
                return createDynamicClientRenegotiationWithoutResumption();
            case HTTPS:
                return createHttpsWorkflow();
            case RESUMPTION:
                return createResumptionWorkflow();
            case FULL_RESUMPTION:
                return createFullResumptionWorkflow();
            case SIMPLE_MITM_PROXY:
                return createSimpleMitmProxyWorkflow();
            case SIMPLE_FORWARDING_MITM_PROXY:
                return createSimpleForwardingMitmProxyWorkflow();
            case TLS13_PSK:
                return createTls13PskWorkflow(false);
            case FULL_TLS13_PSK:
                return createFullTls13PskWorkflow(false);
            case ZERO_RTT:
                return createTls13PskWorkflow(true);
            case FULL_ZERO_RTT:
                return createFullTls13PskWorkflow(true);
            case FALSE_START:
                return createFalseStartWorkflow();
            case RSA_SYNC_PROXY:
                return createSyncProxyWorkflow();
            case DYNAMIC_HANDSHAKE:
                return createDynamicHandshakeWorkflow();
            case DYNAMIC_HELLO:
                return createDynamicHelloWorkflow();
            case DYNAMIC_HTTPS:
                return createHttpsDynamicWorkflow();
            default:
                throw new ConfigurationException("Unknown WorkflowTraceType " + type.name());
        }
    }

    private AliasedConnection getConnection() {
        AliasedConnection con = null;
        if (mode == null) {
            throw new ConfigurationException("Running mode not set, can't configure workflow");
        } else {
            switch (mode) {
                case CLIENT:
                    con = config.getDefaultClientConnection();
                    break;
                case SERVER:
                    con = config.getDefaultServerConnection();
                    break;
                default:
                    throw new ConfigurationException(
                            "This workflow can only be configured for"
                                    + " modes CLIENT and SERVER, but actual mode was "
                                    + mode);
            }
        }
        return con;
    }

    /**
     * Create an empty - or almost empty workflow trace, depending on the StartTLS flag in the
     * config.
     *
     * @param connection
     * @return An entry workflow trace
     */
    public WorkflowTrace createTlsEntryWorkflowTrace(AliasedConnection connection) {
        WorkflowTrace workflowTrace = new WorkflowTrace();

        if (config.getStarttlsType() != StarttlsType.NONE) {
            addStartTlsActions(connection, config.getStarttlsType(), workflowTrace);
        }

        return workflowTrace;
    }

    /**
     * Create a short hello workflow for the default connection end defined in config.
     *
     * @return A short hello workflow
     */
    private WorkflowTrace createShortHelloWorkflow() {
        return createShortHelloWorkflow(getConnection());
    }

    /**
     * Create a short hello workflow for the given connection end.
     *
     * @param connection
     * @return A short hello workflow
     */
    public WorkflowTrace createShortHelloWorkflow(AliasedConnection connection) {
        WorkflowTrace trace = createTlsEntryWorkflowTrace(connection);

        if (config.isAddEncryptedServerNameIndicationExtension()
                && connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            trace.addTlsAction(new EsniKeyDnsRequestAction());
        }

        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.CLIENT,
                        new ClientHelloMessage(config)));

        if (config.getHighestProtocolVersion().isDTLS() && config.isDtlsCookieExchange()) {
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.SERVER,
                            new HelloVerifyRequestMessage(config)));
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.CLIENT,
                            new ClientHelloMessage(config)));
        }

        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.SERVER,
                        new ServerHelloMessage(config)));

        return trace;
    }

    /**
     * Create a hello workflow for the default connection end defined in config.
     *
     * @return A hello workflow
     */
    private WorkflowTrace createHelloWorkflow() {
        return createHelloWorkflow(getConnection());
    }

    /**
     * Create a hello workflow for the given connection end.
     *
     * @param connection
     * @return A hello workflow
     */
    public WorkflowTrace createHelloWorkflow(AliasedConnection connection) {
        WorkflowTrace trace = createShortHelloWorkflow(connection);
        trace.removeTlsAction(trace.getTlsActions().size() - 1);

        CipherSuite selectedCipherSuite = config.getDefaultSelectedCipherSuite();
        List<ProtocolMessage> messages = new LinkedList<>();
        messages.add(new ServerHelloMessage(config));
        if (config.getHighestProtocolVersion().isTLS13()) {
            if (Objects.equals(config.getTls13BackwardsCompatibilityMode(), Boolean.TRUE)
                    || connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
                ChangeCipherSpecMessage ccs = new ChangeCipherSpecMessage();
                ccs.setRequired(false);
                messages.add(ccs);
            }
            messages.add(new EncryptedExtensionsMessage(config));
            if (Objects.equals(config.isClientAuthentication(), Boolean.TRUE)) {
                messages.add(new CertificateRequestMessage(config));
            }
            if (!selectedCipherSuite.isPWD()) {
                messages.add(new CertificateMessage(config));
                messages.add(new CertificateVerifyMessage(config));
            }
            messages.add(new FinishedMessage(config));
        } else {
            if (selectedCipherSuite.requiresServerCertificateMessage()) {
                messages.add(new CertificateMessage(config));
            }
            addServerKeyExchangeMessage(messages);
            if (Objects.equals(config.isClientAuthentication(), Boolean.TRUE)) {
                messages.add(new CertificateRequestMessage(config));
            }
            messages.add(new ServerHelloDoneMessage(config));
        }
        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config, connection, ConnectionEndType.SERVER, messages));

        return trace;
    }

    /**
     * Create a handshake workflow for the default connection end defined in config.
     *
     * @return A handshake workflow
     */
    private WorkflowTrace createHandshakeWorkflow() {
        return createHandshakeWorkflow(getConnection());
    }

    /**
     * Create a handshake workflow for the given connection end.
     *
     * @param connection
     * @return A handshake workflow
     */
    public WorkflowTrace createHandshakeWorkflow(AliasedConnection connection) {
        WorkflowTrace trace = createHelloWorkflow(connection);

        List<ProtocolMessage> messages = new LinkedList<>();
        if (config.getHighestProtocolVersion().isTLS13()) {
            if (Objects.equals(config.getTls13BackwardsCompatibilityMode(), Boolean.TRUE)
                    || connection.getLocalConnectionEndType() == ConnectionEndType.SERVER) {
                ChangeCipherSpecMessage ccs = new ChangeCipherSpecMessage();
                ccs.setRequired(false);
                messages.add(ccs);
            }
            if (Objects.equals(config.isClientAuthentication(), Boolean.TRUE)) {
                messages.add(new CertificateMessage(config));
                messages.add(new CertificateVerifyMessage(config));
            }
        } else {
            if (Objects.equals(config.isClientAuthentication(), Boolean.TRUE)) {
                messages.add(new CertificateMessage(config));
                addClientKeyExchangeMessage(messages);
                messages.add(new CertificateVerifyMessage(config));
            } else {
                addClientKeyExchangeMessage(messages);
            }
            messages.add(new ChangeCipherSpecMessage(config));
        }
        messages.add(new FinishedMessage(config));
        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config, connection, ConnectionEndType.CLIENT, messages));

        if (!config.getHighestProtocolVersion().isTLS13()) {
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.SERVER,
                            new ChangeCipherSpecMessage(config),
                            new FinishedMessage(config)));
        }

        return trace;
    }

    /**
     * Create a full workflow for the default connection end defined in config.
     *
     * @return A full workflow
     */
    private WorkflowTrace createFullWorkflow() {
        return createFullWorkflow(getConnection());
    }

    /**
     * Create an extended TLS workflow including an application data and heartbeat messages.
     *
     * @param connection
     * @return A full workflow with application messages
     */
    public WorkflowTrace createFullWorkflow(AliasedConnection connection) {
        WorkflowTrace trace = createHandshakeWorkflow(connection);

        if (config.isServerSendsApplicationData()) {
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.SERVER,
                            new ApplicationMessage(config)));
        }

        if (config.isAddHeartbeatExtension()) {
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.CLIENT,
                            new ApplicationMessage(config),
                            new HeartbeatMessage(config)));
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.SERVER,
                            new HeartbeatMessage(config)));
        } else {
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.CLIENT,
                            new ApplicationMessage(config)));
        }

        return trace;
    }

    /** Create a handshake workflow for the default connection end defined in config. */
    private WorkflowTrace createFalseStartWorkflow() {
        return createFalseStartWorkflow(getConnection());
    }

    /** Create a false start workflow for the given connection end. */
    private WorkflowTrace createFalseStartWorkflow(AliasedConnection connection) {

        if (config.getHighestProtocolVersion().isTLS13()) {
            throw new ConfigurationException(
                    "The false start workflow is not implemented for TLS 1.3");
        }

        WorkflowTrace workflowTrace = this.createHandshakeWorkflow(connection);
        MessageAction appData =
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.CLIENT,
                        new ApplicationMessage(config));

        // Client CKE, CCS, Fin
        // TODO weired
        TlsAction lastClientAction;
        if (connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            lastClientAction = (TlsAction) workflowTrace.getLastSendingAction();
        } else {
            lastClientAction = (TlsAction) workflowTrace.getLastReceivingAction();
        }
        int i = workflowTrace.getTlsActions().indexOf(lastClientAction);
        workflowTrace.addTlsAction(i + 1, appData);

        return workflowTrace;
    }

    private WorkflowTrace createSsl2HelloWorkflow() {
        AliasedConnection connection = getConnection();
        WorkflowConfigurationFactory factory = new WorkflowConfigurationFactory(config);
        WorkflowTrace trace =
                factory.createTlsEntryWorkflowTrace(config.getDefaultClientConnection());

        MessageAction action =
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.CLIENT,
                        new SSL2ClientHelloMessage(config));
        action.setRecords(new BlobRecord());
        trace.addTlsAction(action);
        action =
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.SERVER,
                        new SSL2ServerHelloMessage(config));
        action.setRecords(new BlobRecord());
        trace.addTlsAction(action);
        return trace;
    }

    private WorkflowTrace createFullResumptionWorkflow() {
        AliasedConnection conEnd = getConnection();
        WorkflowTrace trace = this.createHandshakeWorkflow(conEnd);
        if (config.getHighestProtocolVersion().isDTLS() && config.isFinishWithCloseNotify()) {
            AlertMessage alert = new AlertMessage();
            alert.setConfig(AlertLevel.WARNING, AlertDescription.CLOSE_NOTIFY);
            trace.addTlsAction(new SendAction(alert));
        }
        trace.addTlsAction(new ResetConnectionAction());
        WorkflowTrace tempTrace = this.createResumptionWorkflow();
        for (TlsAction resumption : tempTrace.getTlsActions()) {
            trace.addTlsAction(resumption);
        }
        return trace;
    }

    /**
     * Create a resumption workflow for the default connection end defined in config. This can be
     * used as the follow up to a normal handshake to test session resumption capabilities.
     *
     * @return A resumption workflow
     */
    private WorkflowTrace createResumptionWorkflow() {
        return createResumptionWorkflow(getConnection());
    }

    /**
     * Create a resumption workflow for the given connection end. This can be used as the follow up
     * to a normal handshake to test session resumption capabilities.
     *
     * @param connection
     * @return A resumption workflow
     */
    public WorkflowTrace createResumptionWorkflow(AliasedConnection connection) {
        WorkflowTrace trace = createTlsEntryWorkflowTrace(connection);

        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.CLIENT,
                        new ClientHelloMessage(config)));

        if (config.getHighestProtocolVersion().isDTLS() && config.isDtlsCookieExchange()) {
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.SERVER,
                            new HelloVerifyRequestMessage(config)));
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.CLIENT,
                            new ClientHelloMessage(config)));
        }

        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.SERVER,
                        new ServerHelloMessage(config),
                        new ChangeCipherSpecMessage(config),
                        new FinishedMessage(config)));
        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.CLIENT,
                        new ChangeCipherSpecMessage(config),
                        new FinishedMessage(config)));

        return trace;
    }

    private WorkflowTrace createClientRenegotiationWithResumptionWorkflow() {
        AliasedConnection conEnd = getConnection();
        WorkflowTrace trace = createHandshakeWorkflow(conEnd);
        trace.addTlsAction(new RenegotiationAction());
        WorkflowTrace renegotiationTrace = createResumptionWorkflow();
        for (TlsAction reneAction : renegotiationTrace.getTlsActions()) {
            if (reneAction.isMessageAction()) { // DO NOT ADD ASCII ACTIONS
                trace.addTlsAction(reneAction);
            }
        }
        return trace;
    }

    private WorkflowTrace createClientRenegotiationWorkflow() {
        AliasedConnection conEnd = getConnection();
        WorkflowTrace trace = createHandshakeWorkflow(conEnd);
        trace.addTlsAction(new RenegotiationAction());
        trace.addTlsAction(new FlushSessionCacheAction());
        WorkflowTrace renegotiationTrace = createHandshakeWorkflow(conEnd);
        for (TlsAction reneAction : renegotiationTrace.getTlsActions()) {
            if (reneAction.isMessageAction()) { // DO NOT ADD ASCII ACTIONS
                trace.addTlsAction(reneAction);
            }
        }
        return trace;
    }

    private WorkflowTrace createServerRenegotiationWorkflow() {
        AliasedConnection connection = getConnection();
        WorkflowTrace trace = createHandshakeWorkflow(connection);
        WorkflowTrace renegotiationTrace = createHandshakeWorkflow(connection);
        trace.addTlsAction(new RenegotiationAction());
        MessageAction action =
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.SERVER,
                        new HelloRequestMessage(config));
        trace.addTlsAction(action);
        for (TlsAction reneAction : renegotiationTrace.getTlsActions()) {
            if (reneAction.isMessageAction()) { // DO NOT ADD ASCII ACTIONS
                trace.addTlsAction(reneAction);
            }
        }
        return trace;
    }

    private WorkflowTrace createHttpsWorkflow() {
        AliasedConnection connection = getConnection();
        WorkflowTrace trace = createHandshakeWorkflow(connection);
        MessageAction action =
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.CLIENT,
                        new HttpsRequestMessage(config));
        trace.addTlsAction(action);
        action =
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.SERVER,
                        new HttpsResponseMessage(config));
        trace.addTlsAction(action);
        return trace;
    }

    private WorkflowTrace createHttpsDynamicWorkflow() {
        AliasedConnection connection = getConnection();
        WorkflowTrace trace = createDynamicHandshakeWorkflow();
        MessageAction action =
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.CLIENT,
                        new HttpsRequestMessage(config));
        trace.addTlsAction(action);
        action =
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.SERVER,
                        new HttpsResponseMessage(config));
        trace.addTlsAction(action);
        return trace;
    }

    private WorkflowTrace createSimpleMitmProxyWorkflow() {

        if (mode != RunningModeType.MITM) {
            throw new ConfigurationException(
                    "This workflow trace can only be created when running"
                            + " in MITM mode. Actual mode: "
                            + mode);
        }

        AliasedConnection inboundConnection = config.getDefaultServerConnection();
        AliasedConnection outboundConnection = config.getDefaultClientConnection();

        if (outboundConnection == null || inboundConnection == null) {
            throw new ConfigurationException("Could not find both necessary connection ends");
        }

        // client -> mitm
        String clientToMitmAlias = inboundConnection.getAlias();
        // mitm -> server
        String mitmToServerAlias = outboundConnection.getAlias();

        LOGGER.debug("Building mitm trace for: " + inboundConnection + ", " + outboundConnection);

        WorkflowTrace clientToMitmHandshake = createHandshakeWorkflow(inboundConnection);
        WorkflowTrace mitmToServerHandshake = createHandshakeWorkflow(outboundConnection);

        WorkflowConfigurationFactory factory = new WorkflowConfigurationFactory(config);
        WorkflowTrace trace =
                factory.createTlsEntryWorkflowTrace(config.getDefaultClientConnection());

        trace.addConnection(inboundConnection);
        trace.addConnection(outboundConnection);
        trace.addTlsActions(clientToMitmHandshake.getTlsActions());
        trace.addTlsActions(mitmToServerHandshake.getTlsActions());

        // Forward request client -> server
        ForwardMessagesAction f =
                new ForwardMessagesAction(
                        clientToMitmAlias, mitmToServerAlias, new ApplicationMessage(config));
        trace.addTlsAction(f);

        // Print client's app data contents
        PrintLastHandledApplicationDataAction p =
                new PrintLastHandledApplicationDataAction(clientToMitmAlias);
        p.setStringEncoding("US-ASCII");
        trace.addTlsAction(p);

        // Forward response server -> client
        f =
                new ForwardMessagesAction(
                        mitmToServerAlias, clientToMitmAlias, new ApplicationMessage(config));
        trace.addTlsAction(f);

        // Print server's app data contents
        p = new PrintLastHandledApplicationDataAction(mitmToServerAlias);
        p.setStringEncoding("US-ASCII");
        trace.addTlsAction(p);

        return trace;
    }

    private WorkflowTrace createSimpleForwardingMitmProxyWorkflow() {

        if (mode != RunningModeType.MITM) {
            throw new ConfigurationException(
                    "This workflow trace can only be created when running"
                            + " in MITM mode. Actual mode: "
                            + mode);
        }

        AliasedConnection inboundConnection = config.getDefaultServerConnection();
        AliasedConnection outboundConnection = config.getDefaultClientConnection();

        if (outboundConnection == null || inboundConnection == null) {
            throw new ConfigurationException("Could not find both necessary connection ends");
        }

        // client -> mitm
        String clientToMitmAlias = inboundConnection.getAlias();
        // mitm -> server
        String mitmToServerAlias = outboundConnection.getAlias();

        LOGGER.debug("Building mitm trace for: " + inboundConnection + ", " + outboundConnection);

        WorkflowConfigurationFactory factory = new WorkflowConfigurationFactory(config);
        WorkflowTrace trace =
                factory.createTlsEntryWorkflowTrace(config.getDefaultClientConnection());

        trace.addConnection(inboundConnection);
        trace.addConnection(outboundConnection);

        // <!-- CH-->
        ForwardRecordsAction forwardRecordsAction =
                new ForwardRecordsAction(clientToMitmAlias, mitmToServerAlias);
        trace.addTlsAction(forwardRecordsAction);

        // <!-- SH, Cert, SHD-->
        ForwardRecordsAction forwardRecordsAction2 =
                new ForwardRecordsAction(mitmToServerAlias, clientToMitmAlias);
        trace.addTlsAction(forwardRecordsAction2);

        // <!-- CKE, CCS, Fin -->
        ForwardRecordsAction forwardRecordsAction3 =
                new ForwardRecordsAction(clientToMitmAlias, mitmToServerAlias);
        trace.addTlsAction(forwardRecordsAction3);

        // <!-- CCS, Fin -->
        ForwardRecordsAction forwardRecordsAction4 =
                new ForwardRecordsAction(mitmToServerAlias, clientToMitmAlias);
        trace.addTlsAction(forwardRecordsAction4);
        return trace;
    }

    private WorkflowTrace createTls13PskWorkflow(boolean zeroRtt) {
        AliasedConnection connection = getConnection();
        ChangeCipherSpecMessage ccsServer = new ChangeCipherSpecMessage();
        ChangeCipherSpecMessage ccsClient = new ChangeCipherSpecMessage();

        if (connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            ccsServer.setRequired(false);
        } else {
            ccsClient.setRequired(false);
        }

        WorkflowConfigurationFactory factory = new WorkflowConfigurationFactory(config);
        WorkflowTrace trace =
                factory.createTlsEntryWorkflowTrace(config.getDefaultClientConnection());

        List<ProtocolMessage> clientHelloMessages = new LinkedList<>();
        List<ProtocolMessage> serverMessages = new LinkedList<>();
        List<ProtocolMessage> clientMessages = new LinkedList<>();

        ClientHelloMessage clientHello;
        ApplicationMessage earlyDataMsg;

        if (connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            clientHello = new ClientHelloMessage(config);
            earlyDataMsg = new ApplicationMessage(config);
            earlyDataMsg.setDataConfig(config.getEarlyData());
        } else {
            clientHello = new ClientHelloMessage();
            earlyDataMsg = new ApplicationMessage();
        }
        clientHelloMessages.add(clientHello);
        if (zeroRtt) {
            if (Objects.equals(config.getTls13BackwardsCompatibilityMode(), Boolean.TRUE)
                    || connection.getLocalConnectionEndType() == ConnectionEndType.SERVER) {
                clientHelloMessages.add(ccsClient);
            }
            clientHelloMessages.add(earlyDataMsg);
        }

        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config, connection, ConnectionEndType.CLIENT, clientHelloMessages));

        ServerHelloMessage serverHello;
        EncryptedExtensionsMessage encExtMsg;
        FinishedMessage serverFin = new FinishedMessage(config);

        if (connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            serverHello = new ServerHelloMessage();
            encExtMsg = new EncryptedExtensionsMessage();
        } else {
            serverHello = new ServerHelloMessage(config);
            encExtMsg = new EncryptedExtensionsMessage(config);
        }
        if (zeroRtt) {
            encExtMsg.addExtension(new EarlyDataExtensionMessage());
        }

        serverMessages.add(serverHello);
        if (Objects.equals(config.getTls13BackwardsCompatibilityMode(), Boolean.TRUE)
                || connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            serverMessages.add(ccsServer);
        }
        if (!zeroRtt
                && (Objects.equals(config.getTls13BackwardsCompatibilityMode(), Boolean.TRUE)
                        || connection.getLocalConnectionEndType() == ConnectionEndType.SERVER)) {
            clientMessages.add(ccsClient);
        }
        serverMessages.add(encExtMsg);
        serverMessages.add(serverFin);

        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config, connection, ConnectionEndType.SERVER, serverMessages));

        if (zeroRtt) {
            clientMessages.add(new EndOfEarlyDataMessage());
        }
        clientMessages.add(new FinishedMessage(config));
        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config, connection, ConnectionEndType.CLIENT, clientMessages));
        return trace;
    }

    private WorkflowTrace createFullTls13PskWorkflow(boolean zeroRtt) {
        AliasedConnection ourConnection = getConnection();
        WorkflowTrace trace = createHandshakeWorkflow();
        // Remove extensions that are only required in the second handshake
        HelloMessage initialHello;
        if (ourConnection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            initialHello =
                    (HelloMessage)
                            WorkflowTraceUtil.getFirstSendMessage(
                                    HandshakeMessageType.CLIENT_HELLO, trace);
            EarlyDataExtensionMessage earlyDataExtension =
                    initialHello.getExtension(EarlyDataExtensionMessage.class);
            if (initialHello.getExtensions() != null) {
                initialHello.getExtensions().remove(earlyDataExtension);
            }
        } else {
            initialHello =
                    (HelloMessage)
                            WorkflowTraceUtil.getFirstSendMessage(
                                    HandshakeMessageType.SERVER_HELLO, trace);
            EncryptedExtensionsMessage encryptedExtensionsMessage =
                    (EncryptedExtensionsMessage)
                            WorkflowTraceUtil.getFirstSendMessage(
                                    HandshakeMessageType.ENCRYPTED_EXTENSIONS, trace);
            if (encryptedExtensionsMessage != null
                    && encryptedExtensionsMessage.getExtensions() != null) {
                EarlyDataExtensionMessage earlyDataExtension =
                        encryptedExtensionsMessage.getExtension(EarlyDataExtensionMessage.class);
                encryptedExtensionsMessage.getExtensions().remove(earlyDataExtension);
            }
        }

        if (initialHello.getExtensions() != null) {
            PreSharedKeyExtensionMessage pskExtension =
                    initialHello.getExtension(PreSharedKeyExtensionMessage.class);
            initialHello.getExtensions().remove(pskExtension);
        }

        MessageAction newSessionTicketAction =
                MessageActionFactory.createAction(
                        config,
                        ourConnection,
                        ConnectionEndType.SERVER,
                        new NewSessionTicketMessage(config, false));
        if (newSessionTicketAction instanceof ReceiveAction) {
            newSessionTicketAction
                    .getActionOptions()
                    .add(ActionOption.IGNORE_UNEXPECTED_NEW_SESSION_TICKETS);
        }
        trace.addTlsAction(newSessionTicketAction);
        trace.addTlsAction(new ResetConnectionAction());
        WorkflowTrace zeroRttTrace = createTls13PskWorkflow(zeroRtt);
        for (TlsAction zeroRttAction : zeroRttTrace.getTlsActions()) {
            trace.addTlsAction(zeroRttAction);
        }
        return trace;
    }

    /**
     * A simple synchronizing proxy for RSA KE.
     *
     * <p>Synchronizes the secrets between all parties and forwards first round of exchanged
     * application data messages.
     *
     * <p>Works only for RSA KE ciphers. Extended Master Secret (and possibly other extensions) will
     * brake it. So per default, all extensions are removed and all cipher suites except RSA suites
     * are removed, too.
     */
    private WorkflowTrace createSyncProxyWorkflow() {

        if (mode != RunningModeType.MITM) {
            throw new ConfigurationException(
                    "This workflow trace can only be created when running"
                            + " in MITM mode. Actual mode: "
                            + mode);
        }

        // client -> mitm
        AliasedConnection inboundConnection = config.getDefaultServerConnection();
        String clientToMitmAlias = inboundConnection.getAlias();
        // mitm -> server
        AliasedConnection outboundConnection = config.getDefaultClientConnection();
        String mitmToServerAlias = outboundConnection.getAlias();

        if (outboundConnection == null || inboundConnection == null) {
            throw new ConfigurationException("Could not find both necessary connection ends");
        }

        LOGGER.info(
                "Building synchronizing proxy trace for:\n"
                        + inboundConnection.toCompactString()
                        + ", "
                        + outboundConnection.toCompactString());

        WorkflowConfigurationFactory factory = new WorkflowConfigurationFactory(config);
        WorkflowTrace trace =
                factory.createTlsEntryWorkflowTrace(config.getDefaultClientConnection());

        trace.addConnection(inboundConnection);
        trace.addConnection(outboundConnection);

        List<CipherSuite> removeCiphers = CipherSuite.getImplemented();
        removeCiphers.addAll(CipherSuite.getNotImplemented());
        List<CipherSuite> keepCiphers = new ArrayList<>();
        for (CipherSuite cs : removeCiphers) {
            if (cs.name().startsWith("TLS_RSA")) {
                keepCiphers.add(cs);
            }
        }
        removeCiphers.removeAll(keepCiphers);

        List<ExtensionType> removeExtensions = ExtensionType.getReceivable();
        List<ExtensionType> keepExtensions = new ArrayList<>();
        // keepExtensions.add(ExtensionType.EXTENDED_MASTER_SECRET);
        removeExtensions.removeAll(keepExtensions);

        // Sorry for fooling the silly formatter with EOL comments :>
        trace.addTlsActions( // Forward CH, remove extensions and non RSA KE ciphers
                new BufferedGenericReceiveAction(clientToMitmAlias), //
                new CopyBuffersAction(clientToMitmAlias, mitmToServerAlias), //
                new RemBufferedChCiphersAction(mitmToServerAlias, removeCiphers), //
                new RemBufferedChExtensionsAction(mitmToServerAlias, removeExtensions), //
                new BufferedSendAction(mitmToServerAlias), //
                new ClearBuffersAction(clientToMitmAlias), //

                // Forward SH
                new BufferedGenericReceiveAction(mitmToServerAlias),
                new CopyBuffersAction(mitmToServerAlias, clientToMitmAlias),
                new PopAndSendAction(clientToMitmAlias), //
                new PrintSecretsAction(clientToMitmAlias), //
                new PrintSecretsAction(mitmToServerAlias), //
                // But send our own certificate
                new PopBufferedMessageAction(clientToMitmAlias), //
                new PopBufferedRecordAction(clientToMitmAlias), //
                new SendAction(clientToMitmAlias, new CertificateMessage(config)), //
                // Send SHD
                new PopAndSendAction(clientToMitmAlias), //
                new ClearBuffersAction(mitmToServerAlias), //

                // Forward CKE (use received PMS)
                new BufferedGenericReceiveAction(clientToMitmAlias), //
                new CopyBuffersAction(clientToMitmAlias, mitmToServerAlias), //
                new PopBuffersAction(mitmToServerAlias), //
                new CopyPreMasterSecretAction(clientToMitmAlias, mitmToServerAlias), //
                new SendAction(mitmToServerAlias, new RSAClientKeyExchangeMessage()), //
                // Sends CCS
                new PopAndSendAction(mitmToServerAlias), //
                new ClearBuffersAction(mitmToServerAlias), //
                new ClearBuffersAction(clientToMitmAlias), //

                // Send fresh FIN
                new SendAction(mitmToServerAlias, new FinishedMessage()), //
                new PrintSecretsAction(clientToMitmAlias), //
                new PrintSecretsAction(mitmToServerAlias), //

                // Finish the handshake, and print the secrets we negotiated
                new ReceiveAction(
                        mitmToServerAlias,
                        new ChangeCipherSpecMessage(),
                        new FinishedMessage()), //
                new PrintSecretsAction(clientToMitmAlias), //
                new PrintSecretsAction(mitmToServerAlias), //
                new SendAction(
                        clientToMitmAlias,
                        new ChangeCipherSpecMessage(),
                        new FinishedMessage()), //

                // Step out, enjoy :)
                new ForwardRecordsAction(clientToMitmAlias, mitmToServerAlias), //
                new ForwardRecordsAction(mitmToServerAlias, clientToMitmAlias)); //

        return trace;
    }

    public ClientKeyExchangeMessage createClientKeyExchangeMessage(KeyExchangeAlgorithm algorithm) {
        if (algorithm != null) {
            switch (algorithm) {
                case RSA:
                case RSA_EXPORT:
                    return new RSAClientKeyExchangeMessage(config);
                case ECDHE_ECDSA:
                case ECDH_ECDSA:
                case ECDH_RSA:
                case ECDHE_RSA:
                case ECDH_ANON:
                    return new ECDHClientKeyExchangeMessage(config);
                case DHE_DSS:
                case DHE_RSA:
                case DH_ANON:
                case DH_DSS:
                case DH_RSA:
                    return new DHClientKeyExchangeMessage(config);
                case PSK:
                    return new PskClientKeyExchangeMessage(config);
                case DHE_PSK:
                    return new PskDhClientKeyExchangeMessage(config);
                case ECDHE_PSK:
                    return new PskEcDhClientKeyExchangeMessage(config);
                case PSK_RSA:
                    return new PskRsaClientKeyExchangeMessage(config);
                case SRP_SHA_DSS:
                case SRP_SHA_RSA:
                case SRP_SHA:
                    return new SrpClientKeyExchangeMessage(config);
                case VKO_GOST01:
                case VKO_GOST12:
                    return new GOSTClientKeyExchangeMessage(config);
                case ECCPWD:
                    return new PWDClientKeyExchangeMessage(config);
                default:
                    LOGGER.warn(
                            "Unsupported key exchange algorithm: "
                                    + algorithm
                                    + ", not creating ClientKeyExchange Message");
            }
        } else {
            LOGGER.warn(
                    "Unsupported key exchange algorithm: "
                            + algorithm
                            + ", not creating ClientKeyExchange Message");
        }
        return null;
    }

    public ServerKeyExchangeMessage createServerKeyExchangeMessage(KeyExchangeAlgorithm algorithm) {
        if (algorithm != null) {
            switch (algorithm) {
                case RSA:
                case DH_DSS:
                case DH_RSA:
                    return null;
                case ECDHE_ECDSA:
                case ECDHE_RSA:
                case ECDH_ANON:
                    return new ECDHEServerKeyExchangeMessage(config);
                case DHE_DSS:
                case DHE_RSA:
                case DH_ANON:
                    return new DHEServerKeyExchangeMessage(config);
                case PSK:
                    return new PskServerKeyExchangeMessage(config);
                case DHE_PSK:
                    return new PskDheServerKeyExchangeMessage(config);
                case ECDHE_PSK:
                    return new PskEcDheServerKeyExchangeMessage(config);
                case SRP_SHA_DSS:
                case SRP_SHA_RSA:
                case SRP_SHA:
                    return new SrpServerKeyExchangeMessage(config);
                case ECCPWD:
                    return new PWDServerKeyExchangeMessage(config);
                case RSA_EXPORT:
                    // only send rsa server key exchange message if public key size is bigger than
                    // 512 bits
                    if (config.getDefaultExplicitCertificateKeyPair().getPublicKey().keySize()
                            <= 512) {
                        return null;
                    }
                    return new RSAServerKeyExchangeMessage(config);
                default:
                    LOGGER.warn(
                            "Unsupported key exchange algorithm: "
                                    + algorithm
                                    + ", not creating ServerKeyExchange Message");
            }
        } else {
            LOGGER.warn(
                    "Unsupported key exchange algorithm: "
                            + algorithm
                            + ", not creating ServerKeyExchange Message");
        }
        return null;
    }

    public void addClientKeyExchangeMessage(List<ProtocolMessage> messages) {
        CipherSuite cs = config.getDefaultSelectedCipherSuite();
        ClientKeyExchangeMessage message =
                createClientKeyExchangeMessage(AlgorithmResolver.getKeyExchangeAlgorithm(cs));
        if (message != null) {
            messages.add(message);
        }
    }

    public void addServerKeyExchangeMessage(List<ProtocolMessage> messages) {
        CipherSuite cs = config.getDefaultSelectedCipherSuite();
        ServerKeyExchangeMessage message =
                createServerKeyExchangeMessage(AlgorithmResolver.getKeyExchangeAlgorithm(cs));
        if (message != null) {
            messages.add(message);
        }
    }

    public WorkflowTrace addStartTlsActions(
            AliasedConnection connection, StarttlsType type, WorkflowTrace workflowTrace) {
        switch (type) {
            case FTP:
                {
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.FTP_S_CONNECTED.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.CLIENT,
                                    StarttlsMessage.FTP_TLS.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.FTP_S_READY.getStarttlsMessage(),
                                    "US-ASCII"));
                    return workflowTrace;
                }
            case IMAP:
                {
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.IMAP_S_CONNECTED.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.CLIENT,
                                    StarttlsMessage.IMAP_TLS.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.IMAP_S_READY.getStarttlsMessage(),
                                    "US-ASCII"));
                    return workflowTrace;
                }
            case POP3:
                {
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.POP3_S_CONNECTED.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.CLIENT,
                                    StarttlsMessage.POP3_TLS.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.POP3_S_READY.getStarttlsMessage(),
                                    "US-ASCII"));
                    return workflowTrace;
                }
            case SMTP:
                {
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.SMTP_S_CONNECTED.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.CLIENT,
                                    StarttlsMessage.SMTP_C_CONNECTED.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.SMTP_S_OK.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.CLIENT,
                                    StarttlsMessage.SMTP_TLS.getStarttlsMessage(),
                                    "US-ASCII"));
                    workflowTrace.addTlsAction(
                            MessageActionFactory.createAsciiAction(
                                    connection,
                                    ConnectionEndType.SERVER,
                                    StarttlsMessage.SMTP_S_READY.getStarttlsMessage(),
                                    "US-ASCII"));
                    return workflowTrace;
                }
            default:
                return workflowTrace;
        }
    }

    /**
     * Create a dynamic hello workflow for the default connection end defined in config.
     *
     * @return A dynamic hello workflow
     */
    private WorkflowTrace createDynamicHelloWorkflow() {
        return createDynamicHelloWorkflow(getConnection());
    }

    /**
     * Create a dynamic hello workflow for the given connection end.
     *
     * @param connection
     * @return A dynamic hello workflow
     */
    public WorkflowTrace createDynamicHelloWorkflow(AliasedConnection connection) {
        WorkflowTrace trace = createTlsEntryWorkflowTrace(connection);

        trace.addTlsAction(
                MessageActionFactory.createAction(
                        config,
                        connection,
                        ConnectionEndType.CLIENT,
                        new ClientHelloMessage(config)));

        if (config.getHighestProtocolVersion().isDTLS() && config.isDtlsCookieExchange()) {
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.SERVER,
                            new HelloVerifyRequestMessage(config)));
            trace.addTlsAction(
                    MessageActionFactory.createAction(
                            config,
                            connection,
                            ConnectionEndType.CLIENT,
                            new ClientHelloMessage(config)));
        }

        if (connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            if (config.getHighestProtocolVersion().isTLS13()) {
                trace.addTlsAction(new ReceiveTillAction(new FinishedMessage()));
            } else {
                trace.addTlsAction(new ReceiveTillAction(new ServerHelloDoneMessage()));
            }
            return trace;
        } else {
            if (config.getHighestProtocolVersion().isTLS13()) {
                List<ProtocolMessage> tls13Messages = new LinkedList<>();
                tls13Messages.add(new ServerHelloMessage(config));
                if (Objects.equals(config.getTls13BackwardsCompatibilityMode(), Boolean.TRUE)) {
                    ChangeCipherSpecMessage ccs = new ChangeCipherSpecMessage();
                    ccs.setRequired(false);
                    tls13Messages.add(ccs);
                }
                tls13Messages.add(new EncryptedExtensionsMessage(config));
                if (Objects.equals(config.isClientAuthentication(), Boolean.TRUE)) {
                    tls13Messages.add(new CertificateRequestMessage(config));
                }
                tls13Messages.add(new CertificateMessage(config));
                tls13Messages.add(new CertificateVerifyMessage(config));
                tls13Messages.add(new FinishedMessage(config));
                trace.addTlsAction(
                        MessageActionFactory.createAction(
                                config, connection, ConnectionEndType.SERVER, tls13Messages));
            } else {
                trace.addTlsAction(new SendAction(new ServerHelloMessage(config)));
                trace.addTlsAction(new SendDynamicServerCertificateAction());
                trace.addTlsAction(new SendDynamicServerKeyExchangeAction());
                if (Objects.equals(config.isClientAuthentication(), Boolean.TRUE)) {
                    trace.addTlsAction(new SendAction(new CertificateRequestMessage(config)));
                }
                trace.addTlsAction(new SendAction(new ServerHelloDoneMessage(config)));
            }
            return trace;
        }
    }

    /**
     * Create a dynamic handshake workflow for the default connection end defined in config.
     *
     * @return A dynamic handshake workflow
     */
    private WorkflowTrace createDynamicHandshakeWorkflow() {
        return createDynamicHandshakeWorkflow(getConnection());
    }

    /**
     * Create a dynamic handshake workflow for the given connection end.
     *
     * @param connection
     * @return A dynamic handshake workflow
     */
    public WorkflowTrace createDynamicHandshakeWorkflow(AliasedConnection connection) {
        WorkflowTrace trace = createDynamicHelloWorkflow(connection);
        if (connection.getLocalConnectionEndType() == ConnectionEndType.CLIENT) {
            if (config.getHighestProtocolVersion().isTLS13()) {
                List<ProtocolMessage> tls13Messages = new LinkedList<>();
                ChangeCipherSpecMessage ccs = new ChangeCipherSpecMessage();
                ccs.setRequired(false);
                tls13Messages.add(ccs);
                if (Objects.equals(config.isClientAuthentication(), Boolean.TRUE)) {
                    tls13Messages.add(new CertificateMessage(config));
                    tls13Messages.add(new CertificateVerifyMessage(config));
                }
                tls13Messages.add(new FinishedMessage(config));
                trace.addTlsAction(
                        MessageActionFactory.createAction(
                                config, connection, ConnectionEndType.CLIENT, tls13Messages));
            } else {
                if (Objects.equals(config.isClientAuthentication(), Boolean.TRUE)) {
                    trace.addTlsAction(new SendAction(new CertificateMessage(config)));
                    trace.addTlsAction(new SendDynamicClientKeyExchangeAction());
                    trace.addTlsAction(new SendAction(new CertificateVerifyMessage(config)));
                } else {
                    trace.addTlsAction(new SendDynamicClientKeyExchangeAction());
                }
                trace.addTlsAction(
                        new SendAction(
                                new ChangeCipherSpecMessage(config), new FinishedMessage(config)));
                trace.addTlsAction(new ReceiveTillAction(new FinishedMessage(config)));
            }
            return trace;
        } else {
            trace.addTlsAction(new ReceiveTillAction(new FinishedMessage(config)));
            if (!config.getHighestProtocolVersion().isTLS13()) {
                trace.addTlsAction(
                        new SendAction(
                                new ChangeCipherSpecMessage(config), new FinishedMessage(config)));
            }
            return trace;
        }
    }

    private WorkflowTrace createDynamicClientRenegotiationWithoutResumption() {
        WorkflowTrace trace = createDynamicHandshakeWorkflow();
        trace.addTlsAction(new RenegotiationAction());
        trace.addTlsAction(new FlushSessionCacheAction());
        WorkflowTrace renegotiationTrace = createDynamicHandshakeWorkflow();
        for (TlsAction reneAction : renegotiationTrace.getTlsActions()) {
            if (reneAction.isMessageAction()) { // DO NOT ADD ASCII ACTIONS
                trace.addTlsAction(reneAction);
            }
        }
        return trace;
    }
}
