package kaist.gs1.pms;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kaist.gs1.pms.MyShellCommand;
import kaist.gs1.pms.InfoType_User;
import kaist.gs1.pms.RepositoryDao_User;

/*
 * class Controller_Admission
 * 사용자 로그인, 회원가입 처리
 * 
 */
@Controller
public class Controller_Admission{
	@Autowired
	ServletContext servletContext; // homeURL 획득용
	@Autowired
	Manager_UserInfo userManager; //사용자 인증 처리 모듈
	private static final Logger logger = LoggerFactory.getLogger(Controller_Admission.class);
	
	//로그인 페이지
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		return "login";
	}
	//로그아웃 페이지
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		return "logout";
	}
	//signup 페이지 로드
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(HttpServletRequest request, ModelMap model) {
				
		//logger.info("{}", servletContext.getContextPath() );
		//로그인 페이지에서 이동했을때는 즉시 signup페이지 로드
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		if(request.getParameter("from").contentEquals("login_page")) {
			return "signup";
		}//signup 페이지에서 정보 입력후 submit 버튼을 눌렀을 때는 각 정보를 model에 저장
		else if(request.getParameter("from").contentEquals("signup_page")){
			model.addAttribute("userid", request.getParameter("userid"));
			model.addAttribute("password", request.getParameter("password"));
			model.addAttribute("username", request.getParameter("username"));
			model.addAttribute("department", request.getParameter("department"));
			model.addAttribute("telephone", request.getParameter("telephone"));
			model.addAttribute("email", request.getParameter("email"));
			//각 정보에 대해 빈 필드가 있으면 오류메시지 표시
			if(request.getParameter("userid").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );
				
				return "signup";
			}
			else if(request.getParameter("password").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );
				return "signup";
			}
			else if(request.getParameter("username").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );
				return "signup";
			}
			else if(request.getParameter("department").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );
				return "signup";
			}
			else if(request.getParameter("telephone").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );
				return "signup";
			}
			else if(request.getParameter("email").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );
				return "signup";
			}
			else {//모든 정보가 입력된 경우에는 새로운 사용자를 생성하고 성공 메시지를 보낸다. 권한이 없는 이유는 권한은 관리자가 주어야 하기 때문이다.
				userManager.Signup_UserInfo(
				    			request.getParameter("userid"),
				    			request.getParameter("password"),
				    			request.getParameter("username"),
				    			request.getParameter("department"),
				    			request.getParameter("telephone"),
				    			request.getParameter("email")
				    			);
				return "login";
			}
		}
		return "signup";
	}
	//접근제어 페이지 로드
	@RequestMapping(value = "/accessdenied")
	public String accessdenied(ModelMap model) {
		model.addAttribute("error", "true");
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		return "denied";
	}
}
