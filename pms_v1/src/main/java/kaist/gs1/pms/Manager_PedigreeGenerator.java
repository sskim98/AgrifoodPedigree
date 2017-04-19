package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

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
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * pedigree 생성 관리자
 */
@Component
public class Manager_PedigreeGenerator extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    // pedigree 검색 루틴
    public InfoType_Pedigree Find_Pedigree(String sgtin) {
    	InfoType_Pedigree pedigree = selectPedigree(sgtin);
        return pedigree;
    }
    
    // initial pedigree 직접 생성 루틴
    public String Generate_InitialPedigree(String sgtin, String productName, String manufacturer, String productCode, String containerSize, String lot, String expirationDate, String quantity, String itemSerialNumber) {
    	if(sgtin.length() > 0) {
    		// 동일한 sgtin pedigree 가 없으면
	    	InfoType_Pedigree previousPedigree = Find_Pedigree(sgtin);
	    	if(previousPedigree == null) {
	    		// sgtin을 gtin과 serial 로 구분
	    		String[] array = sgtin.replaceAll(".*:", "").split("\\.");
	    		String gtin = array[0] + array[1];
	    		InfoType_Product product = selectProductInfo(gtin);
	    		// initial pedigree를 생성하고 xml과 같이 저장
		    	initialPedigree initialPedigreeData = new initialPedigree(sgtin, productName, manufacturer, productCode, containerSize, lot, expirationDate, quantity, itemSerialNumber);
		    	Document doc_initial = buildDocumentfromObject(initialPedigreeData);
		    	String xml = getStringFromDocument(doc_initial);
		    	InfoType_Pedigree initialPedigree = new InfoType_Pedigree(sgtin , sgtin, "Initial", getCurrentTime(), "", xml); 
		    	boolean status = savePedigree(initialPedigree);
		    	return xml;
	    	}
	    	else {
	    		return "false";
	    	}
    	}
    	else {
    		return "false";
    	}
    }
    // initial pedigree 를 EPCIS event 를 기반으로 생성하는 루틴
    public String Generate_InitialPedigree(String sgtin, String eventXml) {
    	if(sgtin.length() > 0) {
    		// 저장된 initial pedigree 중에서 sgtin으로 검색하여 중복되는 것이 없으면
	    	InfoType_Pedigree previousPedigree = Find_Pedigree(sgtin);
	    	if(previousPedigree == null) {
	    		// sgtin을 gtin과 serial로 구분
	    		String[] array = sgtin.replaceAll(".*:", "").split("\\.");
	    		String gtin = array[0] + array[1];
	    		InfoType_Product product = selectProductInfo(gtin);
	    		// initial pedigree 생성시에 event 정보와 product 정보를 넘겨서 자동으로 생성하도록 함  
		    	initialPedigree initialPedigreeData = new initialPedigree(sgtin, product, buildDocumentfromString(eventXml));
		    	//buildDocumentfromNestedObject(initialPedigreeData);
		    	// xml 빌드
		    	Document doc_initial = buildDocumentfromObject(initialPedigreeData);
		    	String pedXml = getStringFromDocument(doc_initial);
		    	// initial pedigree 생성 및 저장
		    	InfoType_Pedigree initialPedigree = new InfoType_Pedigree(sgtin , sgtin, "Initial", getCurrentTime(), "", pedXml); 
		    	boolean status = savePedigree(initialPedigree);
		    	return pedXml;
	    	}
	    	else {
	    		return "false";
	    	}
    	}
    	else {
    		return "false";
    	}
    }
    
    // shipped pedigee 직접 생성 루틴은 삭제
    /*
    public String Generate_ShippedPedigree(String sgtin) {
    	InfoType_Pedigree previousPedigree = Find_Pedigree(sgtin);
    	if(previousPedigree != null) {
    		pedigree pedigree = new pedigree();
    		Document doc = buildDocumentfromObject(pedigree);
    		
    		String[] array = sgtin.replaceAll(".*:", "").split("\\.");
    		String gtin = array[0] + array[1];
    		InfoType_Product product = selectProductInfo(gtin);
    		
    		shippedPedigree shippedPedigreeData = new shippedPedigree(sgtin, product);
    		Document doc_shipped = buildDocumentfromObject(shippedPedigreeData);
    		Document doc_previous = buildDocumentfromString(previousPedigree.getXml());
    		doc_shipped.getDocumentElement().replaceChild(doc_shipped.adoptNode(doc_previous.getDocumentElement().cloneNode(true)), doc_shipped.getDocumentElement().getElementsByTagName("previousPedigree").item(0));
    		doc.getDocumentElement().appendChild(doc.adoptNode(doc_shipped.getDocumentElement().cloneNode(true)));
    		
    		String xml = signPedigree(doc, sgtin);
    		InfoType_Pedigree shippedPedigree = new InfoType_Pedigree(sgtin , sgtin, "Shipped", getCurrentTime(), xml); 
        	boolean status = savePedigree(shippedPedigree);
        	boolean validity = checkPedigree(shippedPedigree.getXml());
    		return xml;
    	}
    	else {
    		return "failure";
    	}
    }
    */
    // shipped pedigree 를 EPCIS event 기반으로 자동 생성
    public String Generate_ShippedPedigree(String sgtin, String eventXml, InfoType_Company companyInfo, InfoType_Partner partnerInfo) {
    	// shipped pedigree 는 이전에 저장된 pedigree를 기반으로 생성, 따라서 저장된 pedigree 중에 검색해서 있으면 처리
    	InfoType_Pedigree previousPedigree = Find_Pedigree(sgtin);
    	if(previousPedigree != null) {
    		// shipped pedigree를 감쌀 pedigree 생성
    		pedigree pedigree = new pedigree();
    		Document doc = buildDocumentfromObject(pedigree);
    		
    		// sgtin을 gtin과 serial로 구분
    		String[] array = sgtin.replaceAll(".*:", "").split("\\.");
    		String gtin = array[0] + array[1];
    		InfoType_Product product = selectProductInfo(gtin);
    		
    		// shipped pedigree 생성을 위해 event와 product, companyInfo, partnerInfo를 전달
    		shippedPedigree shippedPedigreeData = new shippedPedigree(sgtin, product, buildDocumentfromString(eventXml), companyInfo, partnerInfo);
    		// 생성한 shipped pedigree 를 위한 document 생성
    		Document doc_shipped = buildDocumentfromObject(shippedPedigreeData);
    		// 이전 pedigree를 위한 document  생성
    		Document doc_previous = buildDocumentfromString(previousPedigree.getXml());
    		// document 정보에 유효기간은 이전의 pedigree 유효기간으로 대체->유효기간은 변경되지 않으므로
    		if(doc_shipped.getDocumentElement().getElementsByTagName("expirationDate").getLength()>0) {
    			doc_shipped.getDocumentElement().getElementsByTagName("expirationDate").item(0).setTextContent(doc_previous.getDocumentElement().getElementsByTagName("expirationDate").item(0).getTextContent());
    		}
    		//shipped pedigre 내 이전 pedigree 정보를 삽입
    		doc_shipped.getDocumentElement().replaceChild(doc_shipped.adoptNode(doc_previous.getDocumentElement().cloneNode(true)), doc_shipped.getDocumentElement().getElementsByTagName("previousPedigree").item(0));
    		doc.getDocumentElement().appendChild(doc.adoptNode(doc_shipped.getDocumentElement().cloneNode(true)));
    		
    		// 서명을 위해 certificate 및 개인키 정보를 획득
    		InfoType_Certificate certificateInfo = selectCertificateInfo();
    		PrivateKey privateKey = getPrivateKeyfromString(certificateInfo.getPrivateKey());
    		X509Certificate certificate = getX509CertificatefromString(certificateInfo.getPmsCertificate());
    		// pedigree에 대한 서명 진행
    		String xml = signPedigree(doc, privateKey, certificate, true);
    		// pedigree 정보 타입에 생성 및 서명한 pedigree xml 을 삽입
    		String recipientAddress = partnerInfo.getPmsAddress();
    		InfoType_Pedigree shippedPedigree = new InfoType_Pedigree(sgtin , sgtin, "Shipped", getCurrentTime(), recipientAddress, xml);
    		// 최종 생성된 pedigree 정보 타입을 저장
        	boolean status = savePedigree(shippedPedigree);
        	// 최종 생성된 pedigree xml에 대한 검증
        	boolean validity = checkPedigree(shippedPedigree.getXml());
    		return xml;
    	}
    	else {
    		return "failure";
    	}
    }
    // received pedigree에 대한 직접 생성 루틴은 삭제
    /*
    public String Generate_ReceivedPedigree(String sgtin) {
    	InfoType_Pedigree previousPedigree = Find_Pedigree(sgtin);
    	if(previousPedigree != null) {
    		if(previousPedigree.getType().contentEquals("Imported") ) {
    			if(checkPedigree(previousPedigree.getXml()) == true ) {
		    		pedigree pedigree = new pedigree();
		    		Document doc = buildDocumentfromObject(pedigree);
		    		receivedPedigree receivedPedigreeData = new receivedPedigree(sgtin);
		    		Document doc_received = buildDocumentfromObject(receivedPedigreeData);
		    		Document doc_previous = buildDocumentfromString(previousPedigree.getXml());
		    		doc_received.getDocumentElement().replaceChild(doc_received.adoptNode(doc_previous.getDocumentElement().cloneNode(true)), doc_received.getDocumentElement().getElementsByTagName("previousPedigree").item(0));
		    		doc.getDocumentElement().appendChild(doc.adoptNode(doc_received.getDocumentElement().cloneNode(true)));
		    		String xml = signPedigree(doc, sgtin);
		    		InfoType_Pedigree receivedPedigree = new InfoType_Pedigree(sgtin , sgtin, "Received", getCurrentTime(), xml); 
		        	boolean status = savePedigree(receivedPedigree);
		        	boolean validity = checkPedigree(receivedPedigree.getXml());
		    		return xml;
    			}
    			else {
    				return "failure";
    			}
    		}
    		else {
    			return "failure";
    		}
    	}
    	else {
    		return "failure";
    	}
    }
    */
    // received pedigree 를 EPCIS event 기반으로 자동 생성
    public String Generate_ReceivedPedigree(String sgtin, String eventXml, InfoType_Company companyInfo) {
    	// received pedigree는 이전에 수신한 pedigree 중 imported 상태인 pedigree만 대상으로 새롭게 생성
    	InfoType_Pedigree previousPedigree = Find_Pedigree(sgtin);
    	if(previousPedigree != null) {
    		if(previousPedigree.getType().contentEquals("Imported") ) {
    			// 이전 pedigree의 유효성이 검증되면
    			if(checkPedigree(previousPedigree.getXml()) == true ) {
    				// sgtin을 gtin과 serial로 구분
    	    		String[] array = sgtin.replaceAll(".*:", "").split("\\.");
    	    		String gtin = array[0] + array[1];
    	    		InfoType_Product product = selectProductInfo(gtin);
    	    		// received pedigree 저장을 위한 pedigree 생성
		    		pedigree pedigree = new pedigree();
		    		Document doc = buildDocumentfromObject(pedigree);
		    		// event, product 정보, companyInfo를 기반으로 received pedigree 생성
		    		receivedPedigree receivedPedigreeData = new receivedPedigree(sgtin, product, buildDocumentfromString(eventXml), companyInfo);
		    		// received pedigree에 대한 document 생성
		    		Document doc_received = buildDocumentfromObject(receivedPedigreeData);
		    		// 이전 pedigree에 대한 document 생성
		    		Document doc_previous = buildDocumentfromString(previousPedigree.getXml());
		    		// 유효기간은 변경되지 않으므로 이전 pedigree 의 제품 유효기간을 복사
		    		if(doc_received.getDocumentElement().getElementsByTagName("expirationDate").getLength()>0) {
		    			doc_received.getDocumentElement().getElementsByTagName("expirationDate").item(0).setTextContent(doc_previous.getDocumentElement().getElementsByTagName("expirationDate").item(0).getTextContent());
		    		}
		    		// received pedigree  내 이전 pedigree 정보를 삽입
		    		doc_received.getDocumentElement().replaceChild(doc_received.adoptNode(doc_previous.getDocumentElement().cloneNode(true)), doc_received.getDocumentElement().getElementsByTagName("previousPedigree").item(0));
		    		doc.getDocumentElement().appendChild(doc.adoptNode(doc_received.getDocumentElement().cloneNode(true)));
		    		// received pedigree 서명을 위해 인증서, 개인키 정보 획득
		    		InfoType_Certificate certificateInfo = selectCertificateInfo();
		    		PrivateKey privateKey = getPrivateKeyfromString(certificateInfo.getPrivateKey());
		    		X509Certificate certificate = getX509CertificatefromString(certificateInfo.getPmsCertificate());
		    		// received pedigree의 서명
		    		String xml = signPedigree(doc, privateKey, certificate, true);
		    		// 생성된 received pedigree를 received pedigree  타입에 저장
		    		String recipientAddress = companyInfo.getPmsAddress();
		    		InfoType_Pedigree receivedPedigree = new InfoType_Pedigree(sgtin , sgtin, "Received", getCurrentTime(), recipientAddress, xml);
		    		// 최종 received pedigree xml을 저장
		        	boolean status = savePedigree(receivedPedigree);
		        	// 최종 received pedigree xml을 검증
		        	boolean validity = checkPedigree(receivedPedigree.getXml());
		    		return xml;
		    		
    			}
    			else {
    				return "failure";
    			}
    		}
    		else {
    			return "failure";
    		}
    	}
    	else {
    		return "failure";
    	}
    }
    
    


}