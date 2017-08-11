package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/*
 * 서명을 위한 기능을 모은 서명 관리자
 */
//@Component
public class BaseManager_Signer {

	private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
	private static InfoType_Error errorMsg = new InfoType_Error("", "");
	
	public BaseManager_Signer() {
	}
	// string으로 전달된 xml 문서를 document 로 변경하는 루틴
	public Document buildDocumentfromString(String str_xml) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = null;
		try {
			doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(str_xml)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	// object를 document 로 변경하는 루틴
	public Document buildDocumentfromObject(Object obj) {
		ObjectMapper xmlMapper = new XmlMapper();
		String xml= "";
		try {
			xml = xmlMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = null;
		try {
			doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;
	}

	// ducument를 string 로 변경하는 루틴
	public String getStringFromDocument(Document doc) {
		StringWriter str = new StringWriter();
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(str)); 
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringBuffer sb = str.getBuffer();
		String string = sb.toString();
		String result = string.replaceAll("\r", "").replaceAll("\n", "");
		return result;
	}

	// xml내 특정 element를 string으로 변환하는 루틴
	public String getStringFromElement(Element element) {
		StringWriter buffer = new StringWriter();
		try {
			TransformerFactory.newInstance().newTransformer().transform( new DOMSource(element), new StreamResult(buffer) );
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buffer.toString();
	}

	// 현재 시간을 문자열로 얻기 위한 루틴
	public String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	// pedigree 서명을 위한 루틴, pedigee document, 개인키, 인증서가 필요
	public String signPedigree(Document doc, PrivateKey privateKey, X509Certificate pmsCertificate, ArrayList<X509Certificate> caCertificateArray) {
		// Create a DOM XMLSignatureFactory that will be used to
		// generate the enveloped signature.
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

		// Create a Reference to the enveloped document (in this case,
		// you are signing the whole document, so a URI of "" signifies
		// that, and also specify the SHA1 digest algorithm and
		Element element = (Element)(doc.getFirstChild().getFirstChild());
		element.setIdAttribute("id", true);
		// the ENVELOPED Transform.
		Reference ref;
		SignedInfo signedInfo=null;
		// digest  알고리즘 설정, 서명 알고리즘 설정
		try {
			ref = fac.newReference(
					"", 
					fac.newDigestMethod(DigestMethod.SHA1, null), 
					Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), 
					null, 
					null);
			// Create the SignedInfo.
			signedInfo = fac.newSignedInfo(
					fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
					fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
					Collections.singletonList(ref));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 인증서를 기반으로 keyInfo를 추출
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		List x509Content = new ArrayList();
		x509Content.add(pmsCertificate.getSubjectX500Principal().getName());
		x509Content.add(pmsCertificate);
		Iterator<X509Certificate> certIterator = caCertificateArray.iterator();
    	for(int i=0; certIterator.hasNext(); i++){
    		X509Certificate caCert = certIterator.next();
    		x509Content.add(caCert.getSubjectX500Principal().getName());
    		x509Content.add(caCert);
    	}
		
		X509Data xd = kif.newX509Data(x509Content);
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

		// 서명을 위한 context를 생성, 인자로 개인키와 서명할 document의 element 전달
		DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
		// 서명정보 생성
		XMLSignature signature = fac.newXMLSignature(signedInfo, ki);

		// Marshal, generate, and sign the enveloped signature.
		try {
			// 서명 실행
			signature.sign(dsc);
		} catch (MarshalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// 서명 객체로부터 서명 값을 base64 형태로 추출
		String base64encodedString = Base64.getEncoder().encodeToString(signature.getSignatureValue().getValue());
		
		// java api로 이것을 사용하면 서명 알고리즘, 서명 정보 등이 formatting되어 얻어짐
		StringWriter str = new StringWriter();
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			//trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			//trans.transform(new DOMSource(doc), new StreamResult(os));
			trans.transform(new DOMSource(doc), new StreamResult(str)); 
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//최종 생성된 서명된 xml을 newline 삭제후 리턴
		StringBuffer sb = str.getBuffer();
		String string = sb.toString();

		string.replaceAll("\r", "").replaceAll("\n", "");
		String OnelineString = string.replaceAll("\r", "").replaceAll("\n", "");

		return OnelineString;
	}

	// pedgiree 유효성 검증 루틴
	public InfoType_Error checkPedigree(String ped) {
		Document doc = buildDocumentfromString(ped);

		// 서명정보 리스트 추출
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) { 
			try {
				throw new Exception("Cannot find Signature element");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		// Create a DOMValidateContext and specify a KeySelector
		// and document context.
		// Find Signature element.
		// 서명정보 중 가장 바깥쪽 서명정보를 활용하여 validatecontext 생성
		DOMValidateContext valContext = new DOMValidateContext
				(new X509KeySelector(), nl.item(nl.getLength()-1));

		// Unmarshal the XMLSignature.
		XMLSignature signature = null;
		try {
			// 서명정보 추출
			signature = fac.unmarshalXMLSignature(valContext);
		} catch (MarshalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Validate the XMLSignature.
		boolean coreValidity = false;
		try {
			//서명 유효성 검증
			coreValidity = signature.validate(valContext);
		} catch (XMLSignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Check core validation status.
		if (coreValidity == false) {
			// 서명 검증이 실패했을 경우 
			System.err.println("Signature failed core validation");
			boolean sv = false;
			try {
				sv = signature.getSignatureValue().validate(valContext);
			} catch (XMLSignatureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("signature validation status: " + sv);
			if (sv == false) {
				// Check the validation status of each Reference.
				Iterator i = signature.getSignedInfo().getReferences().iterator();
				for (int j=0; i.hasNext(); j++) {
					Reference ref = (Reference)i.next();
					InputStream is = ref.getDigestInputStream();
					boolean refValid = false;
					try {
						refValid = ref.validate(valContext);
					} catch (XMLSignatureException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("ref["+j+"] validity status: " + refValid);
				}
			}
			errorMsg.setCode("-1");
			errorMsg.setDetail("Signature validation status: " + sv);
			return errorMsg;
			
		} else {
			// 서명에 대한 검증이 끝나면 인증서 경로 검증 수행
			System.out.println("Signature passed core validation");
			
			///////////////////////////// Certificate Validation Check /////////////////////////////
			// 인증서 정보 획득
			Element signatureElement = (Element)(nl.item(nl.getLength()-1));
			// pedigree에 있는 모든 인증서 추출
			NodeList certificateStringArray = signatureElement.getElementsByTagNameNS(XMLSignature.XMLNS, "X509Certificate");
			ArrayList<X509Certificate> certificates = new ArrayList<X509Certificate>();
			if(certificateStringArray.getLength() > 0) {
				for(int i=0; i<certificateStringArray.getLength(); i++) {
					certificates.add(getX509CertificatefromString("-----BEGIN CERTIFICATE-----\n" + certificateStringArray.item(i).getTextContent() + "\n-----END CERTIFICATE-----\n"));
				}
			}
			// 첫번째 인증서는 PMS의 인증서로 인식
			X509Certificate pmsCertificate = certificates.get(0);
			certificates.remove(0);
			try {
				// 인증서 경로 검증
				errorMsg = checkPKICertificatePath(pmsCertificate, certificates);
				if(!errorMsg.getCode().equals("0")) {
					errorMsg = checkPrivateCertificatePath(pmsCertificate, certificates);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/////////////////////////////////////////////////////////////////////////////////////////
			return new InfoType_Error(errorMsg.getCode(), errorMsg.getDetail());
		}
	}

	// pedgiree 유효성 검증 루틴 : 1개의 pedigree 내에 있는 모든 pedigree들에 대한 검증 수행
	public InfoType_Error checkAllNestedPedigree(String ped) {
		Document doc = buildDocumentfromString(ped);
		
		// recursively checking pedigree
		InfoType_Error childErrorMsg = new InfoType_Error("-1", "");
		NodeList nl = doc.getElementsByTagName("pedigree");
		if (nl.getLength() > 1) { 
			Element element = (Element)(nl.item(1));
			childErrorMsg = checkAllNestedPedigree(getStringFromElement(element));
		}
		
		InfoType_Error parentErrorMsg = new InfoType_Error("-1", "");
		parentErrorMsg = checkPedigree(ped);
		
		if(nl.getLength() > 1) {
			if(!childErrorMsg.getCode().equals("0")) {
				return childErrorMsg;
			}
			else {
				return parentErrorMsg;
			}
		}
		else {
			return parentErrorMsg;
		}
	}
		
	// keyInfo에서 X509 타입 키를 찾아 리턴
	public class X509KeySelector extends KeySelector {
		public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method, XMLCryptoContext context)	throws KeySelectorException {
			Iterator ki = keyInfo.getContent().iterator();
			while (ki.hasNext()) {
				XMLStructure info = (XMLStructure) ki.next();
				if (!(info instanceof X509Data))
					continue;
				X509Data x509Data = (X509Data) info;
				Iterator xi = x509Data.getContent().iterator();
				while (xi.hasNext()) {
					Object o = xi.next();
					if (!(o instanceof X509Certificate))
						continue;
					final PublicKey key = ((X509Certificate)o).getPublicKey();
					// Make sure the algorithm is compatible with the method.
					if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
						return new KeySelectorResult() {
							public Key getKey() { return key; }
						};
					}
				}
			}
			throw new KeySelectorException("No key found!");
		}

		// 키 알고리즘이 DSA_SHA1이거나 RSA_SHA1인 경우 true
		boolean algEquals(String algURI, String algName) {
			if ((algName.equalsIgnoreCase("DSA") &&
					algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) ||
					(algName.equalsIgnoreCase("RSA") &&
							algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
				return true;
			} else {
				return false;
			}
		}
	}

	// string 타입 키로부터 키 타입 데이터를 얻는 루틴
	public PrivateKey getPrivateKeyfromString (String pem) {
		BufferedReader bufferedReader = new BufferedReader(new StringReader(pem));
		Security.addProvider(new BouncyCastleProvider());
		PEMParser pemParser = new PEMParser(bufferedReader);
		PEMKeyPair pemKeyPair = null;
		try {
			pemKeyPair = (PEMKeyPair) pemParser.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		KeyPair kp = null;
		try {
			kp = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
		} catch (PEMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			pemParser.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return kp.getPrivate();
	}
	
	// string으로부터 X509 타입 인증서를 얻는 루틴
	public X509Certificate getX509CertificatefromString(String crtString) {
		X509Certificate cert = null;
		if(crtString != null) {
			if(crtString.length() > 0) {
				try {
					cert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(crtString.getBytes("UTF-8")));
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return cert;
	}
	
	// PKI 인증서가 경로상 유효한지 확인하는 루틴, 체인의 유효성을 확인
    public static InfoType_Error checkPKICertificatePath(X509Certificate endCert, ArrayList<X509Certificate> caCertArray ) throws Exception { 
    	BigInteger revokedSerialNumber = BigInteger.valueOf(2); 
    	// create certificate path 
    	CertificateFactory fact = CertificateFactory.getInstance("X.509", "SUN"); 
    	// 인증서 체인에 각 인증서 추가
    	List certChain = new ArrayList(); 
    	certChain.add(endCert); 
    	if(caCertArray != null) {
    		if(!caCertArray.isEmpty()) {
   				Iterator<X509Certificate> certIterator = caCertArray.iterator();
   		    	for(int i=0; certIterator.hasNext(); i++){
   			        certChain.add(certIterator.next());
   		    	}
    		}
    	}
    	CertPath certPath = fact.generateCertPath(certChain); 
    	
    	// perform validation 
    	
    	// 신뢰하는 ca 인증서 로드
    	KeyStore trustStore;
    	trustStore = KeyStore.getInstance("JKS");
		String cacertPath = System.getProperty("java.home") + "\\lib\\security\\cacerts"; // Trust store path should be different by system platform.  
		trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default certification validation
    	
		// 인증서 체인 인증을 위해 trustStore 설정 및 날짜 설정
    	CertPathValidator validator = CertPathValidator.getInstance("PKIX", "SUN"); 
    	PKIXParameters param = new PKIXParameters(trustStore);
    	param.setRevocationEnabled(false);
    	param.setDate(new Date()); 
    	try { 
    		// 인증서 체인 유효성 검증
    		CertPathValidatorResult result = validator.validate(certPath, param); 
    		System.out.println("certificate path validated"); 
    		errorMsg.setCode("0");
    		errorMsg.setDetail("PKI Certificate Validation Check : Valid");
    	} catch (CertPathValidatorException e) { 
    		System.out.println("validation failed on certificate number " + e.getIndex() + ", details: " + e.getMessage());
    		errorMsg.setCode(""+e.getIndex());
    		errorMsg.setDetail("PKI Certificate Validation Check : Not Valid \n" + "details: " + e.getMessage());
    	} 
    	
    	return errorMsg;
    }
    
 // PKI 인증서가 경로상 유효한지 확인하는 루틴, 체인의 유효성을 확인
    public static InfoType_Error checkPrivateCertificatePath(X509Certificate endCert, ArrayList<X509Certificate> caCertArray) throws Exception { 
    	BigInteger revokedSerialNumber = BigInteger.valueOf(2); 
    	CertificateFactory fact = CertificateFactory.getInstance("X.509", "SUN"); 
    	// 인증서 체인에 각 인증서 추가
    	List certChain = new ArrayList(); 
    	certChain.add(endCert); 
    	if(caCertArray != null) {
    		if(!caCertArray.isEmpty()) {
   				Iterator<X509Certificate> certIterator = caCertArray.iterator();
   		    	for(int i=0; certIterator.hasNext(); i++){
   			        certChain.add(certIterator.next());
   		    	}
    		}
    	}
    	CertPath certPath = fact.generateCertPath(certChain); 
    	
    	// 신뢰하는 ca 인증서 로드
    	KeyStore trustStore;
    	trustStore = KeyStore.getInstance("JKS");
		String cacertPath = System.getProperty("catalina.home") + "\\keys\\privateKeystore.jks"; // Trust store path should be different by system platform.  
		trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default certification validation
    	
		if(trustStore.size() > 0) {
			// 인증서 체인 인증을 위해 trustStore 설정 및 날짜 설정
	    	CertPathValidator validator = CertPathValidator.getInstance("PKIX", "SUN"); 
	    	PKIXParameters param = new PKIXParameters(trustStore);
	    	param.setRevocationEnabled(false);
	    	param.setDate(new Date()); 
	    	try { 
	    		// 인증서 체인 유효성 검증
	    		CertPathValidatorResult result = validator.validate(certPath, param); 
	    		System.out.println("certificate path validated"); 
	    		errorMsg.setCode("0");
	    		errorMsg.setDetail("Private Certificate Validation Check : Valid");
	    	} catch (CertPathValidatorException e) { 
	    		System.out.println("validation failed on certificate number " + e.getIndex() + ", details: " + e.getMessage()); 
	    		errorMsg.setCode(""+e.getIndex());
	    		errorMsg.setDetail("Private Certificate Validation Check : Not Valid \n" + "details: " + e.getMessage());
	    	} 
		}
		else {
			System.out.println("validation failed, " + ", details: there is no entry in private trustStore"); 
			errorMsg.setCode("100");
    		errorMsg.setDetail("Private Certificate Validation Check : Not Valid \n" + "details: there is no entry in private trustStore");
		}
    	
    	return errorMsg;
    }
    
    // 인증서를 PKI TrustStore에 저장 : 현재 java.home에 있는 trustStore 이용
    public boolean insertCertificateToPKITrustStore(X509Certificate certificate) throws CertificateException, FileNotFoundException, IOException {
		try {
			KeyStore trustStore;
	    	trustStore = KeyStore.getInstance("JKS");
			String cacertPath = System.getProperty("java.home") + "\\lib\\security\\cacerts"; // Trust store path should be different by system platform.  
			trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default certification validation
			trustStore.setCertificateEntry(certificate.getSerialNumber().toString(16), certificate);
			trustStore.store(new FileOutputStream(cacertPath), "changeit".toCharArray());
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
    
 // 인증서를 PKI TrustStore에서 삭제
    public boolean deleteCertificateFromPKITrustStore(X509Certificate certificate) throws CertificateException, FileNotFoundException, IOException {
		try {
			KeyStore trustStore;
	    	trustStore = KeyStore.getInstance("JKS");
			String cacertPath = System.getProperty("java.home") + "\\lib\\security\\cacerts"; // Trust store path should be different by system platform.  
			trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default certification validation
			trustStore.deleteEntry(certificate.getSerialNumber().toString(16));
			trustStore.store(new FileOutputStream(cacertPath), "changeit".toCharArray());
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
    
    // 인증서를 private TrustStore에 저장 : 현재 tomcat home에 있는 privateKeyStore 이용, 파일이 없다면 생성한 후 저장
    public boolean insertCertificateToPrivateTrustStore(X509Certificate certificate) throws CertificateException, FileNotFoundException, IOException {
		try {
			KeyStore trustStore;
	    	trustStore = KeyStore.getInstance("JKS");
			String privateCertificatePath = System.getProperty( "catalina.home" ) + "\\keys\\privateKeystore.jks"; // Trust store path should be different by system platform.  
			trustStore.load(new FileInputStream(privateCertificatePath), "changeit".toCharArray()); // Use default certification validation
			trustStore.setCertificateEntry(certificate.getSerialNumber().toString(16), certificate);
			trustStore.store(new FileOutputStream(privateCertificatePath), "changeit".toCharArray());
		} catch (FileNotFoundException e) {
			KeyStore trustStore;
	    	try {
				trustStore = KeyStore.getInstance("JKS");
				String privateCertificatePath = System.getProperty( "catalina.home" ) + "\\keys\\privateKeystore.jks"; // Trust store path should be different by system platform.
				trustStore.load(null, "changeit".toCharArray()); // Use default certification validation
				trustStore.setCertificateEntry(certificate.getSerialNumber().toString(16), certificate);
				trustStore.store(new FileOutputStream(privateCertificatePath), "changeit".toCharArray());
			} catch (KeyStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
    
 // 인증서를 private TrustStore에서 삭제 : 파일이 없다면 새로 생성함
    public boolean deleteCertificateFromPrivateTrustStore(X509Certificate certificate) throws CertificateException, FileNotFoundException, IOException {
		try {
			KeyStore trustStore;
	    	trustStore = KeyStore.getInstance("JKS");
			String cacertPath = System.getProperty( "catalina.home" ) + "\\keys\\privateKeystore.jks"; // Trust store path should be different by system platform.  
			trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default certification validation
			trustStore.deleteEntry(certificate.getSerialNumber().toString(16));
			trustStore.store(new FileOutputStream(cacertPath), "changeit".toCharArray());
		} catch (FileNotFoundException e) {
			KeyStore trustStore;
			try {
				trustStore = KeyStore.getInstance("JKS");
				String privateCertificatePath = System.getProperty( "catalina.home" ) + "\\keys\\privateKeystore.jks"; // Trust store path should be different by system platform.
				trustStore.load(null, "changeit".toCharArray()); // Use default certification validation
				trustStore.store(new FileOutputStream(privateCertificatePath), "changeit".toCharArray());
			} catch (KeyStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
}