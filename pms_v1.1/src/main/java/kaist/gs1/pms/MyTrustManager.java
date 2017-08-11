package kaist.gs1.pms;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * https에서 서버, 클라이언트의 인증서를 검증하기 위한 루틴, 테스트용으로 현재 사용하지 않음
 */
public class MyTrustManager implements X509TrustManager {

	private static final Logger logger = LoggerFactory.getLogger(MyTrustManager.class);
	
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub
		X509Certificate cert = chain[0];
		cert.checkValidity();
		logger.info("Welcome home! authType is {}.", authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// TODO Auto-generated method stub

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		return null;
	}

}
