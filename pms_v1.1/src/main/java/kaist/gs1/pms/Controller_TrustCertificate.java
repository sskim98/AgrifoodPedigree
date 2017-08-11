package kaist.gs1.pms;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.security.cert.CertificateEncodingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Controller_TrustCertificate {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_TrustCertificate trustCertManager;
	String ErrorMsg = "";
	
	boolean toggle = false;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_TrustCertificate.class);
	
	/*
	 * 인증서 관리 메뉴로 왔을 때 저장된 개인키 및 인증서를 얻어서 보여줌
	 * 인증서에 newline 과 같은 문자가 있을 시 웹에 맞게 변환후 표시
	 */
	@RequestMapping(value = "/trustCertificate", method = RequestMethod.GET)
	public String trustCertificate(ModelMap model, HttpServletRequest request) {		
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		Iterable<InfoType_TrustCertificate> trustCertificates = trustCertManager.getAllTrustCertificates();
		Iterator<InfoType_TrustCertificate> iterator = trustCertificates.iterator();
		while(iterator.hasNext()) {
			InfoType_TrustCertificate certificate = iterator.next();
			certificate.setCaCertificateString(certificate.getCaCertificateString().replaceAll("\r?\n", "<br>"));
		}
		model.addAttribute("trustCertificates", trustCertificates);

		model.addAttribute("errorMsg", ErrorMsg.replaceAll("\r?\n", "<br>") );
		ErrorMsg = "";
		
		return "trustCertificate";
	}
	/*
	 * 인증서를 업로드 하기 위한 함수
	 * multipartFile로 private CA인증서를 업로드함
	 */
	
	@RequestMapping(value = "/uploadTrustCertificate", method = RequestMethod.POST)
	public String uploadTrustCertificate(ModelMap model, MultipartHttpServletRequest request) throws CertificateEncodingException, NoSuchAlgorithmException {		
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		Iterator<String> iterator = request.getFileNames();
	    MultipartFile multipartFile = null;
	    InfoType_TrustCertificate trustCertificateInfo = null;
	    if(request.getParameter("hasConfidence") != null) {
	    	trustCertificateInfo = new InfoType_TrustCertificate(request.getParameter("hasConfidence"), "", "", "Manual");
	    }
	    else {
	    	trustCertificateInfo = new InfoType_TrustCertificate("Not_Trust", "", "", "Manual");
	    }
	    //업로드 된 파일들을 저장
	    for(int i=0; iterator.hasNext(); i++){
	        multipartFile = request.getFile(iterator.next());
	        String fileContent = null;
	        try {
	        	fileContent = new String(multipartFile.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        if(multipartFile.isEmpty() == false){
	        	if(fileContent.contains("BEGIN CERTIFICATE" )) {
	        		trustCertificateInfo.setCaCertificateString(fileContent);
	        		trustCertificateInfo.setSerialNumber(trustCertificateInfo.getCaCertificate().getSerialNumber().toString(16));
	        		System.out.printf("------------- file start -------------\n");
    	            System.out.printf("name : "+multipartFile.getName()+"\n");
    	            System.out.printf("filename : "+multipartFile.getOriginalFilename()+"\n");
    	            System.out.printf("size : "+multipartFile.getSize()+"\n");
    	            //System.out.printf("file : "+fileContent+"\n");
    	            System.out.printf("-------------- file end --------------\n");
    	            trustCertManager.uploadTrustCertificateInfo(trustCertificateInfo);
	        	}
	        }
	    }
	    
		return "redirect:/trustCertificate";
	}
	
	/*
	 * 신뢰 인증서를 삭제 하기 위한 함수
	 */
	@RequestMapping(value = "/removeTrustCertificate", method = RequestMethod.POST)
	public String removeTrustCertificate(ModelMap model, HttpServletRequest request) {		
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		if(request.getParameter("serialNumber").equals("")) {
			ErrorMsg = "Please select a private certificate!";
		}
		else {
			trustCertManager.deleteTrustCertificate(request.getParameter("serialNumber"));
		}
	    
		return "redirect:/trustCertificate";
	}
	
	/*
	 * 신뢰 인증서를 삭제 하기 위한 함수
	 */
	@RequestMapping(value = "/changeConfidence", method = RequestMethod.POST)
	public String changeConfidence(ModelMap model, HttpServletRequest request) {		
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		if(request.getParameter("serialNumber").equals("")) {
			ErrorMsg = "Please select a private certificate!";
		}
		else {
			trustCertManager.changeConfidenceOfTrustCertificate(request.getParameter("serialNumber"));
		}
	    
		return "redirect:/trustCertificate";
	}
	
}
