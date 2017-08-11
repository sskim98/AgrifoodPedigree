package kaist.gs1.pms;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

/*
 * pedigree  수신 관리자
 */
@Component
public class Manager_PedigreeImporter extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    @Autowired
	Manager_TrustCertificate trustCertManager;
    @Autowired
	Manager_Certificate certManager;
    
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
    		checkPrivateDocumentInfo(xml); //Private Document Info 필드가 있는지 확인
    		
    		/////////////////////////////////////////////////////////////////////
    		// sgtin이 발견되면 sgtin과 xml을 넘겨서 pedigree 타입 생성, 상태는 Imported로 기록
    		sgtin = attributeNode.getNodeValue();
    		InfoType_Pedigree importPedigree = new InfoType_Pedigree(sgtin , sgtin, "Imported", getCurrentTime(), "", xml); 
    		
    		InfoType_Error errorMsg = checkAllNestedPedigree(xml);
            if(errorMsg.getCode().equals("0")) {
            	// 유효한 pedigree 인 경우에 
            	importPedigree.setValidationStatus("success");
            }
            else if(errorMsg.getCode().equals("1")) {
            	// 유효한 pedigree 인 경우에 
            	importPedigree.setValidationStatus("success(certificate timestamp expired)");
            }
            else {
            	importPedigree.setValidationStatus("failure");
            }
            importPedigree.setValidationDetail(errorMsg.getDetail());
            
    		boolean status = savePedigree(importPedigree);
    		return status;
    	}
    	return false;
    }
    
    public boolean checkPrivateDocumentInfo(String ped) {
    	boolean hasPrivateDocumentInfo = false;
    	////////////////// Private Document Info 필드가 있는 경우 TrustCertificate 를 등록 /////////////////////////////////////////
    	Document doc = buildDocumentfromString(ped);
		NodeList nl = doc.getElementsByTagName("privateTrustCertificate");
		for(int i = 0; i < nl.getLength(); i++) { 
			Element element = (Element)(nl.item(i));
			String embeddedCertificateString = "-----BEGIN CERTIFICATE-----\n"+element.getTextContent()+"-----END CERTIFICATE-----\n";
			String serialNumber = trustCertManager.getX509CertificatefromString(embeddedCertificateString).getSerialNumber().toString(16);
			/// 등록된  ca certificate 또는 private root certificate와 일치하는지 확인
			InfoType_Certificate certificateInfo = certManager.getPMSCertificateAndPrivateKey();
			if(certificateInfo != null) {
				ArrayList<String> caCertificateArray = certificateInfo.getCaCertificateStringArray();
				for(int j = 0; j < caCertificateArray.size(); j++) {
					if(caCertificateArray.get(j).equals(embeddedCertificateString)) {
						//return true;
					}
				}
				String privateRootCertificate = certificateInfo.getPrivateRootCertificateString();
				if(privateRootCertificate.equals(embeddedCertificateString)) {
					//return true;
				}
			}
			
			InfoType_TrustCertificate trustCertificateInfo = selectTrustCertificateBySerialNumber(serialNumber);
			if(trustCertificateInfo == null) {
				trustCertificateInfo = new InfoType_TrustCertificate("Not_trust", embeddedCertificateString, serialNumber, "Automatic");
				trustCertManager.uploadTrustCertificateInfo(trustCertificateInfo);
			}
			hasPrivateDocumentInfo = true;
		}
		//////////////////Private Document Info 필드가 있는 경우 TrustCertificate 를 등록 /////////////////////////////////////////
		
		return hasPrivateDocumentInfo;
    }
    
    
}