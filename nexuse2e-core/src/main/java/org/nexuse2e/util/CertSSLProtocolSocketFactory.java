/**
 *  NEXUSe2e Business Messaging Open Source
 *  Copyright 2000-2021, direkt gruppe GmbH
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation version 3 of
 *  the License.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.nexuse2e.util;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ControllerThreadSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.nexuse2e.pojo.CertificatePojo;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * @author gesch
 */
public class CertSSLProtocolSocketFactory implements SecureProtocolSocketFactory {

    /**
     * Log object for this class.
     */
    private static final Logger LOG = LogManager.getLogger(CertSSLProtocolSocketFactory.class);

    private KeyStore keystore = null;
    private String keystorePassword = null;
    private KeyStore truststore = null;
    private SSLContext sslcontext = null;
    private CertificatePojo trustedPartnerCert = null;
    private String tlsProtocols = null;
    private String tlsCiphers = null;

    /**
     * Constructor for AuthSSLProtocolSocketFactory. Either a keystore or truststore file
     * must be given. Otherwise SSL context initialization error will result.
     *
     * @param keystore           The keystore that shall be used for client authentication. Can be <code>null</code> if
     *                           client auth is not used.
     * @param keystorePassword   The password to decrypt <code>keystore</code>.
     * @param truststore         The keystore that shall be used for trusted roots server authentication.
     * @param truststorePassword The password to decrypt <code>truststore</code>.
     * @param trustedPartnerCert The trusted partner certificate to be checked. May be <code>null</code>
     *                           if no special leaf certificate is expected and the certificate shall be checked for trusted root
     *                           and validity only.
     */
    public CertSSLProtocolSocketFactory(final KeyStore keystore, final String keystorePassword,
                                        final KeyStore truststore, final String truststorePassword, CertificatePojo trustedPartnerCert, String tlsProtocols, String tlsCiphers) {

        super();

        this.keystore = keystore;
        this.keystorePassword = keystorePassword;
        this.truststore = truststore;
        this.trustedPartnerCert = trustedPartnerCert;
        this.tlsProtocols = tlsProtocols;
        this.tlsCiphers = tlsCiphers;
    }

    private SSLContext createSSLContext() {

        try {
            KeyManager[] keymanagers = null;
            TrustManager[] trustmanagers = null;
            if (keystore != null) {
                keymanagers = CertificateUtil.createKeyManagers(keystore, this.keystorePassword);
            }
            if (truststore != null) {

                trustmanagers = CertificateUtil.createTrustManagers(truststore, trustedPartnerCert);
                LOG.debug(".......................................................");

                LOG.debug("count:" + trustmanagers.length);
                for (int i = 0; i < trustmanagers.length; i++) {
                    AuthSSLX509TrustManager manager = (AuthSSLX509TrustManager) trustmanagers[i];
                    X509Certificate[] certs = manager.getAcceptedIssuers();
                    for (int j = 0; j < certs.length; j++) {
                        X509Certificate certificate = certs[j];
                        LOG.debug("DN:" + certificate.getSubjectDN());
                    }
                }
                LOG.debug(".......................................................");

            }
            SSLContext sslcontext = SSLContext.getInstance("SSL");

            sslcontext.init(keymanagers, trustmanagers, null);
            return sslcontext;
        } catch (NoSuchAlgorithmException e) {
            LOG.error(e.getMessage(), e);
            throw new Error("Unsupported algorithm exception", e);
        } catch (KeyStoreException e) {
            LOG.error(e.getMessage(), e);
            throw new Error("Keystore exception", e);
        } catch (GeneralSecurityException e) {
            LOG.error(e.getMessage(), e);
            throw new Error("Key management exception", e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new Error("error reading keystore/truststore file", e);
        }
    }

    private SSLContext getSSLContext() {

        if (this.sslcontext == null) {
            this.sslcontext = createSSLContext();
        }
        return this.sslcontext;
    }

    /**
     * Initialize the given socket.
     *
     * @param socket The socket to be initialized.
     * @return The initialized socket, or a new socket object that wraps-up the given socket.
     */
    protected Socket init(Socket socket) throws IOException {
        if (socket instanceof SSLSocket) {
            SSLSocket sslSocket = (SSLSocket) socket;

            String[] configuredProtocols = null;
            String protocolConfigSource = "N/A";
            if (StringUtils.isNotBlank(tlsProtocols)) {
                configuredProtocols = tlsProtocols.trim().split("\\s*,\\s*");
                protocolConfigSource = "connection configuration";
            } else {
                String httpsProtocols = System.getProperty("https.protocols");
                if (httpsProtocols != null) {
                    configuredProtocols = httpsProtocols.trim().split("\\s*,\\s*");
                    protocolConfigSource = "JVM options";
                }
            }
            if (configuredProtocols != null) {
                String[] supportedProtocols = getSSLContext().getSupportedSSLParameters().getProtocols();
                if (!Arrays.asList(supportedProtocols).containsAll(Arrays.asList(configuredProtocols))) {
                    throw new IOException("Unsupported tls protocols configured via " + protocolConfigSource + ": " + Arrays.toString(configuredProtocols) + " (supported protocols: " + Arrays.toString(supportedProtocols) + ")");
                }
                sslSocket.setEnabledProtocols(configuredProtocols);
            }

            String[] configuredCipherSuites = null;
            String cipherConfigSource = "N/A";
            if (StringUtils.isNotBlank(tlsCiphers)) {
                configuredCipherSuites = tlsCiphers.trim().split("\\s*,\\s*");
                cipherConfigSource = "connection configuration";
            } else {
                String cipherSuites = System.getProperty("https.cipherSuites");
                if (StringUtils.isNotBlank(cipherSuites)) {
                    configuredCipherSuites = cipherSuites.trim().split("\\s*,\\s*");
                    cipherConfigSource = "JVM options";
                }
            }
            if (configuredCipherSuites != null) {
                try {
                    sslSocket.setEnabledCipherSuites(configuredCipherSuites);
                } catch (IllegalArgumentException e) {
                    throw new IOException("Unsupported tls cipher suites configured via " + cipherConfigSource + ": " + Arrays.toString(configuredCipherSuites) + ".", e);
                }
            }
        }
        return socket;
    }

    /**
     * Attempts to get a new socket connection to the given host within the given time limit.
     * <p>
     * To circumvent the limitations of older JREs that do not support connect timeout a
     * controller thread is executed. The controller thread attempts to create a new socket
     * within the given limit of time. If socket constructor does not return until the
     * timeout expires, the controller terminates and throws an {@link ConnectTimeoutException}
     * </p>
     *
     * @param host         the host name/IP
     * @param port         the port on the host
     * @param localAddress the local host name/IP to bind the socket to
     * @param localPort    the port on the local machine
     * @param params       {@link HttpConnectionParams Http connection parameters}
     * @return Socket a new socket
     * @throws IOException          if an I/O error occurs while creating the socket
     * @throws UnknownHostException if the IP address of the host cannot be
     *                              determined
     */
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort,
                               final HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {

        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        if (timeout == 0) {
            return createSocket(host, port, localAddress, localPort);
        } else {
            // To be eventually deprecated when migrated to Java 1.4 or above
            return ControllerThreadSocketFactory.createSocket(this, host, port, localAddress, localPort, timeout);
        }
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int)
     */
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException,
            UnknownHostException {

        return init(getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort));
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String, int)
     */
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {

        return init(getSSLContext().getSocketFactory().createSocket(host, port));
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
     */
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
            UnknownHostException {

        return init(getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose));
    }
}