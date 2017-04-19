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
 * pedigree 전달 관리자
 */
@Component
public class Manager_PedigreeExporter extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    
    // pedigree 검색 루틴
    public InfoType_Pedigree Find_Pedigree(String sgtin) {
    	InfoType_Pedigree pedigree = selectPedigree(sgtin);
        return pedigree;
    }
    
    // pedigree 전송 루틴
    public void Export_Pedigree(String urlString, String sgtin) {
		try {
			//authmgr.getHttps("https://itc.kaist.ac.kr/xe/");
			// 해당 URL로 https post를 통해 pedigree 전달
			postHttps(urlString, sgtin);
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
    
    // 파트너 PMS로 https 를 통해 pedigree를 전달
    public void postHttps(String urlString, String sgtin) throws IOException, NoSuchAlgorithmException, KeyManagementException {
    	URL obj = new URL(urlString);
//    	HttpsURLConnection readConn = null;
//    	 String credentials = "sskim" + ":" + "test";
//    	 String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//
//        readConn = (HttpsURLConnection) obj.openConnection();
//        readConn.setRequestMethod("GET");
//        readConn.setRequestProperty("Authorization", );
//        readConn.setRequestProperty("X-CSRF-Token", "Fetch");
//        readConn.connect();
//
//        session = getSessionCookies(readConn); // get the value of response header "set-cookie"
//        String xsrfToken = extractXsrfToken(readConn);

        
    	HttpsURLConnection conn;
    	String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
    	conn = (HttpsURLConnection) obj.openConnection();

    	// Acts like a browser
    	conn.setUseCaches(false);
    	conn.setRequestMethod("POST");
    	//conn.setRequestProperty("Host", "accounts.google.com");
    	conn.setRequestProperty("User-Agent", USER_AGENT);
    	conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    	conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    	conn.setRequestProperty("Accept-Charset", "utf-8");
    	/*
    	for (String cookie : this.cookies) {
    		conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
    	}*/
    	conn.setRequestProperty("Connection", "keep-alive");
    	//conn.setRequestProperty("Referer", "https://accounts.google.com/ServiceLoginAuth");
    	//conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    	conn.setRequestProperty("Content-Type", "application/xml; charset='utf-8'");
    	//??? conn.setRequestProperty("Content-Length", Integer.toString(sgtin.length()));
    	

    	conn.setDoOutput(true);
    	conn.setDoInput(true);
    	InfoType_Pedigree pedigree = Find_Pedigree(sgtin);
    	//??? conn.addRequestProperty("pedigree", previousPedigree.getXml());
    	conn.setRequestProperty("Content-Length", Integer.toString(pedigree.getXml().getBytes().length));
    	
    	// 사설 인증을 위한 keystore 로드 루틴, 현재 사용하지 않음
    	KeyStore ksCACert;
    	try {
    		ksCACert = KeyStore.getInstance(KeyStore.getDefaultType());
    		String keystorePath = System.getProperty("catalina.home") + "\\keys\\cpms_keystore.p12";
    		ksCACert.load(new FileInputStream(keystorePath), "00cola".toCharArray()); 
    		//Initialise a TrustManagerFactory with the CA keyStore 
    		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509"); 
    		tmf.init(ksCACert); //Create new SSLContext using our new TrustManagerFactory 

    	} catch (KeyStoreException e1) {
    		// TODO Auto-generated catch block
    		e1.printStackTrace();
    	} catch (CertificateException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 

    	// 호스트 이름 검증 루틴, 현재 호스트 이름은 검증하지 않음
    	conn.setHostnameVerifier(new HostnameVerifier() {
    		@Override
    		public boolean verify(String hostname, SSLSession session) {
    			// Ignore host name verification. It always returns true.
    			System.out.println("hostname: " + hostname);
    			return true;
    		}
    	});

    	// 사설 인증을 위한 keystore 로드 루틴, 현재 사용하지 않음
    	KeyStore ks;
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		try {
			ks = KeyStore.getInstance("JKS");
			FileInputStream fis = new FileInputStream(System.getProperty("catalina.home") + "\\keys\\cpms_keystore.p12");
			ks.load(fis, "00cola".toCharArray());
			kmf.init(ks, "00cola".toCharArray());
			SSLContext sc = SSLContext.getInstance("TLS");
			//sc.init(kmf.getKeyManagers(), null, null);
		} catch (KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// SSL setting 및 서버 인증서 검증 절차
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(kmf.getKeyManagers(), new TrustManager[] { new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// client certification check
				return;
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

				// Server certification check

				try {

					// 서버 인증을 하기위한 trustStore 로드(PKI인증서)  
					//KeyStore trustStore = KeyStore.getInstance("JKS");  
					//String cacertPath = System.getProperty("java.home") + "\\lib\\security\\cacerts"; // Trust store path should be different by system platform.  
					//trustStore.load(new FileInputStream(cacertPath), "tomcat".toCharArray()); // Use default certification validation  

					// 서버 인증을 하기위한 trustStore 로드(사설인증서)
					KeyStore trustStore = KeyStore.getInstance("JKS");
					String keystorePath = System.getProperty("catalina.home") + "\\keys\\cpms_keystore.p12"; // Trust store path should be different by system platform.
					trustStore.load(new FileInputStream(keystorePath), "00cola".toCharArray()); // Use default certification validation

					// 서버 인증 수행
					TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
					tmf.init(trustStore);
					TrustManager[] tms = tmf.getTrustManagers();
					((X509TrustManager) tms[0]).checkServerTrusted(chain, authType);

					/*
				TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init((KeyStore)null);
				System.out.println("JVM Default Trust Managers:");
				for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
				    System.out.println(trustManager);

				    if (trustManager instanceof X509TrustManager) {
				        X509TrustManager x509TrustManager = (X509TrustManager)trustManager;
				        System.out.println("\tAccepted issuers count : " + x509TrustManager.getAcceptedIssuers().length);
				        ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
				    }
				}
					 */

				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} 

				catch (IOException e) {
					e.printStackTrace();
				}

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} }, null);


		try {
			// SSL 연결
			conn.setSSLSocketFactory(context.getSocketFactory());
			conn.connect();
			conn.setInstanceFollowRedirects(true);
	
	    	// pedigree 전송
			OutputStreamWriter writer = new OutputStreamWriter( conn.getOutputStream() );
		    writer.write( pedigree.getXml() );
		    System.out.println(pedigree.getXml());
		    
		    // 전송한 pedigree 검증
		    boolean validity_result = checkPedigree(pedigree.getXml());
	        if(validity_result)
	        	System.out.println("Validity TRUE");
		    //
		    
	    	//DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    	//BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(wr, "UTF-8"));
	    	//???writer.write("pedigree="+previousPedigree.getXml());
	    	//writer.write(previousPedigree.getXml());
	    	writer.flush();
	    	writer.close();
	
	    	int responseCode = conn.getResponseCode();
	    	System.out.println("\nSending 'POST' request to URL : " + urlString);
	    	System.out.println("Post parameters : " + sgtin);
	    	System.out.println("Response Code : " + responseCode);
	
	    	BufferedReader in =
	                 new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
    
    // https get 을 테스트 하기 위한 루틴, 나중에 파트너 정보를 자동으로 얻어오도록 구현해야 할 수 있음, 현재는 호출하지 않음
    public void getHttps(String urlString, String sgtin) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		// Get HTTPS URL connection
		URL url = new URL(urlString);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		InfoType_Pedigree previousPedigree = Find_Pedigree(sgtin);
		if(previousPedigree != null) {
			conn.addRequestProperty("pedigree", previousPedigree.getXml());
			///////////////////
			KeyStore ksCACert;
			try {
				ksCACert = KeyStore.getInstance(KeyStore.getDefaultType());
				String keystorePath = System.getProperty("catalina.home") + "\\keys\\cpms_keystore.p12";
				ksCACert.load(new FileInputStream(keystorePath), "00cola".toCharArray()); 
				//Initialise a TrustManagerFactory with the CA keyStore 
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509"); 
				tmf.init(ksCACert); //Create new SSLContext using our new TrustManagerFactory 

			} catch (KeyStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 


			//////////////////////

			// Set Hostname verification
			conn.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					// Ignore host name verification. It always returns true.
					System.out.println("hostname: " + hostname);
					return true;
				}
			});

			KeyStore ks;
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			try {
				ks = KeyStore.getInstance("JKS");
				FileInputStream fis = new FileInputStream(System.getProperty("catalina.home") + "\\keys\\cpms_keystore.p12");
				ks.load(fis, "00cola".toCharArray());
				kmf.init(ks, "00cola".toCharArray());
				SSLContext sc = SSLContext.getInstance("TLS");
				//sc.init(kmf.getKeyManagers(), null, null);
			} catch (KeyStoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			// SSL setting
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(kmf.getKeyManagers(), new TrustManager[] { new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// client certification check
					return;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

					// Server certification check

					try {

						// Get trust store  
						//KeyStore trustStore = KeyStore.getInstance("JKS");  
						//String cacertPath = System.getProperty("java.home") + "\\lib\\security\\cacerts"; // Trust store path should be different by system platform.  
						//trustStore.load(new FileInputStream(cacertPath), "tomcat".toCharArray()); // Use default certification validation  

						// Get trust store
						KeyStore trustStore = KeyStore.getInstance("JKS");
						String keystorePath = System.getProperty("catalina.home") + "\\keys\\cpms_keystore.p12"; // Trust store path should be different by system platform.
						trustStore.load(new FileInputStream(keystorePath), "00cola".toCharArray()); // Use default certification validation

						// Get Trust Manager
						TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
						tmf.init(trustStore);
						TrustManager[] tms = tmf.getTrustManagers();
						((X509TrustManager) tms[0]).checkServerTrusted(chain, authType);

						/*
					TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
					trustManagerFactory.init((KeyStore)null);
					System.out.println("JVM Default Trust Managers:");
					for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
					    System.out.println(trustManager);

					    if (trustManager instanceof X509TrustManager) {
					        X509TrustManager x509TrustManager = (X509TrustManager)trustManager;
					        System.out.println("\tAccepted issuers count : " + x509TrustManager.getAcceptedIssuers().length);
					        ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
					    }
					}
						 */

					} catch (KeyStoreException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} 

					catch (IOException e) {
						e.printStackTrace();
					}

				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} }, null);


			conn.setSSLSocketFactory(context.getSocketFactory());

			//conn.getLocalPrincipal();
			//Principal prin = conn.getLocalPrincipal();
			//System.out.println(prin.getName());

			// Connect to host
			conn.connect();
			conn.setInstanceFollowRedirects(true);

			if(conn.getResponseCode() == 200) {
				// Print response from host
				InputStream in = conn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = reader.readLine()) != null) {
					System.out.printf("%s\n", line);
				}
				reader.close();
			}
			else {
				conn.disconnect();
			}
			
		}
		/*
		//////////////////////////////////////////////////////////////////////
		String certificateString = "-----BEGIN CERTIFICATE-----\n" +
		        "MIIDdzCCAl+gAwIBAgIJAMapd+KIAR0MMA0GCSqGSIb3DBQUAMFIxCzAJBgNV\n" +
		        "BAYTAktSMRQwEgYDVQQIDAtHeWVvbmdnaS1kbzEUMB1UEBwwLU2VvbmduYW0t\n" +
		        "c2kxFzAVBgNVBAoMDlRoZWV5ZSBDb21wYW55MB4E2MDIyMDA5MjEyMVoXDTE3\n" +
		        "MDIxOTA5MjEyMVowUjELMAkGA1UEBhMCS1IxSBgNVBAgMC0d5ZW9uZ2dpLWRv\n" +
		        "MRQwEgYDVQQHDAtTZW9uZ25hbS1zaTEXMA1UECgwOVGhlZXllIENvbXBhbnkw\n" +
		        "ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwgEKAoIBAQDbrzdE+u+cIMnGJRZWlOZ+\n" +
		        "8fBil6pVPQntbu3nz92omfiTAeGjb25gkwkLamsM0QISnXSmI+gl3iJmuCyFw\n" +
		        "r3F+GiAUXS7e87vJoOKclbGVIQHPtEsbx+IjK7C2MJgoToNczedFzhr1xI9NV\n" +
		        "ZpFMKV+YONM7qCsKKMjVrj4TE7DCZUaWu8QlPYC6p8kR5ZM6DNtxoE2QOWpiB\n" +
		        "RwRwM6k88KfUVXysuvj3LCRVOqN3GrpCGF8GjuMohrI0zcK2XHvZ6D8GaFUEA\n" +
		        "TLG9QcfD1N/Jo1tHpdWe3kQIYjtGBvjZ3VfUmqt9PFsqjK5sx6Kppd9yPreQp\n" +
		        "AgMBAAGjUDBOGA1UdDgQWBBTjlXdy+BlcQGHfVdF9/8QkW1ciTTAfBgNVHSME\n" +
		        "GDAWgBTjl+BlcQGHfVdF9/8QkW1ciTTAMBgNVHRMEBTADAQH/MA0GCSqGSIb3\n" +
		        "DQEBBQ4IBAQCpWmUEr5p4CdWSGWHSopcNGPgJIODJ4K6Ir/IFJRb5tyzYY02R\n" +
		        "mfUtyP39M8GnnEz5QqYNobCNIVV3cSgAMKeUTAbBQDbE+F04LzR6DRXtdhjp7\n" +
		        "VtRG6McnecEi4D2Zq2ZGdFuAncfzhvdjybgkhkn1TZnbbtsiYqazKsNhdBvzR\n" +
		        "mvEpRkC7eqpAw36A9zHyjvP9tJW0mVJjlUtevARDkLyX+VpMtKOUWHYikLXgK\n" +
		        "UfRm9rtQMIpCC9n0qTMvDkxuBJakdSI7YRCpPYrsv0IEP23UN3Y32j1lthVIU\n" +
		        "9S5KyRYKhDzJXadgO3dNI9bFL0H2SZ6mXqYL\n" +
		        "-----END CERTIFICATE-----";

		ByteArrayInputStream derInputStream = new ByteArrayInputStream(certificateString.getBytes());
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(derInputStream);
		String alias = cert.getSubjectX500Principal().getName();

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null, null);
		trustStore.setCertificateEntry(alias, cert);

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_0);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", new MySSLSocketFactory(trustStore), 443));
		*/

	}


}