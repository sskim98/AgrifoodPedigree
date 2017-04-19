package kaist.gs1.pms;

import java.awt.Menu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.security.Principal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kaist.gs1.pms.MyShellCommand;
import kaist.gs1.pms.InfoType_User;
import kaist.gs1.pms.RepositoryDao_User;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Controller_Search {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_Search searchManager;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);

	// 모바일이 pedigree path를 검색할 때 접근하는 주소
	@RequestMapping(value = "/pathSearch", method = RequestMethod.GET)
	public @ResponseBody String pathSearch(ModelMap model, HttpServletRequest request) {
		// 모바일이 전달한 sgtin 값을 획득
		String sgtin = request.getParameter("sgtin");
		//sgtin과 일치하는 pedigree 검색
		InfoType_Pedigree pedigree = searchManager.Find_Pedigree(sgtin);
		if(pedigree != null) {
			
			System.out.println("request id:" + sgtin );
			if(pedigree.getType().contentEquals("received")) {
				return servletContext.getContextPath()+"/pathSearch?sgtin=" + request.getParameter("sgtin");
			}
			else {
				return pedigree.getRecipientAddress()+"/pathSearch?sgtin=" + request.getParameter("sgtin");
			}
		}
		else {
			return "searchFailure";
		}
	}
	
	// 모바일이 pedigree를 검색할 때 접근하는 주소
		@RequestMapping(value = "/pedigreeSearch", method = RequestMethod.GET)
		public String pedigreeSearch(ModelMap model, HttpServletRequest request) {
			// 모바일이 전달한 sgtin 값을 획득
			String sgtin = request.getParameter("sgtin");
			//sgtin과 일치하는 pedigree 검색
			InfoType_Pedigree pedigree = searchManager.Find_Pedigree(sgtin);
			if(pedigree != null) {
				// pedigree의 이동 경로에 대한 정보 획득
				ArrayList<InfoType_Trace> path = searchManager.Get_Pedigree_TraceInfo(pedigree.getXml());

				// 검색 결과 및 pedigree 이동정보를 view로 전달
				model.addAttribute("searchResult", pedigree );
				model.addAttribute("traceInfo", path);
				System.out.println("request id:" + sgtin );
				return "searchResult";
			}
			else {
				return "searchFailure";
			}
		}
	
}
