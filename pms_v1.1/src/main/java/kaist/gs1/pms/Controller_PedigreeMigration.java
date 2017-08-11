package kaist.gs1.pms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kaist.gs1.pms.MyShellCommand;
import kaist.gs1.pms.InfoType_User;
import kaist.gs1.pms.RepositoryDao_User;

/**
 * https로 pedigree 수신을 진행하는 controller
 */
@Controller
public class Controller_PedigreeMigration {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_PedigreeGenerator pedigreeGenerator;
	@Autowired
	Manager_Certificate certificateManager;
	@Autowired
	Manager_PedigreeInfo pedigreeManager;
	@Autowired
	Manager_PedigreeExporter exportManager;
	@Autowired
	Manager_PedigreeImporter importManager;
	@Autowired
	Manager_PartnerInfo partnerManager;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);
	String ErrorMsg = "";
	/**
	 * https로 pedigree 수신을 진행
	 */
	@RequestMapping(value = "/import", method = RequestMethod.POST)
	public @ResponseBody String ssltest(ModelMap model, HttpServletRequest request) {
		//client Certificate가 있다면 현재는 출력만 한다. https로 pedigree가 교환되므로 secure channel을 위해 client certificate를 이용해야 할 수 있다.
		X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        if (certs != null) {
            for (int i = 0; i < certs.length; i++) {
                System.out.println("Client Certificate [" + i + "] = "
                        + certs[i].toString());
            }
        }
		
        // pedigree를 수신 
        System.out.println("request content length:" + request.getContentLength() );
        BufferedReader in;
        StringBuffer buffer = new StringBuffer();
        String inputLine;
		try {
			in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			
			while ((inputLine = in.readLine()) != null) {
				buffer.append(inputLine);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("request content :" + buffer);
        String importPedigree = buffer.toString();
        String response_str = "data stored";
        // pedigree 유효성 검증 및 import
    	importManager.Import_Pedigree(importPedigree);
    	return response_str; 
	}
	// pedigree를 파트너에게 전달하는 데 사용
	// view에서 pedigree를 선택하여 export를 누르면 modal 창이 떠서 대상 파트너 정보를 사용자에게 입력받고, /export로 정보를 전송하여 pedigree 전달 절차 진행
	@RequestMapping(value = "/export", method = RequestMethod.POST)
	public String export(ModelMap model, HttpServletRequest request) {
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		// 사용자가 modal에서 선택한 파트너 정보를 검색
		InfoType_Partner partnerInfo = partnerManager.selectPartnerInfo(request.getParameter("selectedPartner")); 
		if(partnerInfo != null) {
			// 해당 파트너에게 pedigree를 전달
			exportManager.Export_Pedigree(partnerInfo.getImportAddress(), request.getParameter("selectedShippedPedigreeSgtin"));
			InfoType_Pedigree ped = exportManager.Find_Pedigree(request.getParameter("selectedShippedPedigreeSgtin"));
			ped.setRecipientAddress(partnerInfo.getPmsAddress());
			pedigreeManager.savePedigree(ped);
		}
		
		return "redirect:/pedigreelist";
	}
	
}
