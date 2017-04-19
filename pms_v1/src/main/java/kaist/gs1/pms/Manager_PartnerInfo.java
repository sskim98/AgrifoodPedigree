package kaist.gs1.pms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * 파트너 정보 관리자
 */
@Component
public class Manager_PartnerInfo extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    
    // 파트너 정보 추가 루틴
    public boolean Insert_PartnerInfo(String name, String street1, String street2, String city, String state, String postalCode, String country, String addressId, String pmsAddress, String importAddress, String userName, String userTitle, String userTelephone, String userEmail, String userUrl, String privateCertificate) {
    	// 추가할 파트너 정보가 DB에 없으면 추가
    	InfoType_Partner partner = this.selectPartnerInfo(importAddress);
        if(partner == null) {
        	partner = new InfoType_Partner( name, street1, street2, city, state, postalCode, country, addressId, pmsAddress, importAddress, userName, userTitle, userTelephone, userEmail, userUrl, privateCertificate);
        	savePartnerInfo(partner);
        	//createMasterData(name, street1, street2, city, state, postalCode, country, addressId, importAddress);
        	return true;
        }
        else {
        	return false;
        }
    }
    
    // 파트너 정보 업데이트 루틴
    public boolean Update_PartnerInfo(String index, String name, String street1, String street2, String city, String state, String postalCode, String country, String addressId, String pmsAddress, String importAddress, String userName, String userTitle, String userTelephone, String userEmail, String userUrl, String privateCertificate) {
    	// 업데이트할 파트너 정보가 DB에 있으면 갱신
    	InfoType_Partner partner = this.selectPartnerInfo(index);
        if(partner != null) {
        	removePartnerInfo(partner);
        	partner = new InfoType_Partner( name, street1, street2, city, state, postalCode, country, addressId, pmsAddress, importAddress, userName, userTitle, userTelephone, userEmail, userUrl, privateCertificate);
        	savePartnerInfo(partner);
        	//createMasterData(name, street1, street2, city, state, postalCode, country, addressId, importAddress);
        	return true;
        }
        else {
        	return false;
        }
    }
    
    // 파트너 정보는 EPCIS에 master 데이터로 저장됨, master event를 생성하기 위한 루틴
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
    	//masterData = masterData.replaceAll("/\\s*/", "");
    	System.out.println("master: "+masterData);
    	try {
    		// 생성한 master 이벤트를 EPCIS로 전달하여 저장되도록 함
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
    /*
    public boolean Query_PartnerInfo() {
    	InfoType_Partner partner = this.selectPartnerInfo(address);
    	if(partner != null) {
    		removePartnerInfo(partner);
    	}
		return true;
    }*/

    // EPCIS에 저장된 파트너 정보를 획득
    public boolean fetchEPCISMasterEvents(String urlString) {
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
			response = response.replaceAll(">\\s+", ">");
			System.out.println(response);
			Document dataDocument = buildDocumentfromString(response);
			NodeList dataList = dataDocument.getElementsByTagName("Vocabulary");
			if(dataList.getLength() > 0) {
				for(int i=0; i<dataList.getLength(); i++) {
					String companyName = "";
					String street1 = "";
					String street2 = "";
					String city = "";
					String state = "";
					String zip = "";
					String country = "";
					String addressId = "";
					String pmsAddress = "";
					String importAddress = "";
					String userName = "";
					String userTitle = "";
					String userTelephone = "";
					String userEmail = "";
					String userUrl = "";
					
					Element element = (Element)(dataList.item(i));
					if(element.getElementsByTagName("CompanyName").getLength() > 0) {
						companyName = element.getElementsByTagName("CompanyName").item(0).getTextContent();
					}
					if(element.getElementsByTagName("Street1").getLength() > 0) {
						street1 = element.getElementsByTagName("Street1").item(0).getTextContent();
					}
					if(element.getElementsByTagName("Street2").getLength() > 0) {
						street2 = element.getElementsByTagName("Street2").item(0).getTextContent();
					}
					if(element.getElementsByTagName("City").getLength() > 0) {
						city = element.getElementsByTagName("City").item(0).getTextContent();
					}
					if(element.getElementsByTagName("State").getLength() > 0) {
						state = element.getElementsByTagName("State").item(0).getTextContent();
					}
					if(element.getElementsByTagName("Zip").getLength() > 0) {
						zip = element.getElementsByTagName("Zip").item(0).getTextContent();
					}
					if(element.getElementsByTagName("Country").getLength() > 0) {
						country = element.getElementsByTagName("Country").item(0).getTextContent();
					}
					if(element.getElementsByTagName("PMSAddress").getLength() > 0) {
						importAddress = element.getElementsByTagName("PMSAddress").item(0).getTextContent();
					}
					if(element.getElementsByTagName("ImportAddress").getLength() > 0) {
						importAddress = element.getElementsByTagName("ImportAddress").item(0).getTextContent();
					}
					if(element.getElementsByTagName("VocabularyElement").item(0).getAttributes().getNamedItem("id").getNodeValue() != null) {
						addressId = element.getElementsByTagName("VocabularyElement").item(0).getAttributes().getNamedItem("id").getNodeValue();
					}
					if(element.getElementsByTagName("ContactName").getLength() > 0) {
						userName = element.getElementsByTagName("ContactName").item(0).getTextContent();
					}
					if(element.getElementsByTagName("ContactTitle").getLength() > 0) {
						userTitle = element.getElementsByTagName("ContactTitle").item(0).getTextContent();
					}
					if(element.getElementsByTagName("ContactTelephone").getLength() > 0) {
						userTelephone = element.getElementsByTagName("ContactTelephone").item(0).getTextContent();
					}
					if(element.getElementsByTagName("ContactEmail").getLength() > 0) {
						userEmail = element.getElementsByTagName("ContactEmail").item(0).getTextContent();
					}
					if(element.getElementsByTagName("ContactUrl").getLength() > 0) {
						userUrl = element.getElementsByTagName("ContactUrl").item(0).getTextContent();
					}
					InfoType_Partner partner = new InfoType_Partner(companyName, street1, street2, city, state, zip, country, addressId, pmsAddress, importAddress, userName, userTitle, userTelephone, userEmail, userUrl, "");
					savePartnerInfo(partner);
				}
			}
		}
		
		return true;
    }
    
    // EPCIS에 파트너 정보를 얻기 위해 http get을 연결하는 루틴
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
    
    // EPCIS의 master 데이터에 파트너 정보를 저장하기 위하여 http post를 연결하는 루틴
    public void postHttp(String urlString, String data) throws IOException, NoSuchAlgorithmException, KeyManagementException  {
    	URL obj = new URL(urlString);
    	HttpURLConnection conn;
    	String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
    	conn = (HttpURLConnection) obj.openConnection();

    	// Acts like a browser
    	conn.setUseCaches(false);
    	conn.setRequestMethod("POST");
    	conn.setRequestProperty("User-Agent", USER_AGENT);
    	//conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    	//conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    	//conn.setRequestProperty("Accept-Charset", "utf-8");
    	conn.setRequestProperty("charset", "utf-8");
    	//conn.setRequestProperty("Connection", "keep-alive");
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