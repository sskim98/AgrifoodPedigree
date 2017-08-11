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

/**
 * class Controller_CompanyInfo
 * 회사 정보 확인, 및 수정
 * company_info로 접근하면 저장된 회사 정보를 표현
 * 정보 수정버튼을 누르면 새로 입력된 회사 정보가 저장
 */
@Controller
public class Controller_CompanyInfo {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_CompanyInfo companyManager;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);
	
	/**
	 * company_info로 접근하면 저장된 회사 정보를 꺼내서 웹페이지에 표시
	 */
	@RequestMapping(value = "/company_info", method = RequestMethod.GET)
	public String company_info(HttpServletRequest request, ModelMap model, Principal principal) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		InfoType_Company company = companyManager.getCompanyInfo();
		model.addAttribute("company", company);
		return "company_info";
	}
	/*
	 * 회사정보 수정 버튼을 누르면 POST로 전달되며 필드가 값이 있는지 비교하여 없으면 오류메시지 표시
	 * 필요한 필드의 값이 모두 있는 경우 새로운 정보로 업데이트
	 */
	@RequestMapping(value = "/change_company", method = RequestMethod.POST)
	public String change_company(HttpServletRequest request, ModelMap model, Principal principal) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		if(request.getParameter("from").contentEquals("menu_page")) {

		}
		else if(request.getParameter("from").contentEquals("companyinfo_page")) {
			if(request.getParameter("country").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("province").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("locality").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("organization").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("organizationUnit").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("domain").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("email").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("epcisAddress").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("street").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("postalCode").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else if(request.getParameter("addressId").trim().isEmpty()) {
				model.addAttribute("errorMsg", "Please fill in all the blanks on this form" );

			}
			else {//전달된 회사 정보를 DB에 저장
				companyManager.Insert_CompanyInfo(
				    			request.getParameter("country"),
				    			request.getParameter("province"),
				    			request.getParameter("locality"),
				    			request.getParameter("organization"),
				    			request.getParameter("organizationUnit"),
				    			request.getParameter("domain"),
				    			request.getParameter("email"),
				    			request.getParameter("epcisAddress"),
				    			request.getParameter("street"),
				    			request.getParameter("postalCode"),
				    			request.getParameter("addressId"), 
				    			request.getParameter("pmsAddress"), 
				    			request.getParameter("importAddress"), 
				    			request.getParameter("userName"),
				    			request.getParameter("userTitle"),
				    			request.getParameter("userTelephone"),
				    			request.getParameter("userEmail"),
				    			request.getParameter("userUrl")
				    			);
				//회사 정보는 회사의 EPCIS 주소를 얻어와서 Master Data로 저장되어야 한다.
				//createMasterData는 이를 위한 함수
				InfoType_Company company = companyManager.getCompanyInfo();
				String queryString = company.getEpcisAddress()+"/Service/VocabularyCapture";
				companyManager.createMasterData(
						queryString,
						request.getParameter("organization") + " " + request.getParameter("organizationUnit"),
						request.getParameter("street"),
						request.getParameter("locality"),
						request.getParameter("province"),
						request.getParameter("province"),
						request.getParameter("postalCode"),
						request.getParameter("country"),
						request.getParameter("addressId"),
						request.getParameter("pmsAddress"), 
						request.getParameter("importAddress"), 
		    			request.getParameter("userName"),
		    			request.getParameter("userTitle"),
		    			request.getParameter("userTelephone"),
		    			request.getParameter("userEmail"),
		    			request.getParameter("userUrl")
				);
				model.addAttribute("errorMsg", "Modification Complete!" );
			}
		}
		InfoType_Company company = companyManager.getCompanyInfo();
		model.addAttribute("company", company); 
		return "redirect:/company_info";
	}
	
}
