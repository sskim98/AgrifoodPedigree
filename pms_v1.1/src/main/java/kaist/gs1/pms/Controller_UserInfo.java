package kaist.gs1.pms;

import java.security.Principal;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kaist.gs1.pms.InfoType_User;

/**
 * PMS 사용자 정보 표시 및 추가, 수정
 */
@Controller
public class Controller_UserInfo {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_UserInfo userManager;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);
	String ErrorMsg = "";
	/**
	 * 현재 사용자 정보 표시
	 */
	@RequestMapping(value = "/user_info", method = RequestMethod.GET)
	public String user_info(HttpServletRequest request, ModelMap model, Principal principal) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		
			// 현재 사용자의 정보를 얻어서 view로 전달
			InfoType_User user = userManager.selectUserInfo(principal.getName());
			model.addAttribute("user", user);
			model.addAttribute("errorMsg", ErrorMsg );
			ErrorMsg = "";
			return "user_info";
		
	}
	
	// 현재 사용자의 정보를 수정할 때 이용
	@RequestMapping(value = "/change_user", method = RequestMethod.POST)
	public String change_info(HttpServletRequest request, ModelMap model, Principal principal) {
		//model.addAttribute("homeUrl", servletContext.getContextPath() );
		if(request.getParameter("from").contentEquals("userinfo_page")) {
			if(request.getParameter("userid").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("password").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("username").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("department").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("telephone").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("email").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else {
				// 수정된 사용자 정보가 필요한 모든 필드에 데이터를 담고 있을 때 해당 정보를 업데이트
				userManager.Update_UserInfo(
				    			request.getParameter("userid"),
				    			request.getParameter("password"),
				    			request.getParameter("username"),
				    			request.getParameter("department"),
				    			request.getParameter("telephone"),
				    			request.getParameter("email"),
				    			request.getParameter("authorities"),
				    			request.getParameter("index")
				    			);
				InfoType_User user = userManager.selectUserInfo(principal.getName());
				model.addAttribute("user", user);
			}
		}
		
		return "redirect:/user_info";
	}
	
	// PMS 의 사용자 리스트 표시, 나중에 관리자 권한이 있는 사람만 이용 가능하도록 수정해야 함
	@RequestMapping(value = "/userlist", method = RequestMethod.GET)
	public String userlist(HttpServletRequest request, ModelMap model, Principal principal) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		// 모든 사용자 정보를 얻어오고 view로 전달
		Iterable<InfoType_User> users= userManager.getUserList();
		model.addAttribute("users", users);
		model.addAttribute("errorMsg", ErrorMsg );
		ErrorMsg = "";
		return "userlist";
		
	}
	
	// 사용자 리스트 페이지에서 새로운 사용자를 추가하거나 변경, 삭제할 때 이용, 관리자 권한 필요하도록 수정 필요
	@RequestMapping(value = "/change_list", method = RequestMethod.POST)
	public String change_user(HttpServletRequest request, ModelMap model, Principal principal) {
		//model.addAttribute("homeUrl", servletContext.getContextPath());
		if(request.getParameter("from").contentEquals("userlist_page")){
			if(request.getParameter("userid").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("password").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("username").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("department").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("telephone").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("email").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else {
				String action = request.getParameter("action");
				if(action.equals("Add")) {
					// 사용자 추가인 경우에
					boolean result = userManager.Insert_UserInfo(
							request.getParameter("userid"),
			    			request.getParameter("password"),
			    			request.getParameter("username"),
			    			request.getParameter("department"),
			    			request.getParameter("telephone"),
			    			request.getParameter("email"),
			    			request.getParameter("authorities")
							);
					if(result == false) {
						ErrorMsg =  "Duplicated User ID";
					}
				}
				else if(action.equals("Change")) {
					// 사용자 변경인 경우에
					boolean result = userManager.Update_UserInfo(
							request.getParameter("userid"),
			    			request.getParameter("password"),
			    			request.getParameter("username"),
			    			request.getParameter("department"),
			    			request.getParameter("telephone"),
			    			request.getParameter("email"),
			    			request.getParameter("authorities"),
			    			request.getParameter("index")
							);
					if(result == false) {
						ErrorMsg = "Can't Find User ID";
					}
				}
				else if(action.equals("Delete")) {
					// 사용자 삭제인 경우에
					boolean result = userManager.Delete_UserInfo(request.getParameter("index"));
					if(result == false) {
						ErrorMsg = "Can't Find User ID";
					}
				}
			}
		}
		return "redirect:/userlist";
	}
}
