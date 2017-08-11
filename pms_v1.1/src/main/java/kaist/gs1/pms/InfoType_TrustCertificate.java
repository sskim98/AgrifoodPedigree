package kaist.gs1.pms;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// 인증서 정보 타입
@Document(collection="TrustCertificateInfo") 	 	
public class InfoType_TrustCertificate {
	private String hasConfidence;
	private String caCertificateString; // ca 인증서
	@Id
	private String serialNumber;
	private String method; // 신뢰하는 인증서를 생성한 이유
						   //	ReadOnly: PMS 인증서 등록에서 private CA 인증서를 등록하면 이 인증서는 TrustCertificateInfo로서 저장되고 삭제가 불가능한 ReadOnly가 된다.,
						   //   Manual: TrustCertificateInfo 를 사용자가 직접 입력한 경우
						   //   Automatic: pedigree import 과정에서 private Certificate가 발견되면 설정되는 값 
	
	public InfoType_TrustCertificate( String hasConfidence, String caCertificateString, String serialNumber, String method) {
		this.hasConfidence = hasConfidence;
		this.caCertificateString = caCertificateString;
		this.serialNumber = serialNumber;
		this.method = method;
	}
	
	public String getHasConfidence() {
		return hasConfidence;
	}

	public void setHasConfidence(String hasConfidence) {
		this.hasConfidence = hasConfidence;
	}

	public String getCaCertificateString() {
		return caCertificateString;
	}

	public void setCaCertificateString(String caCertificateString) {
		this.caCertificateString = caCertificateString;
	}

	public String getSerialNumber() {
		return serialNumber;
	}


	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public X509Certificate getCaCertificate() {
		return getX509CertificatefromString(caCertificateString);
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

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	
}
