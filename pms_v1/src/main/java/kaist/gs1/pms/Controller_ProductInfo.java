package kaist.gs1.pms;

import java.net.InetAddress;
import java.security.Principal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import kaist.gs1.pms.MyShellCommand;
import kaist.gs1.pms.InfoType_User;
import kaist.gs1.pms.RepositoryDao_User;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Controller_ProductInfo {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_ProductInfo productManager;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);
	String ErrorMsg = "";
	/**
	 * 제품 정보를 보이고 수정하기 위한 controller. GS1 Source와의 연동으로 진행해야 하나 현재는 PMS에 저장 후 사용
	 */
	@RequestMapping(value = "/change_product", method = RequestMethod.POST)
	public String change_product(HttpServletRequest request, ModelMap model, Principal principal) {
		
		model.addAttribute("homeUrl", servletContext.getContextPath());
		//if(request.getParameter("from").contentEquals("productlist_page")){
			if(request.getParameter("name").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("manufacturer").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("productCode").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			/*
			else if(request.getParameter("productCodeType").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("dosageForm").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("strength").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("containerSize").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("lot").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			*/
			else if(request.getParameter("expirationDate").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else {
				//제품정보 등록 시 동작
				String action = request.getParameter("action");
				if(action.equals("Add")) {
					boolean result = productManager.Insert_ProductInfo(
							request.getParameter("name"),
							request.getParameter("manufacturer"),
							request.getParameter("productCode"),
							request.getParameter("productCodeType"),
							request.getParameter("dosageForm"),
							request.getParameter("strength"),
							request.getParameter("containerSize"),
							request.getParameter("lot"),
							request.getParameter("expirationDate")
							);
					if(result == false) {
						ErrorMsg = "Duplicated Product Code";
					}
				}
				else if(action.equals("Change")) {
					// 제품 정보 변경시 동작
					boolean result = productManager.Update_ProductInfo(
							request.getParameter("name"),
							request.getParameter("manufacturer"),
							request.getParameter("productCode"),
							request.getParameter("productCodeType"),
							request.getParameter("dosageForm"),
							request.getParameter("strength"),
							request.getParameter("containerSize"),
							request.getParameter("lot"),
							request.getParameter("expirationDate")
							);
					if(result == false) {
						ErrorMsg = "Can't Find Product Code";
					}
				}
				// 제품 정보 삭제
				else if(action.equals("Delete")) {
					boolean result = productManager.Delete_ProductInfo(request.getParameter("productCode"));
					if(result == false) {
						ErrorMsg = "Can't Find Product Code";
					}
				}
			}
			// 저장된 제품 리스트를 view로 전달
			Iterable<InfoType_Product> products= productManager.getProductList();
			model.addAttribute("products", products);
			return "redirect:/productlist";
		//}
	}
	
	// 제품정보 저장 리스트를 표시
	@RequestMapping(value = "/productlist", method = RequestMethod.GET)
	public String productlist(HttpServletRequest request, ModelMap model, Principal principal) {
		
		model.addAttribute("homeUrl", servletContext.getContextPath());
		// 저장된 제품 정보를 검색
		Iterable<InfoType_Product> products= productManager.getProductList();
		// view로 제품 정보들 전달
		model.addAttribute("products", products);
		model.addAttribute("errorMsg", ErrorMsg );
		ErrorMsg = "";
		return "productlist";

	}
}
