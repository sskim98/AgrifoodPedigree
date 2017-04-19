package kaist.gs1.pms;

import java.security.Principal;

import javax.inject.Inject;
import javax.security.cert.X509Certificate;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/*
 * 인증 관련 테스트 루틴, 현재 사용하지 않음
 */
public class MyAuthProvider extends AbstractUserDetailsAuthenticationProvider { 
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
		String str = userDetails.getUsername();
		String strr = str.trim();
		
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub
        X509Certificate certificate = (X509Certificate) authentication.getPrincipal();
        Principal prin = certificate.getIssuerDN();
        return null;
	}
}