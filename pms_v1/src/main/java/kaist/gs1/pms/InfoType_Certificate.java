package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// 인증서 정보 타입
@Document(collection="CertificateInfo") 	 	
public class InfoType_Certificate {
	private String privateKey; // 개인키
	private String pmsCertificate; // node 인증서
	private String caCertificate; // ca 인증서
	
	public InfoType_Certificate( String privateKey, String pmsCertificate, String caCertificate) {
		this.pmsCertificate = pmsCertificate;
		this.caCertificate = caCertificate;
		this.privateKey = privateKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPmsCertificate() {
		return pmsCertificate;
	}

	public void setPmsCertificate(String pmsCertificate) {
		this.pmsCertificate = pmsCertificate;
	}

	public String getCaCertificate() {
		return caCertificate;
	}

	public void setCaCertificate(String caCertificate) {
		this.caCertificate = caCertificate;
	}

	
}
