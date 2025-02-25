/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.ProtocolVersion;
import de.rub.nds.tlsattacker.core.protocol.message.DtlsHandshakeMessageFragment;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.util.tests.TestCategories;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.TestAbortedException;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.Security;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CyclicParserSerializerTest {

    private static final Logger LOGGER = LogManager.getLogger();

    private TlsContext context;

    @BeforeAll
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @BeforeEach
    public void setUp() {
        context = new TlsContext();
    }

    public static Stream<Arguments> provideParserSerializerTestVectors() {
        Stream.Builder<Arguments> builder = Stream.builder();
        // Get all parsers by reflection
        Reflections reflections = new Reflections("de.rub.nds.tlsattacker.core.protocol.message");
        Set<Class<? extends ProtocolMessage>> messageClasses = reflections.getSubTypesOf(ProtocolMessage.class);
        Class<? extends ProtocolMessageParser> parserClass = null;
        Class<? extends ProtocolMessagePreparator> preparatorClass = null;
        Class<? extends ProtocolMessageSerializer> serializerClass = null;
        for (Class<? extends ProtocolMessage> messageClass : messageClasses) {
            String testName = messageClass.getSimpleName().replace("Message", "");
            if (Modifier.isAbstract(messageClass.getModifiers())) {
                LOGGER.info("Encountered abstract message class, skipping it: {}", testName);
                continue;
            }
            if (messageClass == DtlsHandshakeMessageFragment.class) {
                LOGGER.debug(
                    "Message class is DtlsHandshakeMessageFragment, will not be included in the provided test vectors");
                continue;
            }

            // Get corresponding parser
            try {
                parserClass = getParser(testName);
                if (Modifier.isAbstract(parserClass.getModifiers())) {
                    LOGGER.error("Encountered abstract parser class for non-abstract message class, skipping it: {}",
                        parserClass.getSimpleName());
                    continue;
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Unable to find corresponding parser class for test {}", testName);
                parserClass = null;
            }

            // Get corresponding preparator
            try {
                preparatorClass = getPreparator(testName);
                if (Modifier.isAbstract(preparatorClass.getModifiers())) {
                    LOGGER.error(
                        "Encountered abstract preparator class for non-abstract message class, skipping it: {}",
                        preparatorClass.getSimpleName());
                    continue;
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Unable to find corresponding preparator class for test {}", testName);
                preparatorClass = null;
            }

            // Get corresponding serializer
            try {
                serializerClass = getSerializer(testName);
                if (Modifier.isAbstract(serializerClass.getModifiers())) {
                    LOGGER.error(
                        "Encountered abstract serializer class for non-abstract message class, skipping it: {}",
                        serializerClass.getSimpleName());
                    continue;
                }
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Unable to find corresponding serializer class for test {}", testName);
                serializerClass = null;
            }

            for (ProtocolVersion version : ProtocolVersion.values()) {
                if (version.isDTLS()) {
                    continue;
                }
                builder.add(
                    Arguments.of(testName, version, messageClass, true, parserClass, preparatorClass, serializerClass));
                builder.add(Arguments.of(testName, version, messageClass, false, parserClass, preparatorClass,
                    serializerClass));
            }
        }
        return builder.build();
    }

    @ParameterizedTest(name = "{0} [{1}, default message constructor: {3}]")
    @MethodSource("provideParserSerializerTestVectors")
    @Tag(TestCategories.INTEGRATION_TEST)
    public void testParserSerializerPairs(String testName, ProtocolVersion protocolVersion,
        Class<? extends ProtocolMessage> messageClass, boolean useDefaultMessageConstructor,
        Class<? extends ProtocolMessageParser> parserClass, Class<? extends ProtocolMessagePreparator> preparatorClass,
        Class<? extends ProtocolMessageSerializer> serializerClass) {
        assumeTrue(messageClass != null, "Message class for test " + testName + " could not be found");
        assumeTrue(parserClass != null, "Parser class for test " + testName + " could not be found");
        assumeTrue(preparatorClass != null, "Preparator class for test " + testName + " could not be found");
        assumeTrue(serializerClass != null, "Serializer class for test " + testName + " could not be found");
        ProtocolMessage message = null;
        ProtocolMessageParser<? extends ProtocolMessage> parser = null;
        ProtocolMessagePreparator<? extends ProtocolMessage> preparator = null;
        ProtocolMessageSerializer<? extends ProtocolMessage> serializer = null;

        context.setSelectedProtocolVersion(protocolVersion);
        context.getConfig().setHighestProtocolVersion(protocolVersion);
        context.getConfig().setDefaultHighestClientProtocolVersion(protocolVersion);

        Constructor<? extends ProtocolMessage> messageConstructor;
        if (useDefaultMessageConstructor) {
            messageConstructor = getDefaultMessageConstructor(messageClass);
        } else {
            messageConstructor = getMessageConstructor(messageClass);
        }
        if (messageConstructor == null) {
            fail("Could not find message constructor for test " + testName);
        }
        try {
            if (useDefaultMessageConstructor) {
                message = messageConstructor.newInstance();
            } else {
                message = messageConstructor.newInstance(context.getConfig());
            }
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            fail("Could not create message instance for test " + testName);
        }

        Constructor<? extends ProtocolMessagePreparator> preparatorConstructor = getConstructor(preparatorClass, 2);
        if (preparatorConstructor == null) {
            fail("Could not find preparator constructor with two arguments for test " + testName);
        }
        try {
            preparator = preparatorConstructor.newInstance(context.getChooser(), message);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            fail("Could not create preparator instance for test " + testName);
        }

        // Skip test if preparation is not supported yet
        try {
            preparator.prepare();
        } catch (UnsupportedOperationException e) {
            LOGGER.info("Preparator for test " + testName + " is not yet supported");
            throw new TestAbortedException("Preparator for test " + testName + " is not yet supported");
        }

        Constructor<? extends ProtocolMessageSerializer> serializerConstructor = getConstructor(serializerClass, 2);
        if (serializerConstructor == null) {
            fail("Could not find serializer constructor with two arguments for test " + testName);
        }
        try {
            serializer = serializerConstructor.newInstance(message, protocolVersion);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            fail("Could not create serializer instance for test " + testName);
        }
        byte[] serializedMessage = serializer.serialize();

        Constructor<? extends ProtocolMessageParser> parserConstructor = getConstructor(parserClass, 4);
        if (parserConstructor == null) {
            fail("Could not find parser constructor with four arguments for test " + testName);
        }
        try {
            parser = parserConstructor.newInstance(0, serializedMessage, protocolVersion, context.getConfig());
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            fail("Could not create parser instance for test " + testName);
        }
        try {
            message = parser.parse();
        } catch (UnsupportedOperationException e) {
            LOGGER.info("Parser for test " + testName + " is not yet supported");
            throw new TestAbortedException("Parser for test " + testName + " is not yet supported");
        }

        try {
            serializer = serializerConstructor.newInstance(message, protocolVersion);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            fail("Could not create serializer instance for test " + testName);
        }
        assertArrayEquals(serializedMessage, serializer.serialize());
    }

    private static Class<? extends ProtocolMessageParser> getParser(String testName) throws ClassNotFoundException {
        String preparatorName = "de.rub.nds.tlsattacker.core.protocol.parser." + testName + "Parser";
        try {
            return (Class<? extends ProtocolMessageParser>) Class.forName(preparatorName);
        } catch (ClassNotFoundException E) {
            try {
                preparatorName = "de.rub.nds.tlsattacker.core.protocol.preparator." + testName + "MessageParser";
                return (Class<? extends ProtocolMessageParser>) Class.forName(preparatorName);
            } catch (ClassNotFoundException ex) {
                throw new ClassNotFoundException("Could not find Parser for " + testName);
            }
        }
    }

    private static Class<? extends ProtocolMessagePreparator> getPreparator(String testName)
        throws ClassNotFoundException {
        String preparatorName = "de.rub.nds.tlsattacker.core.protocol.preparator." + testName + "Preparator";
        try {
            return (Class<? extends ProtocolMessagePreparator>) Class.forName(preparatorName);
        } catch (ClassNotFoundException E) {
            try {
                preparatorName = "de.rub.nds.tlsattacker.core.protocol.preparator." + testName + "MessagePreparator";
                return (Class<? extends ProtocolMessagePreparator>) Class.forName(preparatorName);
            } catch (ClassNotFoundException ex) {
                throw new ClassNotFoundException("Could not find Preparator for " + testName);
            }
        }
    }

    private static Class<? extends ProtocolMessageSerializer> getSerializer(String testName)
        throws ClassNotFoundException {
        String serializerName = "de.rub.nds.tlsattacker.core.protocol.serializer." + testName + "Serializer";
        try {
            return (Class<? extends ProtocolMessageSerializer>) Class.forName(serializerName);
        } catch (ClassNotFoundException E) {
            try {
                return (Class<? extends ProtocolMessageSerializer>) Class.forName(serializerName + "MessageSerializer");
            } catch (ClassNotFoundException ex) {
                throw new ClassNotFoundException("Could not find Serializer for " + testName);
            }
        }
    }

    private static Constructor getMessageConstructor(Class someClass) {
        for (Constructor c : someClass.getConstructors()) {
            if (c.getParameterCount() == 1) {
                if (c.getParameterTypes()[0].equals(Config.class)) {
                    return c;
                }
            }
        }
        LOGGER.warn("Could not find Constructor: " + someClass.getSimpleName());
        return null;
    }

    private static Constructor getDefaultMessageConstructor(Class someClass) {
        for (Constructor c : someClass.getDeclaredConstructors()) {
            if (c.getParameterCount() == 0) {
                return c;
            }
        }
        LOGGER.warn("Could not find Constructor: " + someClass.getSimpleName());
        return null;
    }

    private static Constructor getConstructor(Class someClass, int numberOfArguments) {
        for (Constructor c : someClass.getConstructors()) {
            if (c.getParameterCount() == numberOfArguments) {
                return c;
            }
        }
        LOGGER.warn("Could not find Constructor: " + someClass.getSimpleName());
        return null;
    }
}
