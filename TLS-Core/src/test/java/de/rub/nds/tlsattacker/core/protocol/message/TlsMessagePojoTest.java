/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.message;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.PojoClassFilter;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TlsMessagePojoTest {

    // The package to be tested
    private static final String packageName = TlsMessage.class.getPackageName();

    private static Validator validator;

    @BeforeAll
    public static void setUpClass() {
        validator = ValidatorBuilder.create().with(new SetterTester(), new GetterTester()).build();
    }

    public static Stream<Named<PojoClass>> provideTlsMessagePojoClasses() {
        return PojoClassFactory.getPojoClasses(packageName, new TlsMessagePojoTest.TestClassesFilter()).stream()
            // We're wrapping the POJO class into a named instance for better readability in the IDE
            .map(pojoClass -> Named.of(pojoClass.getName().replace(packageName + ".", ""), pojoClass));
    }

    @ParameterizedTest
    @MethodSource("provideTlsMessagePojoClasses")
    public void testTlsMessageGettersAndSetters(PojoClass providedExtensionMessagePojoClass) {
        validator.validate(providedExtensionMessagePojoClass);
    }

    /**
     * A simple implementation of the PojoClassFilter class to avoid including test classes into POJO tests.
     */
    private static class TestClassesFilter implements PojoClassFilter {
        @Override
        public boolean include(PojoClass pojoClass) {
            return !pojoClass.getSourcePath().contains("/test-classes/");
        }
    }
}
