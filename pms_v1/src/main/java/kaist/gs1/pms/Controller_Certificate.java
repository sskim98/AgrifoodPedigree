package kaist.gs1.pms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kaist.gs1.pms.MyShellCommand;
import kaist.gs1.pms.InfoType_User;
import kaist.gs1.pms.RepositoryDao_User;

/**
 * Handles requests for the application home page.
 */
@Controller
public class Controller_Certificate {
	@Autowired
	ServletContext servletContext;
	@Autowired
	Manager_Certificate certManager;
	
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
	 * 스프링에서 외부 파일을 실행할 수 있는지 테스트 하기 위함, 
	 * 현재는 필요없지만 사설인증서 기능 구현시 필요
	 */
	public void commandTest(){
		MyShellCommand obj = new MyShellCommand();

		String domainName = "google.com";
		String command = "ping " + domainName;
		
		String output = obj.executeCommand(command);

		//System.out.println(output);
		
		List<String> list = obj.getIpAddress(output);

		if (list.size() > 0) {
			System.out.printf("%s has address : %n", domainName);
			for (String ip : list) {
				System.out.println(ip);
			}
		} else {
			System.out.printf("%s has NO address. %n", domainName);
		}
	}
	/*
	 * 인증서 관리 메뉴로 왔을 때 저장된 개인키 및 인증서를 얻어서 보여줌
	 * 인증서에 newline 과 같은 문자가 있을 시 웹에 맞게 변환후 표시
	 */
	@RequestMapping(value = "/certificate", method = RequestMethod.GET)
	public String certificate(ModelMap model, HttpServletRequest request) {		
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		InfoType_Certificate certAndPrivateKey = certManager.getPMSCertificateAndPrivateKey();
		model.addAttribute("privateKey", certAndPrivateKey.getPrivateKey().replaceAll("\r?\n", "<br>"));
		model.addAttribute("pmsCertificate", certAndPrivateKey.getPmsCertificate().replaceAll("\r?\n", "<br>"));
		//model.addAttribute("caCertificate", certAndPrivateKey.getCaCertificate().replaceAll("\r?\n", "<br>"));
		return "certificate_registration";
	}
	/*
	 * 인증서를 업로드 하기 위한 함수
	 * multipartFile로 개인키, 인증서, CA인증서를 업로드함
	 */
	@RequestMapping(value = "/uploadCertificate", method = RequestMethod.POST)
	public String uploadCertificate(ModelMap model, MultipartHttpServletRequest request) {		
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		Iterator<String> iterator = request.getFileNames();
	    MultipartFile multipartFile = null;
	    InfoType_Certificate certificateInfo = new InfoType_Certificate("", "", "");
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
	        	
	        	if(fileContent.contains("RSA PRIVATE KEY" )) {
	        		certificateInfo.setPrivateKey(fileContent);
	        	}
	        	else if(fileContent.contains("BEGIN CERTIFICATE" )) {
	        		if(i==1) {
	        			certificateInfo.setPmsCertificate(fileContent);
	        		}
	        		else if(i==2) {
	        			certificateInfo.setCaCertificate(fileContent);
	        		}
	        	}
	            System.out.printf("------------- file start -------------\n");
	            System.out.printf("name : "+multipartFile.getName()+"\n");
	            System.out.printf("filename : "+multipartFile.getOriginalFilename()+"\n");
	            System.out.printf("size : "+multipartFile.getSize()+"\n");
	            System.out.printf("file : "+fileContent+"\n");
	            System.out.printf("-------------- file end --------------\n");
	        }
	    }//certManager에 인증서 정보를 저장
	    certManager.saveCertificateInfo(certificateInfo);
	    try {
	    	//certManager.storeKey(certManager.getX509CertificatefromString(certificateInfo.getCaCertificate()), certManager.getX509CertificatefromString(certificateInfo.getPmsCertificate()), null);
	    	// 인증서의 path를 검증, 검증이 잘 되지 않았을때 메시지 표시 필요
			certManager.checkPKICertificatePath(certManager.getX509CertificatefromString(certificateInfo.getPmsCertificate()), certManager.getX509CertificatefromString(certificateInfo.getCaCertificate()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/certificate";
	}
	
	/*
	 * 사설 인증서 생성을 위한 함수, 나중에 사설인증 기능 구현시 필요
	 */
	@RequestMapping(value = "/cert", method = RequestMethod.POST)
	public String cert(ModelMap model, HttpServletRequest request) {
		model.addAttribute("homeUrl", servletContext.getContextPath() );
		certManager.test();
		
		return "index";
	}
}
