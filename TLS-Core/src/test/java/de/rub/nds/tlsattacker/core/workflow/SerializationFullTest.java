/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import de.rub.nds.modifiablevariable.string.StringExplicitValueModification;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.*;
import de.rub.nds.tlsattacker.core.https.HttpsRequestMessage;
import de.rub.nds.tlsattacker.core.protocol.ProtocolMessage;
import de.rub.nds.tlsattacker.core.protocol.message.*;
import de.rub.nds.tlsattacker.core.record.AbstractRecord;
import de.rub.nds.tlsattacker.core.record.BlobRecord;
import de.rub.nds.tlsattacker.core.record.Record;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.workflow.action.*;
import de.rub.nds.tlsattacker.core.workflow.factory.WorkflowConfigurationFactory;
import de.rub.nds.tlsattacker.core.workflow.factory.WorkflowTraceType;
import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class SerializationFullTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Test
    public void test(@TempDir File tempDir) throws JAXBException, IOException {
        State state = new State();
        Config config = state.getConfig();
        config.setAddECPointFormatExtension(true);
        config.setAddEllipticCurveExtension(true);
        config.setAddHeartbeatExtension(true);
        config.setAddMaxFragmentLengthExtension(true);
        config.setAddServerNameIndicationExtension(true);
        config.setAddSignatureAndHashAlgorithmsExtension(true);
        config.setAddExtendedMasterSecretExtension(true);
        config.setAddKeyShareExtension(true);
        config.setAddPaddingExtension(true);
        config.setAddSessionTicketTLSExtension(true);
        config.setAddSignedCertificateTimestampExtension(true);
        config.setAddSupportedVersionsExtension(true);
        config.setAddTokenBindingExtension(true);

        WorkflowConfigurationFactory cf = new WorkflowConfigurationFactory(config);
        WorkflowTrace trace = cf.createWorkflowTrace(WorkflowTraceType.FULL, RunningModeType.CLIENT);
        trace.addTlsAction(new ChangeCipherSuiteAction(CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA));
        trace.addTlsAction(new ChangeClientRandomAction(new byte[] { 0x00, 0x11, 0x22, 0x33 }));
        trace.addTlsAction(new ChangeCompressionAction(CompressionMethod.LZS));
        trace.addTlsAction(new ChangeMasterSecretAction(new byte[] { 0x00, 0x22, 0x44, 0x66, 0x44 }));
        trace.addTlsAction(new ChangePreMasterSecretAction(new byte[] { 0x33, 0x66, 0x55, 0x44, }));
        trace.addTlsAction(new WaitAction(10000));
        trace.addTlsAction(new ResetConnectionAction());
        trace.addTlsAction(new ChangeProtocolVersionAction(ProtocolVersion.SSL3));
        trace.addTlsAction(new ChangeServerRandomAction(new byte[] { 0x77, 0x77, 0x77, 0x77, 0x77 }));
        trace.addTlsAction(new DeactivateEncryptionAction());
        trace.addTlsAction(new RenegotiationAction());
        trace.addTlsAction(new GenericReceiveAction());
        List<ProtocolMessage> messages = new LinkedList<>();
        messages.add(new AlertMessage());
        messages.add(new ApplicationMessage());
        messages.add(new CertificateMessage());
        messages.add(new CertificateRequestMessage());
        messages.add(new CertificateVerifyMessage());
        messages.add(new ChangeCipherSpecMessage());
        messages.add(new ClientHelloMessage());
        messages.add(new DHClientKeyExchangeMessage());
        messages.add(new DHEServerKeyExchangeMessage());
        messages.add(new ECDHClientKeyExchangeMessage());
        messages.add(new ECDHEServerKeyExchangeMessage());
        messages.add(new FinishedMessage());
        messages.add(new HeartbeatMessage());
        messages.add(new HelloRequestMessage());
        messages.add(new HelloVerifyRequestMessage());
        messages.add(new RSAClientKeyExchangeMessage());
        messages.add(new SSL2ClientHelloMessage());
        messages.add(new SSL2ServerHelloMessage());
        messages.add(new ServerHelloDoneMessage());
        messages.add(new UnknownHandshakeMessage());
        messages.add(new UnknownMessage(config, ProtocolMessageType.UNKNOWN));
        messages.add(new ServerHelloMessage());
        HttpsRequestMessage message = new HttpsRequestMessage();
        message.setRequestPath("someString");
        message.getRequestPath().setModification(new StringExplicitValueModification("replacedString"));
        messages.add(message);
        SendAction action = new SendAction(messages);
        List<AbstractRecord> records = new LinkedList<>();
        records.add(new BlobRecord());
        records.add(new Record());
        action.setRecords(records);
        trace.addTlsAction(action);

        File f = new File(tempDir, "serializationFullTest.xml");
        assert f.exists() || f.createNewFile();
        WorkflowTraceSerializer.write(f, trace);
        try (FileReader fr = new FileReader(f); BufferedReader reader = new BufferedReader(fr)) {
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append("\n").append(line);
            }
            LOGGER.info(builder.toString());
        }

        try (FileInputStream fis = new FileInputStream(f)) {
            assertDoesNotThrow(() -> WorkflowTraceSerializer.secureRead(fis));
        }
    }
}
