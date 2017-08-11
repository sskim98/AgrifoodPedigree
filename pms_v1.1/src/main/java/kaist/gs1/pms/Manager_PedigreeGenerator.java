package kaist.gs1.pms;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.security.PrivateKey;

/*
 * pedigree 생성 관리자
 */
@Component
public class Manager_PedigreeGenerator extends BaseManager_Info {
	@Autowired
    private Manager_Certificate certManager; // shipping시 거래 당사자간 transaction 정보를 입력하기 위해 접근 필요
	
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static InfoType_Error errorMsg = new InfoType_Error("", "");

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
    
    // shipped pedigree 를 EPCIS event 기반으로 자동 생성
    public InfoType_Error Generate_ShippedPedigree(String sgtin, String eventXml, InfoType_Company companyInfo, InfoType_Partner partnerInfo) {
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
    		InfoType_Certificate pmsCertificate = selectCertificateInfo();
    		// shipped pedigree 생성을 위해 event와 product, companyInfo, partnerInfo를 전달
    		shippedPedigree shippedPedigreeData = new shippedPedigree(sgtin, product, buildDocumentfromString(eventXml), companyInfo, partnerInfo, pmsCertificate);
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
    		PrivateKey privateKey = getPrivateKeyfromString(certificateInfo.getPrivateKeyString());
    		
    		// pedigree에 대한 서명 진행
    		String xml = signPedigree(doc, privateKey, certificateInfo.getPmsCertificate(), certificateInfo.getCaCertificateArray());
    		// pedigree 정보 타입에 생성 및 서명한 pedigree xml 을 삽입
    		String recipientAddress = partnerInfo.getPmsAddress();
    		InfoType_Pedigree shippedPedigree = new InfoType_Pedigree(sgtin , sgtin, "Shipped", getCurrentTime(), recipientAddress, xml);
    		
        	// 최종 생성된 pedigree xml에 대한 검증
        	errorMsg = checkAllNestedPedigree(shippedPedigree.getXml());
        	if(errorMsg.getCode().equals("0")) {
            	// 유효한 pedigree 인 경우에 
        		shippedPedigree.setValidationStatus("success");
            }
        	else if(errorMsg.getCode().equals("1")) {
            	// 유효한 pedigree 인 경우에 
        		shippedPedigree.setValidationStatus("success(expired certificate)");
            }
            else {
            	shippedPedigree.setValidationStatus("failure");
            }
        	shippedPedigree.setValidationDetail(errorMsg.getDetail());
        	// 최종 생성된 pedigree 정보 타입을 저장
        	boolean status = savePedigree(shippedPedigree);
        	
    		return errorMsg;
    	}
    	else {
    		errorMsg.setCode("-1");
    		errorMsg.setDetail("There is no matched pedigree in the DB");
    		return errorMsg;
    	}
    }
    
    // received pedigree 를 EPCIS event 기반으로 자동 생성
    public InfoType_Error Generate_ReceivedPedigree(String sgtin, String eventXml, InfoType_Company companyInfo) {
    	// received pedigree는 이전에 수신한 pedigree 중 imported 상태인 pedigree만 대상으로 새롭게 생성
    	InfoType_Pedigree previousPedigree = Find_Pedigree(sgtin);
    	if(previousPedigree != null) {
    		if(previousPedigree.getType().contentEquals("Imported") ) {
    			// 이전 pedigree의 유효성이 검증되면
    			if(previousPedigree.getValidationStatus().contains("success") ) {
    				// sgtin을 gtin과 serial로 구분
    	    		String[] array = sgtin.replaceAll(".*:", "").split("\\.");
    	    		String gtin = array[0] + array[1];
    	    		InfoType_Product product = selectProductInfo(gtin);
    	    		// received pedigree 저장을 위한 pedigree 생성
		    		pedigree pedigree = new pedigree();
		    		Document doc = buildDocumentfromObject(pedigree);
		    		InfoType_Certificate pmsCertificate = selectCertificateInfo();
		    		// event, product 정보, companyInfo를 기반으로 received pedigree 생성
		    		receivedPedigree receivedPedigreeData = new receivedPedigree(sgtin, product, buildDocumentfromString(eventXml), companyInfo, pmsCertificate);
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
		    		PrivateKey privateKey = getPrivateKeyfromString(certificateInfo.getPrivateKeyString());
		    		
		    		// received pedigree의 서명
		    		String xml = signPedigree(doc, privateKey, certificateInfo.getPmsCertificate(), certificateInfo.getCaCertificateArray());
		    		// 생성된 received pedigree를 received pedigree  타입에 저장
		    		String recipientAddress = companyInfo.getPmsAddress();
		    		InfoType_Pedigree receivedPedigree = new InfoType_Pedigree(sgtin , sgtin, "Received", getCurrentTime(), recipientAddress, xml);

		        	// 최종 received pedigree xml을 검증
		        	errorMsg = checkAllNestedPedigree(receivedPedigree.getXml());
		        	if(errorMsg.getCode().equals("0")) {
		            	// 유효한 pedigree 인 경우에 
		        		receivedPedigree.setValidationStatus("success");
		            }
		        	else if(errorMsg.getCode().equals("1")) {
		            	// 유효한 pedigree 인 경우에 
		            	receivedPedigree.setValidationStatus("success(certificate timestamp expired)");
		            }
		            else {
		            	receivedPedigree.setValidationStatus("failure");
		            }
		        	receivedPedigree.setValidationDetail(errorMsg.getDetail());
		        	
		         // 최종 received pedigree xml을 저장
		        	boolean status = savePedigree(receivedPedigree);
		    		return errorMsg;
		    		
    			}
    			else {
    				errorMsg.setCode("-1");
    	    		errorMsg.setDetail("There is no matched pedigree in the DB");
    				return errorMsg;
    			}
    		}
    		else {
    			errorMsg.setCode("-1");
	    		errorMsg.setDetail("There is no matched pedigree in the DB");
				return errorMsg;
    		}
    	}
    	else {
    		errorMsg.setCode("-1");
    		errorMsg.setDetail("There is no matched pedigree in the DB");
			return errorMsg;
    	}
    }
    
    


}