/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2017 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package de.rub.nds.tlsattacker.attacks.padding.vector;

import de.rub.nds.tlsattacker.core.state.State;

/**
 *
 */
public class StatePaddingOracleVectorPair {

    private final State state;

    private final PaddingVector vector;

    public StatePaddingOracleVectorPair(State state, PaddingVector vector) {
        this.state = state;
        this.vector = vector;
    }

    public State getState() {
        return state;
    }

    public PaddingVector getVector() {
        return vector;
    }
}
