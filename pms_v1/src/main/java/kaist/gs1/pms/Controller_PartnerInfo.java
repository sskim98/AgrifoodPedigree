package kaist.gs1.pms;

import java.io.IOException;
import java.net.InetAddress;
import java.security.Principal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kaist.gs1.pms.MyShellCommand;
import kaist.gs1.pms.InfoType_User;
import kaist.gs1.pms.RepositoryDao_User;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Controller_PartnerInfo {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_PartnerInfo partnerManager;
	@Autowired
	Manager_CompanyInfo companyManager;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);
	String ErrorMsg = "";
	/**
	 * 파트너 정보에 대한 
	 */
	@RequestMapping(value = "/change_partner", method = RequestMethod.POST)
	public String change_partner(ModelMap model, MultipartHttpServletRequest request) {
		ErrorMsg = "";
		model.addAttribute("homeUrl", servletContext.getContextPath());
		
		// 파트너에 대한 사설인증서가 있는 경우 추가
		//사설 인증 페디그리의 경우 사설인증서가 있어야 인증 가능
		Iterator<String> iterator = request.getFileNames();
	    MultipartFile multipartFile = null;
	    String privateCertificate = null;
	    while(iterator.hasNext()){
	        multipartFile = request.getFile(iterator.next());
	        try {
	        	privateCertificate = new String(multipartFile.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    // 파트너 정보 추가의 경우 각 필드가 데이터가 있는지 확인
		//if(request.getParameter("from").contentEquals("partnerlist_page")){
		String action = request.getParameter("action");
		if(action.equals("Add")) {
			if(request.getParameter("name").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("street1").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("street2").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("city").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("state").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("postalCode").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("country").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("addressId").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("pmsAddress").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("importAddress").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else {
				//모든 정보가 있는 경우 파트너 정보를 입력
				boolean result = partnerManager.Insert_PartnerInfo(
					request.getParameter("name"),
					request.getParameter("street1"),
					request.getParameter("street2"),
					request.getParameter("city"),
					request.getParameter("state"),
					request.getParameter("postalCode"),
					request.getParameter("country"),
					request.getParameter("addressId"),
					request.getParameter("pmsAddress"),
					request.getParameter("importAddress"),
					request.getParameter("userName"),
	    			request.getParameter("userTitle"),
	    			request.getParameter("userTelephone"),
	    			request.getParameter("userEmail"),
	    			request.getParameter("userUrl"),
	    			privateCertificate
				);
				
				// 기존에 데이터가 있어서 삽입이 안되는 경우 오류 메시지 표시
				if(result == false) {
					ErrorMsg = "Duplicated Partner's PMS Address";
				}
				// master 데이터로서 파트너 정보 저장
				InfoType_Company company = companyManager.getCompanyInfo();
				String queryString = company.getEpcisAddress()+"/Service/VocabularyCapture?";
				partnerManager.createMasterData(
						queryString,
						request.getParameter("name"),
						request.getParameter("street1"),
						request.getParameter("street2"),
						request.getParameter("city"),
						request.getParameter("state"),
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
			}
		} // 파트너 정보 수정의 경우 필드가 잘 입력되어 있는지 확인
		else if(action.equals("Change")) {
			if(request.getParameter("name").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("street1").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("street2").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("city").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("state").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("postalCode").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("country").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("addressId").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else if(request.getParameter("importAddress").trim().isEmpty()) {
				ErrorMsg = "Please fill in all the blanks on this form";
			}
			else { // 필드가 잘 들어가 있는 경우 수정
				boolean result = partnerManager.Update_PartnerInfo(
					request.getParameter("index"),
					request.getParameter("name"),
					request.getParameter("street1"),
					request.getParameter("street2"),
					request.getParameter("city"),
					request.getParameter("state"),
					request.getParameter("postalCode"),
					request.getParameter("country"),
					request.getParameter("addressId"),
					request.getParameter("pmsAddress"),
					request.getParameter("importAddress"),
					request.getParameter("userName"),
	    			request.getParameter("userTitle"),
	    			request.getParameter("userTelephone"),
	    			request.getParameter("userEmail"),
	    			request.getParameter("userUrl"),
	    			privateCertificate
				);
				if(result == false) { // 수정된 파트너 정보의 PMS 주소가 이전과 다른 경우 오류메시지 표시
					ErrorMsg = "Can't Find Partner's PMS Address";
				}
				// 수정된 파트너 정보를 EPCIS 마스터 데이터에도 적용
				InfoType_Company company = companyManager.getCompanyInfo();
				String queryString = company.getEpcisAddress()+"/Service/VocabularyCapture";
				partnerManager.createMasterData(
						queryString,
						request.getParameter("name"),
						request.getParameter("street1"),
						request.getParameter("street2"),
						request.getParameter("city"),
						request.getParameter("state"),
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
			}
		} // 파트너 정보를 EPCIS 쿼리를 통해 얻어오는 루틴
		else if(action.equals("EPCIS Query")) {
			model.addAttribute("homeUrl", servletContext.getContextPath());
					
			InfoType_Company company = companyManager.getCompanyInfo();
			String queryString = company.getEpcisAddress()+"/Service/Poll/SimpleMasterDataQuery?";
			queryString = queryString + "includeAttributes=true&includeChildren=true&vocabularyName=urn:epcglobal:epcis:vtype:BusinessLocation&";
			partnerManager.fetchEPCISMasterEvents(queryString);
		}
		return "redirect:/partnerlist";
	}
	/*
	 * 파트너 관리 시 기본화면 표시, 리스트 등 
	 */
	@RequestMapping(value = "/partnerlist", method = RequestMethod.GET)
	public String partnerlist(HttpServletRequest request, ModelMap model, Principal principal) {
		
		model.addAttribute("homeUrl", servletContext.getContextPath());
		// 파트너 리스트 얻기
		Iterable<InfoType_Partner> partners= partnerManager.getPartnerList();
		Iterator<InfoType_Partner> it = partners.iterator();
	    while(it.hasNext()){
	        InfoType_Partner partner = it.next();
	        if(partner.getPrivateCertificate() != null) {
	        	//파트너 정보에 사설 인증서가 있는 경우 newline을 삭제후 설정
	        	partner.setPrivateCertificate(partner.getPrivateCertificate().replaceAll("\r?\n", ""));
	        }
	    }
	    //View로 파트너 정보 전달
		model.addAttribute("partners", partners);
		model.addAttribute("errorMsg", ErrorMsg );
		ErrorMsg = "";
		return "partnerlist";

	}
}
