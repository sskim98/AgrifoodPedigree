package kaist.gs1.pms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
public class Controller_Certificate {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_Certificate certManager;
	@Autowired
	Manager_TrustCertificate trustCertManager;
	
	boolean toggle = false;
	
	private static final Logger logger = LoggerFactory.getLogger(Controller_Certificate.class);
	/*
	 * 로그인 시 기본화면 표시, 나중에 pedigree 동작절차에 대해 좀 더 자세한 내용을 담을 필요가 있음
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String defaultPage(ModelMap model) {
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		return "home";
	}

	
	/*
	 * 인증서 관리 메뉴로 왔을 때 저장된 개인키 및 인증서를 얻어서 보여줌
	 * 인증서에 newline 과 같은 문자가 있을 시 웹에 맞게 변환후 표시
	 */
	@RequestMapping(value = "/certificate", method = RequestMethod.GET)
	public String certificate(ModelMap model, HttpServletRequest request) {		
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		InfoType_Certificate certAndPrivateKey = certManager.getPMSCertificateAndPrivateKey();
		if(certAndPrivateKey != null) {
			model.addAttribute("certificateType", certAndPrivateKey.getCertificateType());
			model.addAttribute("privateKey", certAndPrivateKey.getPrivateKeyString().replaceAll("\r?\n", "<br>"));
			model.addAttribute("certificateValidity", certAndPrivateKey.getValidity().replaceAll("\r?\n", "<br>"));
			model.addAttribute("pmsCertificate", certAndPrivateKey.getPmsCertificateString().replaceAll("\r?\n", "<br>"));
			if(certAndPrivateKey.getCaCertificateStringArray().isEmpty() == false) {
				model.addAttribute("caCertificate", certAndPrivateKey.getCaCertificateStringArray().get(0).replaceAll("\r?\n", "<br>"));
			}
			model.addAttribute("privateKeyFileName", certAndPrivateKey.getPrivateKeyFileName());
			model.addAttribute("pmsCertificateFileName", certAndPrivateKey.getPmsCertificateFileName());
			model.addAttribute("caCertificateFileName", certAndPrivateKey.getCaCertificateFileName());
			model.addAttribute("privateRootCertificateFileName", certAndPrivateKey.getPrivateRootCertificateFileName());
		}
		
		
		return "certificate_registration";
	}
	/*
	 * 인증서를 업로드 하기 위한 함수
	 * multipartFile로 개인키, 인증서, CA인증서를 업로드함
	 */
	@RequestMapping(value = "/uploadCertificate", method = RequestMethod.POST)
	public String uploadCertificate(ModelMap model, MultipartHttpServletRequest request) throws FileNotFoundException, IOException {		
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		Iterator<String> iterator = request.getFileNames();
	    MultipartFile multipartFile = null;
	    
	    // 기존 인증서, trustStore 내용 삭제  /////////////////////////////////////////////////////
	    InfoType_Certificate certificateInfo = certManager.getPMSCertificateAndPrivateKey();
	    if(certificateInfo != null) {
	    	if(certificateInfo.getCaCertificateStringArray() != null) {
	    		Iterator<String> certIterator = certificateInfo.getCaCertificateStringArray().iterator();
	    		if(certIterator.hasNext()) {
	    			String certificate = certIterator.next();
	    			X509Certificate caCertificate = null;
	    			try {
	    				// 기존 저장된 인증서가 PKI 타입이면 PKI TrustStore에서 삭제
	    				if(certificateInfo.getCertificateType().equals("PKI")) {
	    					caCertificate = certManager.getX509CertificatefromString(certificate);
	    					certManager.deleteCertificateFromPKITrustStore(caCertificate);
	    				} // 기존 저장된 인증서가 private 타입이면 private TrustStore에서 삭제
	    				else if(certificateInfo.getCertificateType().equals("Private")) {
	    					caCertificate = certManager.getX509CertificatefromString(certificate);
	    					certManager.deleteCertificateFromPrivateTrustStore(caCertificate);
	    					InfoType_TrustCertificate trustCertificateInfo = trustCertManager.selectTrustCertificateBySerialNumber(caCertificate.getSerialNumber().toString(16));
	    					trustCertManager.removeTrustCertificate(trustCertificateInfo);
	    				}
	    			} catch (CertificateException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			} catch (FileNotFoundException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    		}
	    	}
	    }
	    //////////////////////////////////////////////////////////////////////////////////////
	    
	    String certificateType = "";
	    if(request.getParameter("certificateType") != null) {
	    	certificateType = request.getParameter("certificateType");
	    }
	    else {
	    	certificateType = "Unknown";
	    }
	    
	    certificateInfo = new InfoType_Certificate(certificateType);
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
	        	if(multipartFile.getName().equals("privateKey")) {
	        		if(fileContent.contains("RSA PRIVATE KEY" )) {
		        		certificateInfo.setPrivateKeyString(fileContent);
		        		certificateInfo.setPrivateKeyFileName(request.getParameter("privateKeyFileName"));
		        	}
	        	}
	        	else if(multipartFile.getName().equals("pmsCertificate")) {
	        		if(fileContent.contains("BEGIN CERTIFICATE" )) {
	        			certificateInfo.setPmsCertificateString(fileContent);
	        			certificateInfo.setPmsCertificateFileName(request.getParameter("pmsCertificateFileName"));
		        	}
	        	}
	        	else if(multipartFile.getName().equals("caCertificate")) {
	        		if(fileContent.contains("BEGIN CERTIFICATE" )) {
	        			certificateInfo.addCaCertificateStringArray(fileContent);
	        			certificateInfo.setCaCertificateFileName(request.getParameter("caCertificateFileName"));
        				if(certificateType.equals("Private")) {
							certificateInfo.setPrivateRootCertificateString(fileContent);
							certificateInfo.setPrivateRootCertificateFileName(request.getParameter("caCertificateFileName"));
							InfoType_TrustCertificate trustCertificateInfo = new InfoType_TrustCertificate("Trust", fileContent, certManager.getX509CertificatefromString(fileContent).getSerialNumber().toString(16), "ReadOnly");
						    trustCertManager.uploadTrustCertificateInfo(trustCertificateInfo);
						}
		        	}
	        	}
	        	
	            System.out.printf("------------- file start -------------\n");
	            System.out.printf("name : "+multipartFile.getName()+"\n");
	            System.out.printf("filename : "+multipartFile.getOriginalFilename()+"\n");
	            System.out.printf("size : "+multipartFile.getSize()+"\n");
	            //System.out.printf("file : "+fileContent+"\n");
	            System.out.printf("-------------- file end --------------\n");
	            
	        }
	    }
	    
	    //새로이 업로드된 인증서가 PKI 타입이면 경로체크 후 결과를 리턴
	    if(request.getParameter("certificateType").equals("PKI")) {
			try {
				InfoType_Error errorMsg = certManager.checkPKICertificatePath(certificateInfo.getPmsCertificate(), certificateInfo.getCaCertificateArray());
				if(errorMsg.getCode().equals("0")) {
					certificateInfo.setValidity("valid");
				}
				else {
					certificateInfo.setValidity(errorMsg.getDetail());
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }//새로이 업로드된 인증서가 private 타입이면 경로체크 후 결과를 리턴
	    else if(request.getParameter("certificateType").equals("Private")) {
	    	InfoType_Error errorMsg;
			try {
				errorMsg = certManager.checkPrivateCertificatePath(certificateInfo.getPmsCertificate(), certificateInfo.getCaCertificateArray());
				if(errorMsg.getCode().equals("0")) {
					certificateInfo.setValidity("valid");
				}
				else {
					certificateInfo.setValidity(errorMsg.getDetail());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    //certManager에 인증서 정보를 저장
	    certManager.saveCertificateInfo(certificateInfo);
	    
	    
		return "redirect:/certificate";
	}
	
}
