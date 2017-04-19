package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * 서명을 위한 기능을 모은 서명 관리자
 */
//@Component
public class BaseManager_Signer {

	private static final Logger logger = Logger.getLogger(BaseManager_Info.class);

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

	// 현재 시간을 얻기 위한 루틴
	public String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	// pedigree 서명을 위한 루틴, pedigee document, 개인키, 인증서가 필요
	public String signPedigree(Document doc, PrivateKey privateKey, X509Certificate cert, boolean isPKICertificate ) {
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

		/***************
		 *  code to support private certificate ********************************
		 *  사설 인증서인 경우  trustStore로 지정된 파일을 로드, 사설인증서를 서명한 root 인증서
		 */
		if(isPKICertificate == false) {
			KeyStore ks = null;
			try {
				ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream("C:\\Users\\sskim\\keystore.jks"), "00cola".toCharArray());
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

			//PMS의 keyStore를 로드
			KeyStore.PrivateKeyEntry keyEntry = null;
			try {
				keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("end", new KeyStore.PasswordProtection("00cola".toCharArray()));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnrecoverableEntryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			privateKey = keyEntry.getPrivateKey();
			cert = (X509Certificate) keyEntry.getCertificate();
		}
		else {
			
		

		/***************
		 *  code to support PKI certificate ********************************
		 *  PKI 인증서인 경우 전달된 개인키 및 인증서를 이용하여 서명
		 *  */
			
			
		}

		// 인증서를 기반으로 keyInfo를 추출
		KeyInfoFactory kif = fac.getKeyInfoFactory();
		List x509Content = new ArrayList();
		x509Content.add(cert.getSubjectX500Principal().getName());
		x509Content.add(cert);
		X509Data xd = kif.newX509Data(x509Content);
		KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

		// 서명을 위한 context를 생성, 인자로 개인키와 서명할 document의 element 전달
		DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
	
		//dsc.setIdAttributeNS((Element)doc.getFirstChild().getFirstChild(), doc.getFirstChild().getFirstChild().getAttributes().item(0).getNodeValue(), "namespace");

		//"#"+new URI(doc.getFirstChild().getFirstChild().getAttributes().item(0).getNodeValue()).getPath()
		// Create the XMLSignature, but don't sign it yet.
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
		/*
		xml = "<Pedigree>"+xml+"<Signature>"+base64encodedString+"</Signature></Pedigree>";

		Document docc = null;
		try {
			docc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 */
		
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
	public boolean checkPedigree(String ped) {
		// string으로 전달된 pedigee를 document 타입으로 변환
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = null;
		try {
			doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(ped)));
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
					InputStream is = ((Reference) i.next()).getDigestInputStream();
					boolean refValid = false;
					try {
						refValid = ((Reference) i.next()).validate(valContext);
					} catch (XMLSignatureException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("ref["+j+"] validity status: " + refValid);
				}
			}
		} else {
			System.out.println("Signature passed core validation");
		}

		return coreValidity;
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
}