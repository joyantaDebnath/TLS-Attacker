/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.server.config;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import de.rub.nds.tlsattacker.core.config.delegate.GeneralDelegate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ServerCommandConfigTest {

    /**
     * Test config command line parsing
     */
    @Test
    @Disabled("Not implemented")
    public void testCommandLineParsing() {
    }

    /**
     * Test invalid config with invalid cipher suite
     */
    @Test()
    public void testInvalidCommandLineParsing() {
        JCommander jc = new JCommander();

        ServerCommandConfig server = new ServerCommandConfig(new GeneralDelegate());
        jc.addCommand(ServerCommandConfig.COMMAND, server);

        assertThrows(ParameterException.class,
            () -> jc.parse("server", "-cipher", "invalid,TLS_RSA_WITH_AES_256_CBC_SHA", "-version", "TLSv1.2"));
    }

}
