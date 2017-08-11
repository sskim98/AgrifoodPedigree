package kaist.gs1.pms;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;

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
    public ArrayList<InfoType_TraceNode> Get_Pedigree_TraceInfo(String pedigree) {
    	ArrayList<InfoType_TraceNode> report = new ArrayList<InfoType_TraceNode>();
    	
    	String pedString = pedigree;
    	Document pedDoc = null;
    	// pedigree 내 모든 trace 정보를 추출, 단 initial pedigree 정보는 while문 아래에서 처리
    	while(pedString.length() > 0) {
    		// pedigree를 document 형태로 변환
    		pedDoc = buildDocumentfromString(pedString);
    		InfoType_TraceNode element = Analyze_Pedigree(getStringFromDocument(pedDoc));
    		if(element == null) {
    			break;
    		}
    		else {
    			// 발견된 trace 정보를 report에 저장
    			report.add(element);
    			// 내부 pedigree element를 추출
    			Node nextElements = pedDoc.getElementsByTagName("pedigree").item(1);
    			// 내부 pedigree element를 pedString에 저장함으로 recursive 한 동작 수행
    			pedString = getStringFromXmlNode(nextElements);
    		}
    	}
    	
    	// 마지막에 발견된  pedigree가 initial pedigree이면
    	Node initialPedigree = pedDoc.getElementsByTagName("initialPedigree").item(0); 
    	if( initialPedigree != null) {
    		// 생성자 정보 추출
    		String company = pedDoc.getElementsByTagName("manufacturer").item(0).getFirstChild().getTextContent();
        	String gtin = pedDoc.getElementsByTagName("serialNumber").item(0).getFirstChild().getTextContent();
        	String type = initialPedigree.getNodeName();
        	//String signatureDate = pedDoc.getElementsByTagName("signatureDate").item(0).getTextContent();
        	InfoType_TraceNode element = new InfoType_TraceNode(gtin, company, type, "", "-");
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
    public InfoType_TraceNode Analyze_Pedigree(String pedigree) {
    	// pedigree에 대한 document 생성
    	Document pedDoc = buildDocumentfromString(pedigree);
    	InfoType_TraceNode element = null;
    	
    	if(pedDoc.getElementsByTagName("signerInfo").getLength() != 0) {
	    	// 생산자 정보 획득
	    	String company = pedDoc.getElementsByTagName("signerInfo").item(pedDoc.getElementsByTagName("signerInfo").getLength()-1).getFirstChild().getTextContent();
	    	String gtin = pedDoc.getElementsByTagName("serialNumber").item(0).getFirstChild().getTextContent();
	    	String type = pedDoc.getFirstChild().getFirstChild().getNodeName();
	    	String signatureDate = pedDoc.getElementsByTagName("signatureDate").item(pedDoc.getElementsByTagName("signatureDate").getLength()-1).getTextContent();
	    	//String eventTime = pedDoc.getElementsByTagName("eventTime").item(0).getFirstChild().getTextContent();
	    	String validity = "TRUE";
	    	// 발견된 pedigree가 initial pedigree가 아니면 유효성 체크하고 회사 정보를 담은 Trace 타입 정보를 생성
	    	if(pedDoc.getFirstChild().getNodeName() != "initialPedigree") {
	    		validity = checkPedigree(pedigree).getDetail();
	    	}
	    	element = new InfoType_TraceNode(gtin, company, type, signatureDate, validity);
    	}
    	
    	return element;
    }
}