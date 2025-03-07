/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.constants;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

public class ProtocolVersionTest {

    @ParameterizedTest
    @EnumSource(ProtocolVersion.class)
    public void testGetFromValue(ProtocolVersion providedProtocolVersion) {
        assertSame(providedProtocolVersion, ProtocolVersion.getProtocolVersion(providedProtocolVersion.getValue()));
    }

    /**
     * Test of gethighestProtocolVersion method, of class ProtocolVersion.
     */
    @Test
    public void testGetHighestProtocolVersion() {
        List<ProtocolVersion> versions =
            List.of(ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12, ProtocolVersion.TLS13);
        ProtocolVersion highestProtocolVersion = ProtocolVersion.getHighestProtocolVersion(versions);
        assertSame(ProtocolVersion.TLS13, highestProtocolVersion);
    }
}
