/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.attacks.impl;

import de.rub.nds.tlsattacker.attacks.config.PaddingOracleCommandConfig;
import de.rub.nds.tlsattacker.attacks.constants.PaddingRecordGeneratorType;
import de.rub.nds.tlsattacker.attacks.exception.AttackFailedException;
import de.rub.nds.tlsattacker.attacks.exception.PaddingOracleUnstableException;
import de.rub.nds.tlsattacker.attacks.padding.PaddingTraceGenerator;
import de.rub.nds.tlsattacker.attacks.padding.PaddingTraceGeneratorFactory;
import de.rub.nds.tlsattacker.attacks.padding.PaddingVectorGenerator;
import de.rub.nds.tlsattacker.attacks.padding.VectorResponse;
import de.rub.nds.tlsattacker.attacks.padding.vector.PaddingVector;
import de.rub.nds.tlsattacker.attacks.padding.vector.StatePaddingOracleVectorPair;
import de.rub.nds.tlsattacker.attacks.util.response.EqualityError;
import de.rub.nds.tlsattacker.attacks.util.response.EqualityErrorTranslator;
import de.rub.nds.tlsattacker.attacks.util.response.FingerPrintChecker;
import de.rub.nds.tlsattacker.attacks.util.response.ResponseExtractor;
import de.rub.nds.tlsattacker.attacks.util.response.ResponseFingerprint;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.record.AbstractRecord;
import de.rub.nds.tlsattacker.core.record.Record;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.workflow.ParallelExecutor;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import static de.rub.nds.tlsattacker.util.ConsoleLogger.CONSOLE;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Executes a padding oracle attack check. It logs an error in case the tested
 * server is vulnerable to poodle.
 */
public class PaddingOracleAttacker extends Attacker<PaddingOracleCommandConfig> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Config tlsConfig;

    private boolean groupRecords = true;

    private HashMap<Integer, List<VectorResponse>> responseMap;

    private CipherSuite testedSuite;

    private ProtocolVersion testedVersion;

    private final ParallelExecutor executor;

    private boolean shakyScans = false;

    /**
     *
     * @param paddingOracleConfig
     * @param baseConfig
     */
    public PaddingOracleAttacker(PaddingOracleCommandConfig paddingOracleConfig, Config baseConfig) {
        super(paddingOracleConfig, baseConfig);
        tlsConfig = getTlsConfig();
        executor = new ParallelExecutor(1, 3);
    }

    /**
     *
     * @param paddingOracleConfig
     * @param baseConfig
     * @param executor
     */
    public PaddingOracleAttacker(PaddingOracleCommandConfig paddingOracleConfig, Config baseConfig,
            ParallelExecutor executor) {
        super(paddingOracleConfig, baseConfig);
        tlsConfig = getTlsConfig();
        this.executor = executor;
    }

    @Override
    public void executeAttack() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     *
     * @return
     */
    @Override
    public Boolean isVulnerable() {
        if (config.getRecordGeneratorType() == PaddingRecordGeneratorType.VERY_SHORT) {
            groupRecords = false;
        }
        CONSOLE.info("A server is considered vulnerable to this attack if it responds differently to the test vectors.");
        CONSOLE.info("A server is considered secure if it always responds the same way.");
        EqualityError error;

        try {
            responseMap = createResponseMap();
            error = getEqualityError(responseMap);
            if (error != EqualityError.NONE) {
                CONSOLE.info("Found a side channel. Rescanning to confirm.");
                HashMap<Integer, List<VectorResponse>> responseMapTwo = createResponseMap();
                EqualityError errorTwo = getEqualityError(responseMapTwo);
                if (error == errorTwo && lookEqual(responseMap, responseMapTwo)) {
                    HashMap<Integer, List<VectorResponse>> responseMapThree = createResponseMap();
                    EqualityError errorThree = getEqualityError(responseMapThree);
                    if (error == errorThree && lookEqual(responseMap, responseMapThree)) {
                        CONSOLE.info("Found an equality Error.");
                        CONSOLE.info("The Server is very likely vulnerabble");
                    } else {
                        CONSOLE.info("Rescan revealed a false positive");
                        shakyScans = true;
                        return false;
                    }
                } else {
                    CONSOLE.info("Rescan revealed a false positive");
                    shakyScans = true;
                    return false;
                }
            }
        } catch (AttackFailedException E) {
            CONSOLE.info(E.getMessage());
            return null;
        }
        CONSOLE.info(EqualityErrorTranslator.translation(error, null, null));
        if (error != EqualityError.NONE || LOGGER.getLevel().isMoreSpecificThan(Level.INFO)) {
            for (List<VectorResponse> fingerprintList : responseMap.values()) {
                LOGGER.debug("----------------Map-----------------");
                for (VectorResponse vectorResponse : fingerprintList) {
                    LOGGER.debug(vectorResponse.toString());
                }
            }
        }

        return error != EqualityError.NONE;
    }

    /**
     *
     * @param responseMapOne
     * @param responseMapTwo
     * @return
     */
    public boolean lookEqual(HashMap<Integer, List<VectorResponse>> responseMapOne,
            HashMap<Integer, List<VectorResponse>> responseMapTwo) {
        if (responseMapOne.size() != responseMapTwo.size()) {
            throw new PaddingOracleUnstableException(
                    "The padding Oracle seems to be unstable - there is something going terrible wrong. We recommend manual analysis");
        }
        if (responseMapOne.keySet().size() != responseMapTwo.keySet().size()) {
            throw new PaddingOracleUnstableException(
                    "The padding Oracle seems to be unstable - there is something going terrible wrong. We recommend manual analysis");
        }
        for (Integer key : responseMapOne.keySet()) {
            List<VectorResponse> listOne = responseMapOne.get(key);
            List<VectorResponse> listTwo = responseMapTwo.get(key);
            if (listOne.size() != listTwo.size()) {
                throw new PaddingOracleUnstableException(
                        "The padding Oracle seems to be unstable - there is something going terrible wrong. We recommend manual analysis");
            }
            for (VectorResponse vectorResponseOne : listOne) {

                // Find equivalent
                VectorResponse equivalentVector = null;
                for (VectorResponse vectorResponseTwo : listTwo) {
                    if (vectorResponseOne.getPaddingVector().equals(vectorResponseTwo.getPaddingVector())) {
                        equivalentVector = vectorResponseTwo;
                        break;
                    }
                }
                if (equivalentVector == null) {
                    throw new PaddingOracleUnstableException("Could not find equivalent Vector - something went wrong");
                }

                if (FingerPrintChecker.checkEquality(vectorResponseOne.getFingerprint(),
                        equivalentVector.getFingerprint(), false) != EqualityError.NONE) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    public HashMap<Integer, List<VectorResponse>> createResponseMap() {

        PaddingTraceGenerator generator = PaddingTraceGeneratorFactory.getPaddingTraceGenerator(config);
        PaddingVectorGenerator vectorGenerator = generator.getVectorGenerator();
        List<State> stateList = new LinkedList<>();
        List<StatePaddingOracleVectorPair> stateVectorPairList = new LinkedList<>();
        for (PaddingVector vector : vectorGenerator.getVectors(tlsConfig.getDefaultSelectedCipherSuite(),
                tlsConfig.getDefaultHighestClientProtocolVersion())) {
            State state = new State(tlsConfig, generator.getPaddingOracleWorkflowTrace(tlsConfig, vector));
            stateList.add(state);
            stateVectorPairList.add(new StatePaddingOracleVectorPair(state, vector));
        }
        HashMap<Integer, List<VectorResponse>> responseMap = new HashMap<>();
        executor.bulkExecute(stateList);
        for (StatePaddingOracleVectorPair pair : stateVectorPairList) {
            testedSuite = pair.getState().getTlsContext().getSelectedCipherSuite();
            testedVersion = pair.getState().getTlsContext().getSelectedProtocolVersion();
            if (pair.getState().getWorkflowTrace().allActionsExecuted()) {
                ResponseFingerprint fingerprint = ResponseExtractor.getFingerprint(pair.getState());
                clearConnections(pair.getState());
                int length = getLastRecordLength(pair.getState());
                if (!groupRecords) {
                    length = 0;
                }
                List<VectorResponse> responseFingerprintList = responseMap.get(length);
                if (responseFingerprintList == null) {
                    responseFingerprintList = new LinkedList<>();
                    responseMap.put(length, responseFingerprintList);
                }
                responseFingerprintList.add(new VectorResponse(pair.getVector(), fingerprint));
            } else {
                LOGGER.warn("Could not execute Workflow. Something went wrong... Check the debug output for more information");
            }
        }
        return responseMap;
    }

    private int getLastRecordLength(State state) {
        AbstractRecord lastRecord = state.getWorkflowTrace().getLastSendingAction().getSendRecords()
                .get(state.getWorkflowTrace().getLastSendingAction().getSendRecords().size() - 1);
        return ((Record) lastRecord).getLength().getValue();
    }

    /**
     *
     * @param responseMap
     * @return
     */
    public EqualityError getEqualityError(HashMap<Integer, List<VectorResponse>> responseMap) {
        for (List<VectorResponse> list : responseMap.values()) {
            ResponseFingerprint fingerprint = list.get(0).getFingerprint();
            for (int i = 1; i < list.size(); i++) {
                EqualityError error = FingerPrintChecker.checkEquality(fingerprint, list.get(i).getFingerprint(), true);
                if (error != EqualityError.NONE) {
                    CONSOLE.info("Found an equality Error: " + error);
                    LOGGER.debug("Fingerprint1: " + fingerprint.toString());
                    LOGGER.debug("Fingerprint2: " + list.get(i).toString());

                    return error;
                }
            }
        }
        return EqualityError.NONE;
    }

    private void clearConnections(State state) {
        try {
            state.getTlsContext().getTransportHandler().closeConnection();
        } catch (IOException ex) {
            LOGGER.debug(ex);
        }
    }

    /**
     *
     * @return
     */
    public HashMap<Integer, List<VectorResponse>> getResponseMap() {
        return responseMap;
    }

    /**
     *
     * @return
     */
    public CipherSuite getTestedSuite() {
        return testedSuite;
    }

    /**
     *
     * @return
     */
    public ProtocolVersion getTestedVersion() {
        return testedVersion;
    }

    /**
     *
     * @return
     */
    public boolean isShakyScans() {
        return shakyScans;
    }
}
