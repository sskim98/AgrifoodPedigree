package kaist.gs1.pms;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.data.mongodb.core.mapping.Document;

// 인증서 정보 타입
@Document(collection="CertificateInfo") 	 	
public class InfoType_Certificate {
	private String certificateType;
	private String validity;
	private String privateKeyString; // 개인키
	private String pmsCertificateString; // node 인증서
	private ArrayList<String> caCertificateStringArray; // ca 인증서
	private String privateRootCertificateString; // node 인증서
	private String privateKeyFileName; // 개인키
	private String pmsCertificateFileName; // node 인증서
	private String caCertificateFileName; // ca 인증서
	private String privateRootCertificateFileName; // ca 인증서
	
	// 초기화
	public InfoType_Certificate( String certificateType) {
		this.certificateType = certificateType;
		this.privateKeyString = "Null String";
		this.pmsCertificateString = "Null String";
		this.caCertificateStringArray = new ArrayList<String>();
		this.privateRootCertificateString = "Null String";
		this.privateKeyFileName = "Null String";
		this.pmsCertificateFileName = "Null String";
		this.caCertificateFileName = "Null String";
		this.privateRootCertificateFileName = "Null String";
	}

	// string으로부터 X509 타입 인증서를 얻는 루틴
	public X509Certificate getX509CertificatefromString(String crtString) {
		X509Certificate cert = null;
		if(crtString != null) {
			if(crtString.length() > 0) {
				try {
					cert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(new ByteArrayInputStream(crtString.getBytes("UTF-8")));
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return cert;
	}

	public String getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getPrivateKeyString() {
		return privateKeyString;
	}

	public void setPrivateKeyString(String privateKeyString) {
		this.privateKeyString = privateKeyString;
	}

	public String getPmsCertificateString() {
		return pmsCertificateString;
	}

	public void setPmsCertificateString(String pmsCertificateString) {
		this.pmsCertificateString = pmsCertificateString;
	}

	public ArrayList<String> getCaCertificateStringArray() {
		return caCertificateStringArray;
	}

	public void setCaCertificateStringArray(ArrayList<String> caCertificateStringArray) {
		this.caCertificateStringArray = caCertificateStringArray;
	}

	public String getPrivateRootCertificateString() {
		return privateRootCertificateString;
	}

	public void setPrivateRootCertificateString(String privateRootCertificateString) {
		this.privateRootCertificateString = privateRootCertificateString;
	}

	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	public String getPmsCertificateFileName() {
		return pmsCertificateFileName;
	}

	public void setPmsCertificateFileName(String pmsCertificateFileName) {
		this.pmsCertificateFileName = pmsCertificateFileName;
	}

	public String getCaCertificateFileName() {
		return caCertificateFileName;
	}

	public void setCaCertificateFileName(String caCertificateFileName) {
		this.caCertificateFileName = caCertificateFileName;
	}

	public String getPrivateRootCertificateFileName() {
		return privateRootCertificateFileName;
	}

	public void setPrivateRootCertificateFileName(String privateRootCertificateFileName) {
		this.privateRootCertificateFileName = privateRootCertificateFileName;
	}

	public X509Certificate getPmsCertificate() {
		return getX509CertificatefromString(pmsCertificateString);
	}
	
	public void addCaCertificateStringArray(String caCertificateString) {
		caCertificateStringArray.add(caCertificateString);
	}
	//CA 인증서 리스트 얻는 루틴
	public ArrayList<X509Certificate> getCaCertificateArray() {
		ArrayList<X509Certificate> caCertificateArray = new ArrayList<X509Certificate>();
		if(!caCertificateStringArray.isEmpty()) {
			Iterator<String> certIterator = caCertificateStringArray.iterator();
	    	for(int i=0; certIterator.hasNext(); i++){
		        String strCert = certIterator.next();
		        caCertificateArray.add(getX509CertificatefromString(strCert));
	    	}
		}
		return caCertificateArray;
	}
	
	
}
