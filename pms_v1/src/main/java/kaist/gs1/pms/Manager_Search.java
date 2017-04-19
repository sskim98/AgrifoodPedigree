package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
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
import com.fasterxml.jackson.dataformat.xml.ser.XmlSerializerProvider;

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
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * 모바일의 pedigree 검색을 응답하는 관리자
 */
@Component
public class Manager_Search extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    
    // sgtin으로 pedigree를 검색하는 루틴
    public InfoType_Pedigree Find_Pedigree(String sgtin) {
    	InfoType_Pedigree pedigree = selectPedigree(sgtin);
        return pedigree;
    }
    
    // 특정 pedigree의 이동경로정보를 얻기 위한 루틴
    public ArrayList<InfoType_Trace> Get_Pedigree_TraceInfo(String pedigree) {
    	ArrayList<InfoType_Trace> report = new ArrayList<InfoType_Trace>();
    	
    	String pedString = pedigree;
    	Document pedDoc = null;
    	// pedigree 내 모든 trace 정보를 추출, 단 initial pedigree 정보는 while문 아래에서 처리
    	while(pedString.length() > 0) {
    		// pedigree를 document 형태로 변환
    		pedDoc = buildDocumentfromString(pedString);
    		InfoType_Trace element = Analyze_Pedigree(getStringFromDocument(pedDoc));
    		if(element == null) {
    			break;
    		}
    		else {
    			// 발견된 trace 정보를 report에 저장
    			report.add(element);
    			// 내부 pedigree element를 추출
    			Node nextElements = pedDoc.getElementsByTagName("DataType_Pedigree").item(1);
    			// 내부 pedigree element를 pedString에 저장함으로 recursive 한 동작 수행
    			pedString = getStringFromXmlNode(nextElements);
    		}
    	}
    	
    	// 마지막에 발견된  pedigree가 initial pedigree이면
    	Node initialPedigree = pedDoc.getElementsByTagName("DataType_InitialPedigree").item(0); 
    	if( initialPedigree != null) {
    		// 생성자 정보 추출
    		String company = pedDoc.getElementsByTagName("manufacturer").item(0).getFirstChild().getTextContent();
        	String gtin = pedDoc.getElementsByTagName("serialNumber").item(0).getFirstChild().getTextContent();
        	String type = initialPedigree.getNodeName();
        	//String eventTime = pedDoc.getElementsByTagName("eventTime").item(0).getFirstChild().getTextContent();
        	InfoType_Trace element = new InfoType_Trace(gtin, company, type, "", "-");
        	// 마지막 trace 정보를 추가
        	report.add(element);
    	}
    	// 모든 trace 정보를 리턴
    	return report;
    }
    
    // XML 노드로부터 이하 내용을 담은 String을 추출
    private String getStringFromXmlNode(Node node) {
    	if(node != null) {
    		TransformerFactory tFactory = TransformerFactory.newInstance();
    		Transformer transformer = null;
    		try {
    			transformer = tFactory.newTransformer();
    		} catch (TransformerConfigurationException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		StringWriter sw = new StringWriter();
    		StreamResult result = new StreamResult(sw);

    		//Find the first child node - this could be done with xpath as well
    		DOMSource source = null;

    		if(node instanceof Element)
    		{
    			source = new DOMSource(node);
    		}

    		//Do the transformation and output
    		try {
    			transformer.transform(source, result);
    		} catch (TransformerException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return sw.toString();
    	}
    	else {
    		return "";
    	}
    }

    // pedigree 이동경로 정보를 분석하기 위한 루틴
    public InfoType_Trace Analyze_Pedigree(String pedigree) {
    	// pedigree에 대한 document 생성
    	Document pedDoc = buildDocumentfromString(pedigree);
    	// 생산자 정보 획득
    	String company = pedDoc.getElementsByTagName("manufacturer").item(0).getFirstChild().getTextContent();
    	String gtin = pedDoc.getElementsByTagName("serialNumber").item(0).getFirstChild().getTextContent();
    	String type = pedDoc.getFirstChild().getFirstChild().getNodeName();
    	//String eventTime = pedDoc.getElementsByTagName("eventTime").item(0).getFirstChild().getTextContent();
    	String validity = "TRUE";
    	// 발견된 pedigree가 initial pedigree가 아니면 유효성 체크하고 회사 정보를 담은 Trace 타입 정보를 생성
    	if(pedDoc.getFirstChild().getNodeName() != "DataType_InitialPedigree") {
    		validity = String.valueOf(checkPedigree(pedigree));
    	}
    	InfoType_Trace element = new InfoType_Trace(gtin, company, type, "", validity);
    	return element;
    }
}