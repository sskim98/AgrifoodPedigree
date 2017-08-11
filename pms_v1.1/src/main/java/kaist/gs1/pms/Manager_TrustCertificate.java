package kaist.gs1.pms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 * 인증서 관리자
 */
@Component
public class Manager_TrustCertificate extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    private static InfoType_Error errorMsg = new InfoType_Error("", "");
    
    // trustCertificate 정보를 업로드
    public boolean uploadTrustCertificateInfo(InfoType_TrustCertificate trustCertificateInfo) {
    	trustCertificateInfo.setSerialNumber(trustCertificateInfo.getCaCertificate().getSerialNumber().toString(16));
		if(trustCertificateInfo.getHasConfidence().equals("Trust")) {
			try {
				//trustCertificateInfo.setHasConfidence("Trust");
				insertCertificateToPrivateTrustStore(trustCertificateInfo.getCaCertificate());
			} catch (CertificateException e) {
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
		
        saveTrustCertificate(trustCertificateInfo);
	    return true;
    }
    
    //trustCertificate를 신뢰할지 여부를 변경
    public boolean changeConfidenceOfTrustCertificate(String serialNumber) {
    	InfoType_TrustCertificate certificate = selectTrustCertificateBySerialNumber(serialNumber);
    	if(certificate != null) {
    		if(!certificate.getMethod().equals("ReadOnly")) {
	    		if(certificate.getHasConfidence().equals("Trust")) {
	    			certificate.setHasConfidence("Not_trust");
	    			try {
	    				deleteCertificateFromPrivateTrustStore(getX509CertificatefromString(certificate.getCaCertificateString()));
					} catch (CertificateException e) {
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
	    		else {
	    			certificate.setHasConfidence("Trust");
	    			try {
						insertCertificateToPrivateTrustStore(getX509CertificatefromString(certificate.getCaCertificateString()));
					} catch (CertificateException e) {
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
	    		saveTrustCertificate(certificate);
    		}
    	}
    	
		return true;
	}
    
    //trustCertificate를 삭제
    public Boolean deleteTrustCertificate(String serialNumber) {
    	InfoType_TrustCertificate certificateInfo = selectTrustCertificateBySerialNumber(serialNumber);
    	if(certificateInfo != null) {
    		if(!certificateInfo.getMethod().equals("ReadOnly")) {
		    	try {
					deleteCertificateFromPrivateTrustStore(getX509CertificatefromString(certificateInfo.getCaCertificateString()));
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	removeTrustCertificate(certificateInfo);
		    	return true;
    		}
    		return false;
    	}
    	else {
    		return false;
    	}
    }
    
    
}