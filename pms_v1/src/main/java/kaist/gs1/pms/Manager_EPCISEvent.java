package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.net.HttpURLConnection;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * EPCIS 이벤트 관리자
 */
@Component
public class Manager_EPCISEvent extends BaseManager_EPCISEvent {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    @Autowired
    private Manager_PedigreeGenerator pedigreeGenerator; // EPCIS event를 pedigree로 변환
    @Autowired
    private Manager_CompanyInfo companyManager; // shipping시 거래 당사자간 transaction 정보를 입력하기 위해 접근 필요
    @Autowired
    private Manager_PartnerInfo partnerManager; // shipping시 거래 당사자간 transaction 정보를 입력하기 위해 접근 필요
    
    // PMS에 저장된 EPCIS 이벤트를 검색하기 위한 루틴
    public Iterable<InfoType_EPCISEvent> getEPCISEvents() {
    	Iterable<InfoType_EPCISEvent> events = getAllEvent();
        return events;
    }
    
    // EPCIS에 접근하여 query를 보내 이벤트 획득하는 루틴
    public boolean fetchEPCISEvents(String urlString) {
    	String response = "";
		try {
			//authmgr.getHttps("https://itc.kaist.ac.kr/xe/");
			response = getHttp(urlString);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(response != "") {
			// 공백, newline 삭제
			response = response.replaceAll(">\\s+", ">");
			System.out.println(response);
			Document eventDocument = buildDocumentfromString(response);
			NodeList eventList = eventDocument.getElementsByTagName("ObjectEvent");
			if(eventList.getLength() > 0) {
				for(int i=0; i<eventList.getLength(); i++) {
					String type = "event";
					String lastEventRecordTime = "";
					String eventTime = "";
					String recordTime = "";
					String eventTimeZoneOffset = "";
					String epc = "";
					String bizStep = "";
					String action = "";
					String lot = "";
					String quantity = "";
					String source_possessing_party = "";
					String source_location = "";
					String destination_owning_party = "";
					String destination_location = "";
					
					// EPCIS event를 파싱하여 각 정보를 저장
					Element element = (Element)(eventList.item(i));
					if(element.getElementsByTagName("eventTime").getLength() > 0) {
						eventTime = element.getElementsByTagName("eventTime").item(0).getTextContent();
					}
					if(element.getElementsByTagName("recordTime").getLength() > 0) {
						recordTime = element.getElementsByTagName("recordTime").item(0).getTextContent();
					}
					if(element.getElementsByTagName("eventTimeZoneOffset").getLength() > 0) {
						eventTimeZoneOffset = element.getElementsByTagName("eventTimeZoneOffset").item(0).getTextContent();
					}
					if(element.getElementsByTagName("bizStep").getLength() > 0) {
						bizStep = element.getElementsByTagName("bizStep").item(0).getTextContent();
					}
					if(element.getElementsByTagName("action").getLength() > 0) {
						action = element.getElementsByTagName("action").item(0).getTextContent();
					}
					if(element.getElementsByTagName("epcClass").getLength() > 0) {
						lot = element.getElementsByTagName("epcClass").item(0).getTextContent();
						String[] array = lot.replaceAll(".*:", "").split("\\.");
						lot = array[array.length-1];
					}
					if(element.getElementsByTagName("quantity").getLength() > 0) {
						quantity = element.getElementsByTagName("quantity").item(0).getTextContent();
					}
					if(element.getElementsByTagName("source").getLength() > 0) {
						for(int j=0; j<element.getElementsByTagName("source").getLength(); j++) {
							// 송신자 회사 sgln, 발신주소(창고) sgln 저장
							if(element.getElementsByTagName("source").item(j).getAttributes().getNamedItem("type").getNodeValue().contentEquals("urn:epcglobal:cbv:sdt:possessing_party")) {
								source_possessing_party = element.getElementsByTagName("source").item(j).getTextContent();
							}
							if(element.getElementsByTagName("source").item(j).getAttributes().getNamedItem("type").getNodeValue().contentEquals("urn:epcglobal:cbv:sdt:location")) {
								source_location = element.getElementsByTagName("source").item(j).getTextContent();
							}
						}
					}
					if(element.getElementsByTagName("destination").getLength() > 0) {
						for(int j=0; j<element.getElementsByTagName("destination").getLength(); j++) {
							// 수신자 회사 sgln, 발신주소(창고) sgln 저장
							if(element.getElementsByTagName("destination").item(j).getAttributes().getNamedItem("type").getNodeValue().contentEquals("urn:epcglobal:cbv:sdt:owning_party")) {
								destination_owning_party = element.getElementsByTagName("destination").item(j).getTextContent();
							}
							if(element.getElementsByTagName("destination").item(j).getAttributes().getNamedItem("type").getNodeValue().contentEquals("urn:epcglobal:cbv:sdt:location")) {
								destination_location = element.getElementsByTagName("destination").item(j).getTextContent();
							}
						}
					}
					String eventXml = getStringFromElement(element);
					if(element.getElementsByTagName("epc").getLength() > 0) {
						// epc가 여러개인 경우 각각의 epc별로 이벤트를 저장
						for(int j=0; j<element.getElementsByTagName("epc").getLength(); j++) {
							epc = element.getElementsByTagName("epc").item(j).getTextContent();
							InfoType_EPCISEvent event = new InfoType_EPCISEvent(type, lastEventRecordTime, eventTime, recordTime, epc, bizStep, action, lot, quantity, source_possessing_party, source_location, destination_owning_party, destination_location, eventXml);
							storeEvent(event);
							updateLastEventRecordTime(recordTime, eventTimeZoneOffset);
						}
					}
				}
			}
		}
		
		return true;
    }
    
    // EPCIS 이벤트를 획득하기 위한 http 연결 루틴
    public String getHttp(String urlString) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		// Get HTTP URL connection
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		
		// Connect to host
		conn.connect();
		//conn.setInstanceFollowRedirects(true);

		if (conn.getResponseCode() == 200) {
			// Print response from host
			InputStream in = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer response = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
	    	System.out.println(response.toString());
			return response.toString();
		} else {
			conn.disconnect();
			return "";
		}
	}

    // EPCIS 이벤트를 처리하여 pedigree를 생성/업데이트 하기 위한 루틴
    public void handleEvents() {
    	// commissioning 이벤트의 경우 initial pedigree 생성을 수행
    	// 모든 commissioning 이벤트에 대해 우선 처리
    	// pedigree 생성이 완료되면 PMS에 저장된 이벤트 정보를 삭제
    	Iterable<InfoType_EPCISEvent> events = selectEventByBizStep("urn:epcglobal:cbv:bizstep:commissioning");;
    	Iterator<InfoType_EPCISEvent> event = events.iterator();
    	InfoType_EPCISEvent element = null;
    	while(event.hasNext()) { 
    		element = event.next();
    		pedigreeGenerator.Generate_InitialPedigree(element.getEpc(), element.getEventXml());
    		removeEvent(element);
    	}
    	
    	// shipping 이벤트의 경우 shipped pedigree 생성을 수행
    	// 모든 shipped 이벤트에 대해 처리
    	// pedigree 생성이 완료되면 PMS에 저장된 이벤트 정보를 삭제
    	events = selectEventByBizStep("urn:epcglobal:cbv:bizstep:shipping");
    	event = events.iterator();
    	while(event.hasNext()) {
    		element = event.next();
    		InfoType_Company companyInfo = companyManager.getCompanyInfo();
    		InfoType_Partner partnerInfo = partnerManager.selectPartnerInfo(element.getDestination_owning_party());
    		pedigreeGenerator.Generate_ShippedPedigree(element.getEpc(), element.getEventXml(), companyInfo, partnerInfo);
    		removeEvent(element);
    	}
    	
    	// receiving 이벤트의 경우 received pedigree 생성을 수행
    	// 모든 received 이벤트에 대해 처리
    	// pedigree 생성이 완료되면 PMS에 저장된 이벤트 정보를 삭제
    	events = selectEventByBizStep("urn:epcglobal:cbv:bizstep:receiving");
    	event = events.iterator();
    	while(event.hasNext()) {
    		element = event.next();
    		InfoType_Company companyInfo = companyManager.getCompanyInfo();
    		pedigreeGenerator.Generate_ReceivedPedigree(element.getEpc(), element.getEventXml(), companyInfo);
    		removeEvent(element);
    	}
    }
    
    // EPCIS 이벤트를 fetch 한 경우 마지막 쿼리 시간을 저장하는 루틴
    // 마지막 쿼리 시간 이후로 다음 쿼리를 진행 함으로써 중복된 이벤트가 전송되지 않도록 하는 루틴 
    public void updateLastEventRecordTime(String newRecordTime, String zone) {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    	formatter.setTimeZone(TimeZone.getTimeZone("GMT"+zone));
        Date date;
		try {
			// 마지막 쿼리 시간을 양식에 맞게 변환
			date = formatter.parse(newRecordTime.replaceAll("Z", zone));
			InfoType_EPCISEvent masterEvent = getMasterData();
	        date.setTime(date.getTime()+1);
	        formatter.format(date);
	        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	        form.setTimeZone(TimeZone.getTimeZone("GMT"+zone));
	        if(masterEvent == null) {
	        	// 마스터 이벤트가 PMS에 저장되지 않은 경우 생성하여 마지막 쿼리 시간 저장
	        	masterEvent = new InfoType_EPCISEvent("master", form.format(date), "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-");
	        }
	        else {
	        	// 기존 저장된 마스터 이벤트가 있는경우 업데이트 
	        	Date lastEventRecordTime = formatter.parse(masterEvent.getLastEventRecordTime());
	        	if( lastEventRecordTime.getTime() < date.getTime() ) {
	        		masterEvent.setLastEventRecordTime(form.format(date));
	        	}
	        }
	        storeEvent(masterEvent);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}