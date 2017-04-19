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

/*
 * 사용자 정보 관리자
 */
@Component
public class Manager_UserInfo extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    
    // 회원가입 루틴
    public boolean Signup_UserInfo(String userid, String password, String username, String department, String telephone, String email) {
    	// 기본적으로 회원가입을 하면 ROLE_USER로 권한을 주고 기존 사용자와 id가 중복되지 않으면 새로운 사용자 추가
    	ArrayList<String> authList = new ArrayList<String>();
    	authList.add("ROLE_USER");
    	InfoType_User user = this.selectUserInfo(userid);
        if(user == null) {
        	user = new InfoType_User( null, userid, password, username, department, telephone, email, authList);
        	saveUserInfo(user);
        	return true;
        }
        else {
        	return false;
        }
        
    }
    // 새로운 사용자 추가 루틴
    public boolean Insert_UserInfo(String userid, String password, String username, String department, String telephone, String email, String authorities) {
    	// admin이 추가하는 사용자로서 기본권한은 ROLE_USER
    	ArrayList<String> authList = new ArrayList<String>();
        authList.add("ROLE_USER");
        InfoType_User user = this.selectUserInfo(userid);
        if(user == null) {
        	user = new InfoType_User( null, userid, password, username, department, telephone, email, authList);
        	saveUserInfo(user);
        	return true;
        }
        else {
        	return false;
        }
    }
    // 사용자 정보 변경
    public boolean Update_UserInfo(String userid, String password, String username, String department, String telephone, String email, String authorities, String index) {
    	// string 형태로 전달된 권한 정보를 Array 로 변경
    	ArrayList<String> authList = new ArrayList<String>();
        authorities = authorities.trim();
        String[] roles = authorities.split(" ");
        for(int i=0; i<roles.length; i++) {
        	authList.add(roles[i]);
        }
        // 사용자 정보 및 권한을 저장하여 기존 정보 업데이트
        InfoType_User user = this.selectUserInfo(index);
        if(user != null) {
        	user.setUserID(userid);
        	user.setPassword(password);
        	user.setUsername(username);
        	user.setDepartment(department);
        	user.setTelephone(telephone);
        	user.setEmail(email);
        	user.setRoles(authList);
        	saveUserInfo(user);
        	return true;
        }
        else {
        	return false;
        }
    }
    // 기존 사용자 삭제 루틴
    public boolean Delete_UserInfo(String userid) {
    	// 기존 사용자를 검색하여 검색되면 삭제
    	InfoType_User user = selectUserInfo(userid);
    	if(user != null) {
    		removeUserInfo(user);
    		return true;
    	}
    	else {
    		return false;
    	}
    }

}