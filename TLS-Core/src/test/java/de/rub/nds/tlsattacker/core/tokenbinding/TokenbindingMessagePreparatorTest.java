/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.tokenbinding;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.*;
import de.rub.nds.tlsattacker.core.crypto.ECCUtilsBCWrapper;
import de.rub.nds.tlsattacker.core.exceptions.PreparationException;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Security;
import java.util.Base64;

public class TokenbindingMessagePreparatorTest {

    private final static Logger LOGGER = LogManager.getLogger();

    private Config config;

    private TokenBindingMessage message;

    private TokenBindingMessagePreparator preparator;

    @BeforeAll
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @BeforeEach
    public void setUp() {
        config = Config.createConfig();
        TlsContext context = new TlsContext(config);
        Chooser chooser = context.getChooser();
        message = new TokenBindingMessage();
        preparator = new TokenBindingMessagePreparator(chooser, message);
        config.setDefaultSelectedSignatureAndHashAlgorithm(SignatureAndHashAlgorithm.ECDSA_SHA256);
        context.setClientRandom(
            ArrayConverter.hexStringToByteArray("772EF595D8B1885E8F5DA5B0595B9E324E04571D5392BF99A046F00A1D331AEB"));
        context.setServerRandom(
            ArrayConverter.hexStringToByteArray("C3CE61F0F6A8335E98AF8725385586B41FEFF205B4E05A000823F78B5F8F5C02"));
        context.setMasterSecret(ArrayConverter.hexStringToByteArray(
            "3B4B7628B03375E582E1398DA34FB51A9526847151337029CC15689130EE879B65DC461EF9DAEBB33C4C0FF5885FCE73"));
        context.setSelectedCipherSuite(CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA);
        context.setSelectedProtocolVersion(ProtocolVersion.TLS12);
    }

    /**
     * Test of prepareProtocolMessageContents method, of class TokenBindingMessagePreparator.
     *
     */
    @Test
    public void testPrepareProtocolMessageContents() {
        preparator.prepare();
        TokenBindingMessageSerializer serializer = new TokenBindingMessageSerializer(message, ProtocolVersion.TLS12);
        byte[] serialize = serializer.serialize();
        TokenBindingMessageParser selfParser =
            new TokenBindingMessageParser(0, serialize, ProtocolVersion.TLS12, config);
        TokenBindingMessage selfParsed = selfParser.parse();
        assertNotNull(selfParsed);
        String base64 =
            "AIkAAgBBQM9eQES_uxoyRn0DDoYLcWqvm6Oo3p0lI1s3fRjdIj6dw8wLDf0RWkxuyNAmgAQkUWxm8_JfwS8MziBYVuJ5ECcAQHF_HGcPiSv_X60y5Ql-AxoqaWzwqXvpStEBgY_IX8kT_qAHsb5h38ZuQoWOaZVgqlF1sa70B4GVXxmi2JkdJYcAAA";
        byte[] decode = Base64.getUrlDecoder().decode(base64);
        TokenBindingMessageParser parser = new TokenBindingMessageParser(0, decode, ProtocolVersion.TLS12, config);
        TokenBindingMessage parsedMessage = parser.parse();
        byte[] xBytes = new byte[32];
        System.arraycopy(parsedMessage.getPoint().getValue(), 0, xBytes, 0, 32);
        LOGGER.debug("X:" + ArrayConverter.bytesToHexString(xBytes));
        byte[] yBytes = new byte[32];
        System.arraycopy(parsedMessage.getPoint().getValue(), 32, yBytes, 0, 32);
        LOGGER.debug("Y:" + ArrayConverter.bytesToHexString(yBytes));
        BigInteger intX = new BigInteger(xBytes);
        LOGGER.debug("intx: " + intX);

        ASN1Integer x = new ASN1Integer(xBytes);
        LOGGER.debug("xasn1:" + x.getPositiveValue());
        ASN1Integer y = new ASN1Integer(yBytes);
        ECDomainParameters generateEcParameters = generateEcParameters();
        ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(
            generateEcParameters.getCurve().createPoint(x.getPositiveValue(), y.getPositiveValue()),
            generateEcParameters);
        LOGGER.debug("RAW X:" + ecPublicKeyParameters.getQ().getRawXCoord().toBigInteger());
        LOGGER.debug("RAW Y:" + ecPublicKeyParameters.getQ().getRawYCoord().toBigInteger());
        LOGGER.debug("Valid: " + ecPublicKeyParameters.getQ().isValid());
        ECDSASigner signer = new ECDSASigner();
        signer.init(false, ecPublicKeyParameters);

        byte[] signedContent =
            ArrayConverter.hexStringToByteArray("0002A5F86D4EA32D7B305774F6EA56DB444F4D70D777ABF77B810561935F3A96B9C2");// Correct
        // ekm
        // with
        // blablub
        // ours byte[] signedContent =
        // ArrayConverter.hexStringToByteArray("00022054736C9903E145286A925F9F2C064603D3211BCF0D81EDB6FEB6E9ACCAB4B7");
        byte[] rBytes = new byte[32];
        System.arraycopy(parsedMessage.getSignature().getValue(), 0, rBytes, 0, 32);
        byte[] sBytes = new byte[32];
        System.arraycopy(parsedMessage.getSignature().getValue(), 32, sBytes, 0, 32);
        LOGGER.debug("r:" + ArrayConverter.bytesToHexString(rBytes));
        LOGGER.debug("s:" + ArrayConverter.bytesToHexString(sBytes));
        LOGGER.debug("r:" + new ASN1Integer(rBytes).getPositiveValue());
        LOGGER.debug("s:" + new ASN1Integer(sBytes).getPositiveValue());
        // decodeASN1(parsedMessage.getSignature().getValue());
    }

    private ECDomainParameters generateEcParameters() {
        NamedGroup[] curves = new NamedGroup[] { NamedGroup.SECP256R1 };
        ECPointFormat[] formats = new ECPointFormat[] { ECPointFormat.UNCOMPRESSED };
        InputStream is = new ByteArrayInputStream(ArrayConverter
            .concatenate(new byte[] { EllipticCurveType.NAMED_CURVE.getValue() }, NamedGroup.SECP256R1.getValue()));
        ECDomainParameters ecParams;
        try {
            ecParams = ECCUtilsBCWrapper.readECParameters(curves, formats, is);
        } catch (IOException ex) {
            throw new PreparationException("Failed to generate EC domain parameters", ex);
        }

        return ecParams;
    }
}
