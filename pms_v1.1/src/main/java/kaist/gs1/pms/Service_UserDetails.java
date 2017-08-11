package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import kaist.gs1.pms.RepositoryDao_User;

//사용자 credential 정보를 비교하여 인증을 처리하는 모듈
//도메인, 즉 itc.kaist.ac.kr 과 같은 도메인으로 검색하는 api 정의
@Component
public class Service_UserDetails implements UserDetailsService {

    @Resource
    private RepositoryDao_User userRepositoryDao;
    private static final Logger logger = Logger.getLogger(Service_UserDetails.class);
    private User userdetails;
    
    //사용자  ID로 DB를 검색하여 검색된 사용자가 있으면 사용자의 정보를 리턴
    //리턴된 사용자 정보는 로그인모듈과 연결되어 자동으로 동작
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        InfoType_User peduser = getUserDetail(username); // 함수 호출로 사용자 credential 정보 획득
        
        // 얻어온 사용자 정보가 null이면, 즉 사용자가 DB에 등록된 사용자가 아니면 엑세스 거부
        if(peduser == null) {
        	throw new UsernameNotFoundException("������ ������ ã�� �� �����ϴ�.");
        }
        
        //사용자가 유효한 사용자이면 credential 리턴
        userdetails = new User(
        		peduser.getUserID(), 
        		peduser.getPassword(),
            	enabled,
            	accountNonExpired,
            	credentialsNonExpired,
            	accountNonLocked,
            	getAuthorities(peduser.getRoles()));
            return userdetails;
    }

    //DB에 사용자 권한은 공백으로 구분되는 문자열 배열로 이루어져 있다.
    //각 권한을List<GrantedAuthority 객체로 변환하여  리턴
    public List<GrantedAuthority> getAuthorities(ArrayList<String> roles) {
        List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
        for(int i=0; i<roles.size(); i++) {
	            authList.add(new SimpleGrantedAuthority(roles.get(i)));
        }
        return authList;
    }

    //Dao를 통해 userid를 검색하고 검색된 사용자 credential을 리턴
    public InfoType_User getUserDetail(String userid) {
    	InfoType_User peduser = userRepositoryDao.findUserByUserID(userid);
        return peduser;
    }
}