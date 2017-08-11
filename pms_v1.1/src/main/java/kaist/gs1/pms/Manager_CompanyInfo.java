package kaist.gs1.pms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 * 회사 정보 관리자
 */
@Component
public class Manager_CompanyInfo extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    
    // 회사정보 추가 루틴
    public boolean Insert_CompanyInfo(String country, String province, String locality, String name, String department, String domain, String email, String epcisAddress, String street, String postalCode, String addressId, String pmsAddress, String importAddress, String userName, String userTitle, String userTelephone, String userEmail, String userUrl) {
    	// 기존 회사 정보 삭제
    	removeAllCompanyInfo();
    	// 사용자의 입력한 정보로 새로운 회사정보 생성
    	InfoType_Company company = new InfoType_Company( country, province, locality, name, department, domain, email, epcisAddress, street, postalCode, addressId, pmsAddress, importAddress, userName, userTitle, userTelephone, userEmail, userUrl);
    	// 회사 정보 저장
    	saveCompanyInfo(company);
        return true;
    }
    
    // 회사 정보를 EPCIS master 정보로 저장하기 위한 루틴
    public void createMasterData(String urlString, String name, String street1, String street2, String city, String state, String postalCode, String country, String addressId, String pmsAddress, String importAddress, String userName, String userTitle, String userTelephone, String userEmail, String userUrl) {
    	String masterData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
							+"<!DOCTYPE project>"
							+"<epcismd:EPCISMasterDataDocument "
							+"xmlns:epcismd=\"urn:epcglobal:epcis-masterdata:xsd:1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
								+"schemaVersion=\"1.0\" creationDate=\"2005-07-11T11:30:47.0Z\">"
								+"<EPCISBody>"
									+"<VocabularyList>"
										+"<Vocabulary type=\"urn:epcglobal:epcis:vtype:BusinessLocation\">"
											+"<VocabularyElementList>"
												+"<VocabularyElement id=\""+addressId+"\">"
												+"<attribute id=\"http://epcis.example.com/mda/address\">"
													+"<example:Address xmlns:example=\"http://epcis.example.com/ns\">"
														+"<CompanyName>"+name+"</CompanyName>"
															+"<Street1>"+street1+"</Street1>"
															+"<Street2>"+street2+"</Street2>"
															+"<City>"+city+"</City>"
															+"<State>"+state+"</State>"
															+"<Zip>"+postalCode+"</Zip>"
															+"<Country>"+country+"</Country>"
															+"<PMSAddress>"+pmsAddress+"</PMSAddress>"
															+"<ImportAddress>"+importAddress+"</ImportAddress>"
															+"<ContactName>"+userName+"</ContactName>"
															+"<ContactTitle>"+userTitle+"</ContactTitle>"
															+"<ContactTelephone>"+userTelephone+"</ContactTelephone>"
															+"<ContactEmail>"+userEmail+"</ContactEmail>"
															+"<ContactUrl>"+userUrl+"</ContactUrl>"
														+"</example:Address>"
													+"</attribute>"
												+"</VocabularyElement>"
											+"</VocabularyElementList>"
										+"</Vocabulary>"
									+"</VocabularyList>"
								+"</EPCISBody>"
							+"</epcismd:EPCISMasterDataDocument>";
    	System.out.println("master: "+masterData);
    	try {
    		// master 정보를 EPCIS로 전송하여 저장
			postHttp(urlString, masterData);
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
    }

    // EPCIS master 정보를 저장하기 위한 http post 전송 루틴
    public void postHttp(String urlString, String data) throws IOException, NoSuchAlgorithmException, KeyManagementException  {
    	URL obj = new URL(urlString);
    	HttpURLConnection conn;
    	String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
    	conn = (HttpURLConnection) obj.openConnection();

    	// Acts like a browser
    	conn.setUseCaches(false);
    	conn.setRequestMethod("POST");
    	conn.setRequestProperty("User-Agent", USER_AGENT);
    	conn.setRequestProperty("charset", "utf-8");
    	conn.setRequestProperty("Content-Type", "application/xml; charset='utf-8'");
    	conn.setDoOutput(true);
    	conn.setDoInput(true);
    	conn.setRequestProperty("Content-Length", Integer.toString(data.getBytes().length));
    	conn.setInstanceFollowRedirects( false );
		
    	try {
		
	    	// Send post request
    		/** POSTing **/
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            // master 정보를 body에 전송
            writer.write(data); // should be fine if my getQuery is encoded right yes?
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

	
	    	int responseCode = conn.getResponseCode();
	    	System.out.println("\nSending 'POST' request to URL : " + urlString);
	    	System.out.println("Response Code : " + responseCode);
	
	    	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    	String inputLine;
	    	StringBuffer response = new StringBuffer();
	
	    	while ((inputLine = in.readLine()) != null) {
	    		response.append(inputLine);
	    	}
	    	in.close();
	    	System.out.println(response.toString());
		}
		catch (IOException ex) {
			System.out.println("Connection Failure!");
		}
	}
}