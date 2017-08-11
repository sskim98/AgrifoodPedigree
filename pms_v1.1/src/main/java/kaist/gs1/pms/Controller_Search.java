package kaist.gs1.pms;

import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
			// received pedigree면 pms의 주소를 전달, 그렇지 않으면 다른 pms로 전송한 상황이므로 수신자의 검색 주소를 전달
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
				ArrayList<InfoType_TraceNode> path = searchManager.Get_Pedigree_TraceInfo(pedigree.getXml());

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
