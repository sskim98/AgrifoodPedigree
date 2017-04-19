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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaist.gs1.pms.MyShellCommand;
import kaist.gs1.pms.InfoType_User;
import kaist.gs1.pms.RepositoryDao_User;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Controller_PedigreeManagement {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_PedigreeGenerator pedigreeGenerator;
	@Autowired
	Manager_PedigreeInfo pedigreeManager;
	@Autowired
	Manager_PartnerInfo partnerManager;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);
	String ErrorMsg = "";
	/**
	 * 저장된 pedigree 리스트 요청
	 * 저정된 pedigree를 view로 전달함으로써 각 pedigree 클릭시 내용을 확인할 수 있도록 함
	 */
	@RequestMapping(value = "/pedigreelist")
	public String pedigreelist(HttpServletRequest request, ModelMap model, Principal principal, RedirectAttributes redirectAttr) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		//pedigree 전달을 할 수 있기 때문에 어느 파트너로 전달할 지에 대한 정보가 필요
		//따라서 파트너 정보도 같이 얻어와서 view로 전달해야 한다.
		Iterable<InfoType_Partner> partners= partnerManager.getPartnerList();
		model.addAttribute("partners", partners);
		Iterable<InfoType_Pedigree> pedigrees= pedigreeManager.Get_PedigreeList();
		model.addAttribute("pedigrees", pedigrees);
		model.addAttribute("errorMsg", ErrorMsg );
		ErrorMsg = "";
		return "pedigreelist";
		
	}
	/*
	@RequestMapping(value = "/generatePedigree")
	public String generate_initialPedigree(HttpServletRequest request, ModelMap model, Principal principal, RedirectAttributes redirectAttr) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		//model.addAttribute("initialPedigree", request.getParameter("xml"));
		
		return "pedigree_generate";
		
	}
	*/
	
	/*
	 * initial pedigree를 직접 생성할 때 사용
	 * initial pedigree 생성 버튼을 누른경우 호출 
	 */
	@RequestMapping(value = "/post_initialData", method = RequestMethod.POST)
	public String post_initialData(HttpServletRequest request, ModelMap model, Principal principal, RedirectAttributes redirectAttr) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		String gtin = request.getParameter("serialNumber").trim();
		String productName = request.getParameter("productName").trim();
		String manufacturer = request.getParameter("manufacturer").trim();
		String productCode = request.getParameter("productCode").trim();
		String containerSize = request.getParameter("containerSize").trim();
		String lot = request.getParameter("lot").trim();
		String expirationDate = request.getParameter("expirationDate").trim();
		String quantity = request.getParameter("quantity").trim();
		String itemSerialNumber = request.getParameter("itemSerialNumber").trim();
		String xml = pedigreeGenerator.Generate_InitialPedigree(gtin, productName, manufacturer, productCode, containerSize, lot, expirationDate, quantity, itemSerialNumber);
		redirectAttr.addFlashAttribute("pedigreeXml", xml);
		return "redirect:pedigreelist";
	}
	
	/*
	@RequestMapping(value = "/post_shippedData", method = RequestMethod.POST)
	public String post_shippedData(HttpServletRequest request, ModelMap model, Principal principal, RedirectAttributes redirectAttr) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		String sgtin = request.getParameter("selectedPreviousPedigreeSgtin").trim();
		String xml = pedigreeGenerator.Generate_ShippedPedigree(sgtin);
		redirectAttr.addFlashAttribute("pedigreeXml", xml);
		return "redirect:pedigreelist";
		
	}
	*/
	/*
	@RequestMapping(value = "/post_receivedData", method = RequestMethod.POST)
	public String post_receivedData(HttpServletRequest request, ModelMap model, Principal principal, RedirectAttributes redirectAttr) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		String gtin = request.getParameter("selectedIndex").trim();
		String xml = pedigreeGenerator.Generate_ReceivedPedigree(gtin);
		redirectAttr.addFlashAttribute("pedigreeXml", xml);
		return "redirect:pedigreelist";
		
	}
	*/
	
}
