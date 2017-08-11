package kaist.gs1.pms;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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
    	HttpsURLConnection conn;
    	String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
    	conn = (HttpsURLConnection) obj.openConnection();

    	// Acts like a browser
    	conn.setUseCaches(false);
    	conn.setRequestMethod("POST");
    	conn.setRequestProperty("User-Agent", USER_AGENT);
    	conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    	conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    	conn.setRequestProperty("Accept-Charset", "utf-8");
    	conn.setRequestProperty("Connection", "keep-alive");
    	conn.setRequestProperty("Content-Type", "application/xml; charset='utf-8'");

    	conn.setDoOutput(true);
    	conn.setDoInput(true);
    	InfoType_Pedigree pedigree = Find_Pedigree(sgtin);
    	conn.setRequestProperty("Content-Length", Integer.toString(pedigree.getXml().getBytes().length));
    	
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
			FileInputStream fis = new FileInputStream(System.getProperty("java.home")  + "\\lib\\security\\cacerts");
			ks.load(fis, "changeit".toCharArray());
			kmf.init(ks, "changeit".toCharArray());
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
					KeyStore trustStore = KeyStore.getInstance("JKS");  
					String cacertPath = System.getProperty("java.home") + "\\lib\\security\\cacerts"; // Trust store path should be different by system platform.  
					trustStore.load(new FileInputStream(cacertPath), "changeit".toCharArray()); // Use default certification validation  
					// 서버 인증 수행
					TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
					tmf.init(trustStore);
					TrustManager[] tms = tmf.getTrustManagers();
					try {
						((X509TrustManager) tms[0]).checkServerTrusted(chain, authType);
					} catch ( CertificateException e) {
						// 서버 인증을 하기위한 trustStore 로드(사설인증서)
						KeyStore privateTrustStore = KeyStore.getInstance("JKS"); 
						String keystorePath = System.getProperty("catalina.home") + "\\keys\\privateKeystore.jks"; // Trust store path should be different by system platform.
						privateTrustStore.load(new FileInputStream(keystorePath), "changeit".toCharArray()); // Use default certification validation
						TrustManagerFactory privateTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
						privateTmf.init(privateTrustStore);
						TrustManager[] privateTms = privateTmf.getTrustManagers();
						((X509TrustManager) privateTms[0]).checkServerTrusted(chain, authType);
					}

				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
    
}