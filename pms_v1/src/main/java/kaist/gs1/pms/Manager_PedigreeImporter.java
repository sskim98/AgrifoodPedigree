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
import javax.persistence.AttributeNode;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.Attributes;
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
 * pedigree  수신 관리자
 */
@Component
public class Manager_PedigreeImporter extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    
    // 이전에 수신된 동일한 sgtin의 pedigree가 있는 지 검색하는 루틴
    public InfoType_Pedigree Find_Pedigree(String sgtin) {
    	InfoType_Pedigree pedigree = selectPedigree(sgtin);
        return pedigree;
    }
    
    // 수신된 pedigree 에 대한 저장을 위한 루틴
    public boolean Import_Pedigree(String xml) {
    	// 수신한 pedigree를 document로 변경
    	Document doc_import = buildDocumentfromString(xml);
    	
    	// 이전 pedigree에서 id(sgtin)를 찾기 위한 루틴
    	NodeList nodeList = doc_import.getElementsByTagName("initialPedigree");
    	if(nodeList.getLength() == 0) {
    		nodeList = doc_import.getElementsByTagName("shippedPedigree");
    		if(nodeList.getLength() == 0) {
        		nodeList = doc_import.getElementsByTagName("receivedPedigree");
        		if(nodeList.getLength() == 0) {
        			return false;
        		}
        	}
    	}
    	Node attributeNode = nodeList.item(0).getAttributes().getNamedItem("id");
    	
    	String sgtin;
    	if(attributeNode != null) {
    		// sgtin이 발견되면 sgtin과 xml을 넘겨서 pedigree 타입 생성, 상태는 Imported로 기록
    		sgtin = attributeNode.getNodeValue();
    		InfoType_Pedigree importPedigree = new InfoType_Pedigree(sgtin , sgtin, "Imported", getCurrentTime(), "", xml); 
    		boolean status = savePedigree(importPedigree);
    		return status;
    	}
    	return false;
    }
    
    
}