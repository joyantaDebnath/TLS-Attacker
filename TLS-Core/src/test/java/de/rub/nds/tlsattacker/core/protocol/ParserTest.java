/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol;

import static org.junit.jupiter.api.Assertions.*;

import de.rub.nds.tlsattacker.core.exceptions.ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;

public class ParserTest {

    private Parser<Object> parser;
    private Parser<Object> middleParser;

    @BeforeEach
    public void setUp() {
        byte[] bytesToParse = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
        parser = new ParserImpl(0, bytesToParse);
        middleParser = new ParserImpl(3, bytesToParse);
    }

    @Test
    public void testPeek() {
        parser.parseByteArrayField(4);
        assertEquals(4, parser.peek());
    }

    @Test
    public void testPeekFailure() {
        parser.parseByteArrayField(9);
        assertThrows(ParserException.class, parser::peek);
    }

    /**
     * Test of parseByteArrayField method, of class Parser.
     */
    @Test
    public void testParseByteField() {
        byte[] result = parser.parseByteArrayField(1);
        assertArrayEquals(result, new byte[] { 0 });
        result = parser.parseByteArrayField(2);
        assertArrayEquals(result, new byte[] { 1, 2 });
        result = middleParser.parseByteArrayField(1);
        assertArrayEquals(result, new byte[] { 3 });
        result = middleParser.parseByteArrayField(2);
        assertArrayEquals(result, new byte[] { 4, 5 });
    }

    /**
     * Test of parseSingleByteField method, of class Parser.
     */
    @Test
    public void testParseSingleByteField() {
        byte result = parser.parseByteField(1);
        assertEquals(result, 0);
        result = middleParser.parseByteField(1);
        assertEquals(result, 3);
    }

    /**
     * Test of parseIntField method, of class Parser.
     */
    @Test
    public void testParseIntField() {
        int result = parser.parseIntField(1);
        assertEquals(0, result);
        result = parser.parseIntField(2);
        assertEquals(0x0102, result);
        result = middleParser.parseIntField(1);
        assertEquals(3, result);
        result = middleParser.parseIntField(2);
        assertEquals(0x0405, result);
    }

    /**
     * Test of parseIntField method, of class Parser.
     */
    @Test
    public void testParseBigIntField() {
        BigInteger result = parser.parseBigIntField(1);
        assertEquals(0, result.intValue());
        result = parser.parseBigIntField(2);
        assertEquals(0x0102, result.intValue());
        result = middleParser.parseBigIntField(1);
        assertEquals(3, result.intValue());
        result = middleParser.parseBigIntField(2);
        assertEquals(0x0405, result.intValue());
    }

    @Test
    public void testParseIntFieldNegative() {
        assertThrows(ParserException.class, () -> parser.parseIntField(-123));
    }

    @Test
    public void testParseIntFieldZero() {
        assertThrows(ParserException.class, () -> parser.parseIntField(0));
    }

    @Test
    public void testParseByteFieldZero() {
        assertEquals(0, parser.parseByteArrayField(0).length);
    }

    @Test
    public void testParseByteFieldNegative() {
        assertThrows(ParserException.class, () -> parser.parseByteArrayField(-123));
    }

    @Test
    public void testParseSingleByteFieldNegative() {
        assertThrows(ParserException.class, () -> parser.parseByteField(-123));
    }

    @Test
    public void testParseSingleByteFieldZero() {
        assertThrows(ParserException.class, () -> parser.parseByteField(0));
    }

    @Test
    public void testAlreadyParsed() {
        assertArrayEquals(parser.getAlreadyParsed(), new byte[0]);
        parser.parseIntField(1);
        assertArrayEquals(parser.getAlreadyParsed(), new byte[] { 0 });
        parser.parseIntField(3);
        assertArrayEquals(parser.getAlreadyParsed(), new byte[] { 0, 1, 2, 3 });
    }

    @Test
    public void testAlreadyParsedMiddle() {
        assertArrayEquals(middleParser.getAlreadyParsed(), new byte[0]);
        middleParser.parseIntField(1);
        assertArrayEquals(middleParser.getAlreadyParsed(), new byte[] { 3 });
        middleParser.parseIntField(3);
        assertArrayEquals(middleParser.getAlreadyParsed(), new byte[] { 3, 4, 5, 6 });
    }

    @Test
    public void testConstructorException() {
        byte[] base = new byte[] { 0, 1 };
        assertThrows(ParserException.class, () -> new ParserImpl(3, base));
    }

    @Test
    public void testEnoughBytesLeft() {
        assertTrue(parser.enoughBytesLeft(9));
        assertFalse(parser.enoughBytesLeft(10));
        assertTrue(parser.enoughBytesLeft(1));
        parser.parseByteArrayField(7);
        assertFalse(parser.enoughBytesLeft(9));
        assertTrue(parser.enoughBytesLeft(2));
        assertTrue(parser.enoughBytesLeft(1));

    }

    @Test
    public void testBytesLeft() {
        assertEquals(9, parser.getBytesLeft());
        parser.parseByteArrayField(2);
        assertEquals(7, parser.getBytesLeft());
        parser.parseByteArrayField(7);
        assertEquals(0, parser.getBytesLeft());
    }

    @Test
    public void testParseString() {
        byte[] bytesToParse = "This is a test\t\nabc".getBytes(Charset.defaultCharset());
        parser = new ParserImpl(0, bytesToParse);
        assertEquals("This is a test\t\n", parser.parseStringTill((byte) 0x0A));
    }

    public static class ParserImpl extends Parser<Object> {

        public ParserImpl(int i, byte[] a) {
            super(i, a);
        }

        @Override
        public Object parse() {
            return null;
        }
    }
}
