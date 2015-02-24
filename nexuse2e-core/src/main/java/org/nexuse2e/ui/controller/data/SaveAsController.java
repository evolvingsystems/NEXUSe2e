package org.nexuse2e.ui.controller.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Hex;
import org.nexuse2e.Engine;
import org.nexuse2e.configuration.CertificateType;
import org.nexuse2e.configuration.EngineConfiguration;
import org.nexuse2e.pojo.CertificatePojo;
import org.nexuse2e.pojo.MessagePayloadPojo;
import org.nexuse2e.pojo.MessagePojo;
import org.nexuse2e.ui.form.ProtectedFileAccessForm;
import org.nexuse2e.util.CertificateUtil;
import org.nexuse2e.util.EncryptionUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles binary download ("save as...").
 *
 * @author Jonas Reese
 */
@Controller
public class SaveAsController {

    protected static Logger LOG = Logger.getLogger(SaveAsController.class);

    @RequestMapping("/DataSaveAs.do")
    public void dataSaveAs(
            ProtectedFileAccessForm form,
            @RequestParam("type") String type,
            HttpServletRequest request,
            HttpServletResponse response,
            EngineConfiguration engineConfiguration) {
        String contentType = "application/unknown";
    
        LOG.debug("type = " + type);
    
        try {
            if (type.equals("temppkcs12")) {
            } else if (type.equals("serverCert")) {
            } else if (type.equals("cacerts")) {
    
                List<CertificatePojo> certificates = engineConfiguration.getCertificates(CertificateType.CA.getOrdinal(), null);
    
                KeyStore jks = CertificateUtil.generateKeyStoreFromPojos(certificates);
                CertificatePojo cPojo = engineConfiguration.getFirstCertificateByType(CertificateType.CACERT_METADATA.getOrdinal(), true);
                String pwd = "changeit";
                if (cPojo == null) {
                    LOG.warn("ca certificate metadata not found!");
                } else {
                    pwd = EncryptionUtil.decryptString(cPojo.getPassword());
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    jks.store(baos, pwd.toCharArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response.setHeader("Content-Disposition", "attachment; filename=\"cacerts\"");
    
                byte[] data = baos.toByteArray();
                response.setContentType(contentType);
                response.setContentLength(data.length);
                OutputStream os = response.getOutputStream();
                os.write(data);
                os.flush();
            } else if (type.equals("partner")) {
                byte[] data = new byte[0];
                
                int format = form.getFormat();
                int nxCertificateId = form.getNxCertificateId();
                String filename = "unknown";
                if (format == ProtectedFileAccessForm.PEM) {
                    filename = "certificate.pem";
                } else {
                    filename = "certificate.der";
                }
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                
                CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.ALL.getOrdinal(), nxCertificateId);
                if (cPojo == null) {
                    return;
                }
                X509Certificate cert;
                if (cPojo.isX509()) {
                    cert = CertificateUtil.getX509Certificate(cPojo);
                } else {
                    KeyStore keyStore = CertificateUtil.getPKCS12KeyStore(cPojo);
                    cert = CertificateUtil.getHeadCertificate(keyStore);
                }
                if (format == ProtectedFileAccessForm.PEM) {
                    data = CertificateUtil.getPemData(cert).getBytes();
                } else {
                    data = cert.getEncoded();
                }
                
                response.setContentLength(data.length);
                OutputStream os = response.getOutputStream();
                os.write(data);
                os.flush();
            } else if (type.equals("cacert")) {
                byte[] data = new byte[0];
                
                int format = form.getFormat();
                int nxCertificateId = form.getNxCertificateId();
                String filename = "unknown";
                if (format == ProtectedFileAccessForm.PEM) {
                    filename = "ca_certificate.pem";
                } else {
                    filename = "ca_certificate.der";
                }
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                
                CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.CA.getOrdinal(), nxCertificateId);
                if (cPojo == null) {
                    return;
                }
                
                X509Certificate cert = CertificateUtil.getX509Certificate(cPojo);
                if (format == ProtectedFileAccessForm.PEM) {
                    data = CertificateUtil.getPemData(cert).getBytes();
                } else {
                    data = cert.getEncoded();
                }
                
                response.setContentLength(data.length);
                OutputStream os = response.getOutputStream();
                os.write(data);
                os.flush();
            } else if (type.equals("staging")) {
                byte[] data = new byte[0];
    
                int format = form.getFormat();
                int content = form.getContent();
                int nxCertificateId = form.getNxCertificateId();
                String filename = "unknown";
                if (content == 1) {
                    if (format == ProtectedFileAccessForm.PEM) {
                        filename = "certificate.pem";
                    } else {
                        filename = "certificate.der";
                    }
                } else if (content == 2) {
                    filename = "certificates.zip";
                } else if (content == 3) {
                    filename = "keychain-backup.p12";
                }
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    
                try {
    
                    CertificatePojo cPojo = engineConfiguration.getCertificateByNxCertificateId(CertificateType.STAGING.getOrdinal(), nxCertificateId);
                    if (cPojo == null) {
                        return;
                    }
                    KeyStore jks = KeyStore.getInstance(CertificateUtil.DEFAULT_KEY_STORE, CertificateUtil.DEFAULT_JCE_PROVIDER);
                    jks.load(new ByteArrayInputStream(cPojo.getBinaryData()), EncryptionUtil.decryptString(cPojo.getPassword()).toCharArray());
                    boolean foundKey = false;
                    String alias = null;
                    Enumeration<String> e = jks.aliases();
                    while (e.hasMoreElements()) {
                        alias = e.nextElement();
                        // System.out.println( "Alias: '" + alias + "', entry is cert: " + jks.isCertificateEntry( alias ) );
                        if (jks.isKeyEntry(alias)) {
                            foundKey = true;
                            break;
                        }
                    }
                    if (!foundKey) {
                        throw new Exception("No certificate found!");
                    }
                    Certificate[] certs = jks.getCertificateChain(alias);
                    if (content == 1) {
                        X509Certificate cert = (X509Certificate) certs[0];
                        if (format == ProtectedFileAccessForm.PEM) {
                            data = CertificateUtil.getPemData(cert).getBytes();
                        } else if (format == ProtectedFileAccessForm.DER) {
                            data = cert.getEncoded();
                        }
    
                    } else if (content == 2) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ZipOutputStream zos = new ZipOutputStream(baos);
                        String ext = "";
    
                        if (format == ProtectedFileAccessForm.PEM) {
                            ext = ".pem";
                        } else {
                            ext = ".der";
                        }
                        ByteArrayOutputStream indexStream = new ByteArrayOutputStream();
                        PrintWriter pw = new PrintWriter(indexStream);
    
                        for (int i = 0; i < certs.length; i++) {
                            String certName = CertificateUtil.createCertificateId((X509Certificate) certs[i]);
                            String cn = CertificateUtil.getSubject((X509Certificate) certs[i], X509Name.CN);
                            String o = CertificateUtil.getSubject((X509Certificate) certs[i], X509Name.O);
                            String fingerprint = "NA";
                            byte[] responseBuf;
                            try {
                                Digest digest = new MD5Digest();
                                responseBuf = new byte[digest.getDigestSize()];
                                digest.update(certs[i].getEncoded(), 0, certs[i].getEncoded().length);
                                digest.doFinal(responseBuf, 0);
                                fingerprint = new String(Hex.encode(responseBuf));
                            } catch (CertificateEncodingException e1) {
                                e1.printStackTrace();
                            }
    
                            ZipEntry ze = new ZipEntry(certName + ext);
                            zos.putNextEntry(ze);
                            pw.println("CommonName: " + cn);
                            pw.println("Organisation: " + o);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            pw.println("expiresponse: " + sdf.format(((X509Certificate) certs[i]).getNotAfter()) + " - "
                                    + CertificateUtil.getRemainingValidity((X509Certificate) certs[i]));
                            pw.println("FingerPrint: " + fingerprint);
    
                            if (i < certs.length - 1) {
                                pw.println("---------------------------------------------------------");
                            }
    
                            if (format == ProtectedFileAccessForm.PEM) {
                                data = CertificateUtil.getPemData((X509Certificate) certs[i]).getBytes();
                            } else {
                                data = certs[i].getEncoded();
                            }
                            zos.write(data);
                        }
                        pw.flush();
                        pw.close();
    
                        ZipEntry index = new ZipEntry("index.txt");
                        zos.putNextEntry(index);
                        zos.write(indexStream.toByteArray());
                        zos.flush();
                        zos.close();
                        data = baos.toByteArray();
    
                    } else if (content == 3) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        String password = "nexus";
                        if (form.getPassword() != null && form.getPassword().equals(form.getPasswordRepeat())) {
                            password = form.getPassword();
                        }
                        jks.store(baos, password.toCharArray());
                        data = baos.toByteArray();
    
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // }
                response.setContentType(contentType);
                response.setContentLength(data.length);
                OutputStream os = response.getOutputStream();
                os.write(data);
                os.flush();
            } else if (type.equals("privatepem")) {
                LOG.debug("exporting private pem structure ");
    
                CertificatePojo requestPojo = engineConfiguration.getFirstCertificateByType(CertificateType.REQUEST.getOrdinal(), true);
                if (requestPojo == null) {
                    LOG.error("no request found in database");
                    return;
                }
                CertificatePojo privKeyPojo = engineConfiguration.getFirstCertificateByType(CertificateType.PRIVATE_KEY.getOrdinal(), true);
                if (privKeyPojo == null) {
                    LOG.error("no request found in database");
                    return;
                }
                StringBuffer sb = new StringBuffer();
    
                KeyPair keyPair = CertificateUtil.getKeyPair(privKeyPojo);
                PKCS10CertificationRequest pkcs10Request = CertificateUtil.getPKCS10Request(requestPojo);
                sb.append(CertificateUtil.getPemData(pkcs10Request));
                sb.append("\n");
                sb.append(CertificateUtil.getPemData(keyPair, EncryptionUtil.decryptString(privKeyPojo.getPassword())));
    
                byte[] data = new byte[0];
                response.setHeader("Content-Disposition", "attachment; filename=\"private_data.pem\"");
                data = sb.toString().getBytes();
                response.setContentType(contentType);
                response.setContentLength(data.length);
                OutputStream os = response.getOutputStream();
                os.write(data);
                os.flush();
    
            } else if (type.equals("request")) {
                String format = request.getParameter("format");
                String nxCertIdString = request.getParameter("nxCertificateId");
    
                int nxCertificateId = Integer.parseInt(nxCertIdString);
    
                if (nxCertificateId == 0) {
                    LOG.error("no certificateId found!");
                    return;
                }
                CertificatePojo certificate = engineConfiguration.getCertificateByNxCertificateId(CertificateType.ALL.getOrdinal(), nxCertificateId);
    
                PKCS10CertificationRequest pkcs10request = CertificateUtil.getPKCS10Request(certificate);
                byte[] data = new byte[0];
                if (format.toLowerCase().equals("pem")) {
                    response.setHeader("Content-Disposition", "attachment; filename=\"CertificateRequest.pem\"");
                    data = ((String) CertificateUtil.getPemData(pkcs10request)).getBytes();
                } else {
                    response.setHeader("Content-Disposition", "attachment; filename=\"CertificateRequest.der\"");
                    data = pkcs10request.getEncoded();
                }
    
                response.setContentType(contentType);
                response.setContentLength(data.length);
                OutputStream os = response.getOutputStream();
                os.write(data);
                os.flush();
            } else if (type.equals("content")) {
    
                byte[] data = new byte[0];
                String contenType = "text/xml";
                String fileExtension = "dat";
    
                String messageId = request.getParameter("messageId");
                String contentNo = request.getParameter("no");
                MessagePojo message = Engine.getInstance().getTransactionService().getMessage(messageId);
                if (message != null) {
                    if (contentNo == null || contentNo.equals("")) {
                        byte[] b = message.getHeaderData();
                        if (b != null) {
                            data = b;
                        }
                    } else {
                        List<MessagePayloadPojo> payloads = Engine.getInstance().getTransactionService().getMessagePayloadsFromMessage(message);
                        int no = Integer.parseInt(contentNo);
                        if (no < payloads.size()) {
                            MessagePayloadPojo payload = payloads.get(no);
                            byte[] b = payload.getPayloadData();
                            if (b != null) {
                                data = b;
                            }
                            if (!StringUtils.isEmpty(payload.getMimeType())) {
                                contenType = payload.getMimeType();
                            }
                        }
                    }
                }
    
                response.setContentType(contenType);
                if (data != null) {
                    response.setContentLength(data.length);
                }
    
                String tempFileExtension = Engine.getInstance().getFileExtensionFromMime(contenType);
                if (!StringUtils.isEmpty(tempFileExtension)) {
                    fileExtension = tempFileExtension;
                }
    
                response.setHeader("Content-Disposition", "attachment; filename=\"" + message.getMessageId() + "_payload-" + contentNo + "." + fileExtension
                        + "\"");
                OutputStream os = response.getOutputStream();
                os.write(data);
                os.flush();
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return;
    }
}
