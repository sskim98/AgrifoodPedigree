package kaist.gs1.pms;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import javax.xml.parsers.*;

/*
 * pedigee  관리자
 */
@Component
public class Manager_PedigreeInfo extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    // 저장된 pedigree 리스트를 얻기 위한 루틴
    public Iterable<InfoType_Pedigree> Get_PedigreeList() {
    	Iterable<InfoType_Pedigree> pedigrees = getPedigreeList();
        return pedigrees;
    }
    // trustCertificate 변경에 따른 pedigree 재검증 루틴
    public boolean revalidate(String id) {
    	boolean status = false;
    	InfoType_Pedigree pedigreeInfo = selectPedigree(id); 
		if(pedigreeInfo != null) {
			if(!pedigreeInfo.getType().equals("Initial")) {
				InfoType_Error errorMsg = checkAllNestedPedigree(pedigreeInfo.getXml());
		        if(errorMsg.getCode().equals("0")) {
		        	// 유효한 pedigree 인 경우에 
		        	pedigreeInfo.setValidationStatus("success");
		        }
		        else if(errorMsg.getCode().equals("1")) {
		        	// 유효한 pedigree 인 경우에 
		        	pedigreeInfo.setValidationStatus("success(certificate timestamp expired)");
		        }
		        else {
		        	pedigreeInfo.setValidationStatus("failure");
		        }
		        pedigreeInfo.setValidationDetail(errorMsg.getDetail());
		        
				status = savePedigree(pedigreeInfo);
			}
		}
    	return status;
    }
}