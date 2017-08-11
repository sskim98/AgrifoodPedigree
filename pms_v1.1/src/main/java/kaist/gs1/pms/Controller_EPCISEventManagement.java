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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 회사의 EPCIS로부터 이벤트 정보를 fetch,
 * EPCIS event를 처리하여 pedigree DB에 저장
 */
@Controller
public class Controller_EPCISEventManagement {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_CompanyInfo companyManager; //회사정보에 저장되어 있는 EPCIS주소를 얻기위해 필요
	@Autowired
	Manager_EPCISEvent epcisEventManager; // EPCIS 이벤트를 처리하기 위한 모듈
	@Autowired
	Manager_PedigreeGenerator pedigreeGenerator; // EPCIS 이벤트를 이용하여 pedigree를 만들기 위해 필요
	@Autowired
	Manager_PedigreeInfo pedigreeManager; //EPCIS 웹 페이지에서 현재 저장된 pedigree도 같이 표현하기 때문에 pedigreeManager가 필요, Event->pedigree 변환을 보여주기 위함
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);
	String ErrorMsg = "";
	/**
	 * EPCIS event들을 표시해주는 웹 페이지 호출
	 * 웹 페이지에서 fetch 된 event들과 pedigree들의 상태를 보여줌
	 */
	@RequestMapping(value = "/epcisEvent")
	public String epcisEvent(HttpServletRequest request, ModelMap model, Principal principal, RedirectAttributes redirectAttr) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		Iterable<InfoType_EPCISEvent> events= epcisEventManager.getAllEvent();
		model.addAttribute("events", events);
		Iterable<InfoType_Pedigree> pedigrees= pedigreeManager.Get_PedigreeList();
		model.addAttribute("pedigrees", pedigrees);
		model.addAttribute("errorMsg", ErrorMsg );
		ErrorMsg = "";
		return "epcisEvent";
	}
	/*
	 * EPCIS events의 fetch를 위한 함수
	 * fetch events 버튼을 누르면 POST로 요청이 오고 다음 절차로 이벤트 fetch
	 * 1. companyInfo를 얻어와서 EPCIS 주소를 획득
	 * 2. epcisEventManger에서 master데이터를 획득(마지막으로 fetch한 시간을 얻어서 이 시간 이후의 event만 획득 - query할때 인자로 넘김)
	 * 3. EPCIS 주소로 query를 보내 event들을 얻어오고 저장
	 */
	@RequestMapping(value = "/getEPCISEvent", method = RequestMethod.POST)
	public String getEPCISEvent(HttpServletRequest request, ModelMap model, Principal principal, RedirectAttributes redirectAttr) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		//model.addAttribute("initialPedigree", request.getParameter("xml"));
		
		InfoType_Company company = companyManager.getCompanyInfo();
		InfoType_EPCISEvent event = epcisEventManager.getMasterData();
		String queryString = company.getEpcisAddress()+"/Service/Poll/SimpleEventQuery?";
		if(event != null) {
			 queryString = queryString + "GE_recordTime=" + event.getLastEventRecordTime();
		}
		epcisEventManager.fetchEPCISEvents(queryString);
		return "redirect:epcisEvent";
	}
	/*
	 * handleEvent 버튼을 누르면 이벤트들을 파싱해서 pedigree로 만들고 저장한 후 EPCIS 페이지를 보여줌 
	 */
	@RequestMapping(value = "/handleEvent", method = RequestMethod.POST)
	public String handleEvent(HttpServletRequest request, ModelMap model, Principal principal, RedirectAttributes redirectAttr) {
		model.addAttribute("homeUrl", servletContext.getContextPath());
		epcisEventManager.handleEvents();
		return "redirect:epcisEvent";
	}
}
