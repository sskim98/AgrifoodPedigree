package kaist.gs1.pms;

import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

/*
 * 인증서의 정보를 추출하기 위한 루틴, 현재 사용하지 않음
 */
public class MyX509PrincipalExtractor implements X509PrincipalExtractor {

	private static Pattern serialNumber = Pattern.compile("serialNumber=([^=,]*)", Pattern.CASE_INSENSITIVE);
	
	@Override
	public Object extractPrincipal(X509Certificate cert) {
		// TODO Auto-generated method stub
		String name = cert.getSubjectDN().getName();
		Matcher m = serialNumber.matcher(name);

		if (!m.find())
			throw new BadCredentialsException("Haven't found serialNumber in certificate.");

		return m.group(1);
		//return null;
	}

}
