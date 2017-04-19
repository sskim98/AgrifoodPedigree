package kaist.gs1.pms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * 인증서 관리자
 */
@Component
public class Manager_Certificate extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    
    // private Key 생성 --> CSR 생성 --> Root Key 생성 --> certificate 생성 을 테스트 하기 위한 코드, 현재 사용하지 않음
    public boolean test() {
    	// node key pair 생성
    	KeyPair pmsKeyPair = generateKeyPair("RSA", 1024);
        System.out.println("KeyPair generated");
         
        // node csr 생성
        PKCS10CertificationRequest request = null;
		try {
			request = generateRequest(pmsKeyPair);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
		// CSR을 PEM 타입으로 변환
		StringWriter strWriter = new StringWriter();
		PemWriter writer = new PemWriter(strWriter);
		PemObjectGenerator pemObject;
		try {
			pemObject = new PemObject("CERTIFICATE REQUEST", request.getEncoded());
			writer.writeObject(pemObject);
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print(strWriter.toString());
	    
		// root key pair를 생성
		KeyPair rootKeyPair = generateKeyPair("RSA", 1024);
		X509Certificate rootCert = null;
		// root 인증서 생성
		try {
			rootCert = generateV1SelfSignedCertificate(rootKeyPair, "CN=root,C=AU,O=The Legion of the Bouncy Castle,OU=Bouncy Intermediate Certificate,EmailAddress=feedback-crypto@bouncycastle.org");
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// node key pair의 유효성 검증
		try {
			if (!request.isSignatureValid(new JcaContentVerifierProviderBuilder().build(pmsKeyPair.getPublic())))
			{
				System.out.println("Failed verify check.");
			}
			else
			{
				System.out.println("PKCS#10 request verified.");
			}
		} catch (OperatorCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PKCSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// node의 인증서 생성
		SubjectPublicKeyInfo pkInfo = request.getSubjectPublicKeyInfo();
		RSAKeyParameters rsa;
		X509Certificate pmsCert = null;
		try {
			rsa = (RSAKeyParameters) PublicKeyFactory.createKey(pkInfo);
			RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(rsa.getModulus(), rsa.getExponent());
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey rsaPub = kf.generatePublic(rsaSpec);
			pmsCert = createCert(rsaPub, rootKeyPair.getPrivate(), rootKeyPair.getPublic());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// node 인증서를 PEM 타입으로 변경
		StringWriter strWriter2 = new StringWriter();
        PemWriter pWrt = new PemWriter(strWriter2);
        try
        {
            pWrt.writeObject(new PemObject("CERTIFICATE", pmsCert.getEncoded()));
            pWrt.close();
        }
        catch (Exception e)
        {
            try {
				throw new CertificateEncodingException("can't encode certificate for PEM encoded path");
			} catch (CertificateEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        
        System.out.println(strWriter2.toString());
		
        // 인증서 저장
        storeKey(rootCert, pmsCert, pmsKeyPair);
        
        /*
        try {
			//checkCertificatePath(pmsKeyPair, rootKeyPair, pmsCert, rootCert );
        	checkCertificate(pmsCert);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
        // 인증서의 path를 검증 테스트
        try {
			checkPrivateCertificatePath(pmsCert, rootCert);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        // 파일로부터 인증서들을 읽어서 path 검증을 테스트
        InputStream inStream;
		try {
			inStream = new FileInputStream("C:\\gsdev\\itc_cert\\itc.crt");
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			pmsCert = (X509Certificate)cf.generateCertificate(inStream);
			inStream.close();
			inStream = new FileInputStream("C:\\gsdev\\itc_cert\\inter.crt");
			CertificateFactory cff = CertificateFactory.getInstance("X.509");
			rootCert = (X509Certificate)cff.generateCertificate(inStream);
			inStream.close();
			checkPKICertificatePath(pmsCert, rootCert );
			//checkPKICertificatePath(rootKeyPair, pmsCert, null );
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return true;
    }
    
    // CSR 생성 루틴 : PEM 타입 CSR 생성
    public String generateCSR() {
    	KeyPair pmsKeyPair = generateKeyPair("RSA", 1024);
        System.out.println("KeyPair generated");
         
        PKCS10CertificationRequest request = null;
		try {
			request = generateRequest(pmsKeyPair);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
		StringWriter strWriter = new StringWriter();
		PemWriter writer = new PemWriter(strWriter);
		PemObjectGenerator pemObject;
		try {
			pemObject = new PemObject("CERTIFICATE REQUEST", request.getEncoded());
			writer.writeObject(pemObject);
		    writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print(strWriter.toString());
		return strWriter.toString();
    }
    
    // 자가 서명 인증서 생성 루틴
    // subject : 인증서 생성을 위한 정보 "CN=root,C=AU,O=The Legion of the Bouncy Castle,OU=Bouncy Intermediate Certificate,EmailAddress=feedback-crypto@bouncycastle.org"
    public static X509Certificate generateV1SelfSignedCertificate(KeyPair caKeyPair, String subject) throws Exception {
        try {
        	// 기관 정보 입력
            X500Name subjectDN = new X500Name(subject);
            // 시리얼 번호 및 10년짜리 인증서 생성
            BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
            Date validityStartDate = new Date(System.currentTimeMillis() - 100000);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 10);
            Date validityEndDate = new Date(calendar.getTime().getTime());
            SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(caKeyPair.getPublic().getEncoded());

            // 인증서 생성
            X509v1CertificateBuilder builder = new X509v1CertificateBuilder(subjectDN, serialNumber, validityStartDate,
                    validityEndDate, subjectDN, subPubKeyInfo);
            // 인증서 서명 알고리즘 설정
            AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256WithRSAEncryption");
            // 인증서 다이제스트 알고리즘 설정
            AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId); 
            // 인증서 서명
            ContentSigner contentSigner = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(PrivateKeyFactory.createKey(caKeyPair.getPrivate().getEncoded())); 
            X509CertificateHolder holder = builder.build(contentSigner);
            // 인증서 생성
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("SUN").getCertificate(holder);
            // 인증서 검증: 현재 날짜
            cert.checkValidity(new Date());
            // 인증서에 대해 ca의 공개키로 인증되는지 확인 : ca의 개인키로 인증서의 서명이 검증되는지 확인
            cert.verify(caKeyPair.getPublic());
            
            return cert;
        } catch (Exception e) {
            throw new RuntimeException("Error creating X509v1Certificate.", e);
        }
    }
     
    // node의 인증서 생성 루틴 : 테스트 루틴
    // node의 인증서를 생성하기 위해 ca의 키들을 이용
    public static X509Certificate createCert(PublicKey pubKey, PrivateKey caPrivKey, PublicKey caPubKey) throws Exception
        {
            // ca 정보 입력
            X500NameBuilder issuerBuilder = new X500NameBuilder();

            issuerBuilder.addRDN(BCStyle.CN, "root");
            issuerBuilder.addRDN(BCStyle.C, "AU");
            issuerBuilder.addRDN(BCStyle.O, "The Legion of the Bouncy Castle");
            issuerBuilder.addRDN(BCStyle.OU, "Bouncy Intermediate Certificate");
            issuerBuilder.addRDN(BCStyle.EmailAddress, "feedback-crypto@bouncycastle.org");

            // node 인증서 정보 입력
            X500NameBuilder subjectBuilder = new X500NameBuilder();

            subjectBuilder.addRDN(BCStyle.C, "AU");
            subjectBuilder.addRDN(BCStyle.O, "The Legion of the Bouncy Castle");
            subjectBuilder.addRDN(BCStyle.L, "Melbourne");
            subjectBuilder.addRDN(BCStyle.CN, "Eric H. Echidna");
            subjectBuilder.addRDN(BCStyle.EmailAddress, "feedback-crypto@bouncycastle.org");

            // 인증서 생성
            X509v3CertificateBuilder v3Bldr = new JcaX509v3CertificateBuilder(issuerBuilder.build(), BigInteger.valueOf(3),
                new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30), new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30)),
                subjectBuilder.build(), pubKey);

            //
            // extensions
            //
            JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

            v3Bldr.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                extUtils.createSubjectKeyIdentifier(pubKey));

            v3Bldr.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                extUtils.createAuthorityKeyIdentifier(caPubKey));

            X509CertificateHolder certHldr = v3Bldr.build(new JcaContentSignerBuilder("SHA1WithRSA").build(caPrivKey));

            X509Certificate cert = new JcaX509CertificateConverter().setProvider("SUN").getCertificate(certHldr);

            cert.checkValidity(new Date());

            cert.verify(caPubKey);

            

            return cert;
        }
    
    // 사설인증서 생성을 위한 루틴
    // node의 키를 이용하여 CSR  생성, 현재 테스트에만 이용
    public static PKCS10CertificationRequest generateRequest(
            KeyPair pair)
            throws Exception
        {
    		X500Principal subject = new X500Principal ("CN=root,C=AU,O=The Legion of the Bouncy Castle,OU=Bouncy Intermediate Certificate,EmailAddress=feedback-crypto@bouncycastle.org");
    		ContentSigner signGen = new JcaContentSignerBuilder("SHA1withRSA").build(pair.getPrivate());
    		PKCS10CertificationRequestBuilder builder = new JcaPKCS10CertificationRequestBuilder(subject, pair.getPublic());
    		PKCS10CertificationRequest csr = builder.build(signGen);
    		//// write to PEM file
    		//    		OutputStreamWriter output = new OutputStreamWriter(System.out);
    		//    		PEMWriter pem = new PEMWriter(output);
    		//    		pem.writeObject(request);
    		//    		pem.close();
    		return csr;
        }
    
    // node의 키를 생성하기 위한 루틴
    public KeyPair generateKeyPair(String alg, int keySize) {
        try{
            KeyPairGenerator keyPairGenerator = null;
            keyPairGenerator = KeyPairGenerator.getInstance(alg);
             
            keyPairGenerator.initialize(keySize, new SecureRandom());
             
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         
        return null;
    }
    
    // 루트 인증서, node 인증서, node 키 저장 루틴
	// tomcat 기본 폴더의 \key\test_keystore.jks에 저장
    public boolean storeKey(X509Certificate rootCert, X509Certificate pmsCert, KeyPair pmsKeyPair) {
    	KeyStore store;
		try {
			// 키스토어 인스턴스 획득
			store = KeyStore.getInstance("JKS");
			char[]   password = "00cola".toCharArray();
	        // create and save a JKS store
	        store.load(null, null);
	        
	        // 인증서 루트 정렬
	        Certificate[] chain = new Certificate[2];
	        chain[0] = pmsCert;
	        if(rootCert != null) {
	        	chain[1] = rootCert;
	        }
	        // set the entries
	        // 인증서 루트 설정
	        store.setCertificateEntry("root", rootCert);
	        //store.setKeyEntry("end", pmsKeyPair.getPrivate(), password, chain);
	        //인증서 저장
	        store.store(new FileOutputStream(System.getProperty("catalina.home") + "\\keys\\test_keystore.jks"), password);
	        return true;
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }
    
    // 인증서 유효성 검증 루틴, 테스트를 위한 루틴이며 현재 사용하지 않음
    public void checkCertificate(X509Certificate pmsCert) {
    	
    	KeyStore trustStore;
    	TrustManagerFactory trustManagerFactory = null;
    	X509Certificate[] chain = new X509Certificate[10];
    	
		try {
			// 키스토어 접근
			trustStore = KeyStore.getInstance("JKS");
			// 유효한 ca 인증서들 획득
			String cacertPath = System.getProperty("java.home") + "\\lib\\security\\cacerts"; // Trust store path should be different by system platform.  
			trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default certification validation  

			// trustManagerFactor instance 획득
	    	trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(trustStore);
			// node 인증서 설정
			chain[0] = pmsCert;
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		InputStream inStream;
		try {
			// ca 인증서 로드
			inStream = new FileInputStream("C:\\gsdev\\itc_cert\\inter.crt");
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			// ca인증서를 인증서 체인에 추가
			chain[1] = (X509Certificate)cf.generateCertificate(inStream);
			inStream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("JVM Default Trust Managers:");
		for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
		    System.out.println(trustManager);

		    // 인증서 검증
		    if (trustManager instanceof X509TrustManager) {
		        X509TrustManager x509TrustManager = (X509TrustManager)trustManager;
		        System.out.println("\tAccepted issuers count : " + x509TrustManager.getAcceptedIssuers().length);
		        // 유효한 ca 인증서 출력
		        for(int i=0; i<x509TrustManager.getAcceptedIssuers().length; i++) {
		        	System.out.println(x509TrustManager.getAcceptedIssuers()[i].getIssuerDN());
					System.out.println(x509TrustManager.getAcceptedIssuers()[i].getSubjectDN().getName());
					System.out.println(x509TrustManager.getAcceptedIssuers()[i].getSubjectX500Principal());
		        }
		        System.out.println("===============================================");
		        System.out.println(chain[0].getSubjectX500Principal());
		        System.out.println(chain[0].getIssuerX500Principal());
		        System.out.println(chain[0].getCriticalExtensionOIDs());
				try {
					// 인증서 체인이 유효한지 검증
					((X509TrustManager) trustManager).checkServerTrusted(chain, "RSA");
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
    }

    // PKI 인증서가 경로상 유효한지 확인하는 루틴, 체인의 유효성을 확인
    public static void checkPKICertificatePath(X509Certificate endCert, X509Certificate interCert ) throws Exception { 
    	// create certificates and CRLs
    	
    	BigInteger revokedSerialNumber = BigInteger.valueOf(2); 
    	//X509CRL rootCRL = createCRL( rootCert, rootKeyPair.getPrivate(), revokedSerialNumber); 
    	// create CertStore to support validation
    	//List list = new ArrayList(); 
    	//list.add(interCert); 
    	//list.add(endCert); 
    	//list.add(rootCRL); 
    	//CollectionCertStoreParameters params = new CollectionCertStoreParameters( list); 
    	//CertStore store = CertStore.getInstance( "Collection", params, "SUN"); 
    	// create certificate path 
    	CertificateFactory fact = CertificateFactory.getInstance("X.509", "SUN"); 
    	// 인증서 체인에 각 인증서 추가
    	List certChain = new ArrayList(); 
    	certChain.add(endCert); 
    	if(interCert != null) {
    		certChain.add(interCert);
    	}
    	CertPath certPath = fact.generateCertPath(certChain); 
    	
    	//Set trust = Collections.singleton(new TrustAnchor(interCert, null)); 
    	// perform validation 
    	
    	// 신뢰하는 ca 인증서 로드
    	KeyStore trustStore;
    	trustStore = KeyStore.getInstance("JKS");
		String cacertPath = System.getProperty("java.home") + "\\lib\\security\\cacerts"; // Trust store path should be different by system platform.  
		trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default certification validation
    	
		// 인증서 체인 인증을 위해 trustStore 설정 및 날짜 설정
    	CertPathValidator validator = CertPathValidator.getInstance("PKIX", "SUN"); 
    	//PKIXParameters param = new PKIXParameters(trust); 
    	PKIXParameters param = new PKIXParameters(trustStore);
    	param.setRevocationEnabled(false);
    	//param.addCertStore(store); 
    	param.setDate(new Date()); 
    	try { 
    		// 인증서 체인 유효성 검증
    		CertPathValidatorResult result = validator.validate(certPath, param); 
    		System.out.println("certificate path validated"); 
    	} catch (CertPathValidatorException e) { 
    		System.out.println("validation failed on certificate number " + e.getIndex() + ", details: " + e.getMessage()); 
    	} 
    }
    
    // 사설 인증서의 체인 유효성 검증, 사설인증기능 구현할 때 필요
    public static void checkPrivateCertificatePath(X509Certificate endCert, X509Certificate rootCert ) throws Exception { 
    	// 사설인증서의 trustStore를 로드
    	KeyStore trustStore;
    	trustStore = KeyStore.getInstance("JKS");
		String cacertPath = "C:\\Users\\sskim\\keystore.jks"; // Trust store path should be different by system platform.  
		trustStore.load(new FileInputStream(cacertPath), "00cola".toCharArray()); // Use default certification validation  
		
		// 인증서 체인 구성
        List list = new ArrayList();
        list.add(trustStore.getCertificate("root"));
        //list.add(endCert);
        CollectionCertStoreParameters params = new CollectionCertStoreParameters( list );
        CertStore                     store = CertStore.getInstance("Collection", params, "SUN");

        // create certificate path
        CertificateFactory fact = CertificateFactory.getInstance("X.509", "SUN");
        List               certChain = new ArrayList();
        certChain.add(endCert);
        //certChain.add(rootCert);
        
        CertPath certPath = fact.generateCertPath(certChain);
        Set      trust = Collections.singleton(new TrustAnchor(rootCert, null));

        // 인증서 체인 검증을 위한 trustStore 설정 및 날짜 설정
        CertPathValidator validator = CertPathValidator.getInstance("PKIX", "SUN");
        PKIXParameters    param = new PKIXParameters(trust);
        
        param.setRevocationEnabled(false);
        param.addCertStore(store);
        param.setDate(new Date());
        
        try
        {
        	// 인증서 체인 검증
            CertPathValidatorResult result = validator.validate(certPath, param);

            System.out.println("certificate path validated");
        }
        catch (CertPathValidatorException e)
        {
            System.out.println("validation failed on certificate number " + e.getIndex() + ", details: " + e.getMessage());
        }
    }

    // 인증서 폐기 목록 생성, 사설인증 기능 구현시 필요
    public static X509CRL createCRL( X509Certificate caCert, PrivateKey caPrivateKey, BigInteger revokedSerialNumber) throws Exception
    {
    	try {
    		X500Name issuerDN = new X500Name(PrincipalUtil.getIssuerX509Principal(caCert).getName());
    		X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(issuerDN, new Date());

    		// build and sign CRL with CA private key
    		ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("SUN").build(caPrivateKey);
    		X509CRLHolder crl = crlBuilder.build(signer);
    		JcaX509CRLConverter converter = new JcaX509CRLConverter();
    		return converter.getCRL(crl);
    	} catch (Exception e) {
    		throw new RuntimeException("Failed to create new certificate revocation list ", e);
    	}
    }
    
    // 인증서와 개인키를 파일에 저장
    public boolean storeCertificateFileAndPrivateKeyFile(byte[] privateKeyBytes, byte[] certificateBytes) {
    	
    	KeyFactory kf;
		try {
			kf = KeyFactory.getInstance("RSA");
			// 개인키 로드
			PrivateKey publicKey = kf.generatePrivate(new X509EncodedKeySpec(privateKeyBytes));
			// 인증서 저장, 개인키 저장부분 구현되지 않음, 나중에 사설인증 기능 구현시 구현
	    	CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
	    	InputStream in = new ByteArrayInputStream(certificateBytes);
	    	X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} // or "EC" or whatever
		catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
        return true;
    }
    
    // PMS의 인증서와 개인키를 검색하는 루틴
    public InfoType_Certificate getPMSCertificateAndPrivateKey() {
    	return selectCertificateInfo();
    }
}