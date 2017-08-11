package kaist.gs1.pms;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
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
	@Autowired
	Manager_Search searchManager;
	
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
		// pedigree 들을 검색하여
		Iterator<InfoType_Pedigree> pedIterator = pedigrees.iterator();
		ArrayList<InfoType_TracePath> traceInfoList = new ArrayList<InfoType_TracePath>();
		while(pedIterator.hasNext()) {
			InfoType_Pedigree pedigreeInfo = pedIterator.next();
			// trace 정보를 추출하고
			InfoType_TracePath path = new InfoType_TracePath(pedigreeInfo.get_id());
			path.setPath(searchManager.Get_Pedigree_TraceInfo(pedigreeInfo.getXml()));
			for(int i=0; i<path.getPath().size(); i++) {
				// 각 pedigree 들에 대한 유효성 정보를 같이 넣어서
				path.getPath().get(i).setValidity(path.getPath().get(i).getValidity().replace("\n", ""));
			}
			traceInfoList.add(path);
		}
		// 검색 결과 및 pedigree 이동정보를 view로 전달
		model.addAttribute("traceInfoList", traceInfoList);
		ErrorMsg = "";
		return "pedigreelist";
		
	}
	
	
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
	
	// pedigree를 파트너에게 전달하는 데 사용
		// view에서 pedigree를 선택하여 export를 누르면 modal 창이 떠서 대상 파트너 정보를 사용자에게 입력받고, /export로 정보를 전송하여 pedigree 전달 절차 진행
		@RequestMapping(value = "/revalidate", method = RequestMethod.POST)
		public String export(ModelMap model, HttpServletRequest request) {
			model.addAttribute("homeUrl", servletContext.getContextPath() );
			
			if(request.getParameter("selectedIndex") != null) {
			pedigreeManager.revalidate(request.getParameter("selectedIndex"));
			}
			
			return "redirect:/pedigreelist";
		}
	
}
