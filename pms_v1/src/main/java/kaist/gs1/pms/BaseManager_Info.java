package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * info 타입 정보 관리자
 */
//@Component
public class BaseManager_Info extends BaseManager_Signer {

    @Resource
    protected RepositoryDao_User userRepositoryDao;
    @Resource
    protected RepositoryDao_Company companyRepositoryDao;
    @Resource
    protected RepositoryDao_Partner partnerRepositoryDao;
    @Resource
    protected RepositoryDao_Certificate certificateRepositoryDao;
    @Resource
    protected RepositoryDao_Product productRepositoryDao;
    @Resource
    protected RepositoryDao_Pedigree pedigreeRepositoryDao;

    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    private InfoType_Pedigree pedigree = null;
    private InfoType_User user = null;
    private InfoType_Company company = null;
    private InfoType_Partner partner = null;
    

    // PMS 사용자 리스트 획득 루틴
    public Iterable<InfoType_User> getUserList() {
    	return userRepositoryDao.findAll();
    }
    // id 사용자 획득 루틴
    public InfoType_User selectUserInfo(String userid) {
        user = userRepositoryDao.findUserByUserID(userid);
        return user;
    }
    
    // 사용자 정보 저장 루틴
    public boolean saveUserInfo(InfoType_User info) {
    	userRepositoryDao.save(info);
		return true;
    }
    
    // 사용자 정보 삭제 루틴
    public boolean removeUserInfo(InfoType_User info) {
    	userRepositoryDao.delete(info);
		return true;
    }

    // 회사 정보 획득 루틴
    public InfoType_Company getCompanyInfo() {
    	if(companyRepositoryDao.count()>0) {
    		company = companyRepositoryDao.findAll().iterator().next();
    	}
        return company;
    }
    // 회사 정보 저장 루틴
    public boolean saveCompanyInfo(InfoType_Company info) {
    	companyRepositoryDao.save(info);
		return true;
    }
    // 모든 회사 정보 삭제 루틴
    public boolean removeAllCompanyInfo() {
    	if(companyRepositoryDao.count()>0) {
    		companyRepositoryDao.deleteAll();
    	}
		return true;
    }

    // 모든 파트너사 정보획득 루틴
    public Iterable<InfoType_Partner> getPartnerList() {
    	return partnerRepositoryDao.findAll();
    }
    
    // 파트너사 정보 획득 루틴
    public InfoType_Partner selectPartnerInfo(String addressId) {
        partner = partnerRepositoryDao.findPartnerByAddressId(addressId);
        return partner;
    }
    // 파트너 정보 저장 루틴
    public boolean savePartnerInfo(InfoType_Partner info) {
    	partnerRepositoryDao.save(info);
		return true;
    }
    // 파트너 정보 삭제 루틴
    public boolean removePartnerInfo(InfoType_Partner info) {
    	partnerRepositoryDao.delete(info);
		return true;
    }
    //  인증서 저장 루틴
    public boolean saveCertificateInfo(InfoType_Certificate info) {
    	InfoType_Certificate certificateAndPrivateKey = this.selectCertificateInfo();
        if(certificateAndPrivateKey != null) {
        	certificateRepositoryDao.deleteAll();
        	certificateRepositoryDao.save(info);
        	return true;
        }
        else {
        	return false;
        }
    }
    // 특정 인증서 정보 획득 루틴
    public InfoType_Certificate selectCertificateInfo() {
    	Iterable<InfoType_Certificate> certs =  certificateRepositoryDao.findAll();
    	return certs.iterator().next();
    }
    // 모든 제품 정보 획득 루틴
    public Iterable<InfoType_Product> getProductList() {
    	return productRepositoryDao.findAll();
    }
    // 제품 정보 획득 루틴
    public InfoType_Product selectProductInfo(String productCode) {
        return productRepositoryDao.findProductByProductCode(productCode);
    }
    // 제품 정보 저장 루틴
    public boolean saveProductInfo(InfoType_Product info) {
    	productRepositoryDao.save(info);
		return true;
    }
    // 제품 정보 삭제 루틴
    public boolean removeProductInfo(InfoType_Product info) {
    	productRepositoryDao.delete(info);
		return true;
    }
    
    // pedigree정보 리스트 획득 루틴
    public Iterable<InfoType_Pedigree> getPedigreeList() {
    	return pedigreeRepositoryDao.findAll();
    }
    // sgtin의 pedigree 검색 루틴
    public InfoType_Pedigree selectPedigree(String sgtin) {
        pedigree = pedigreeRepositoryDao.findPedigreeBySgtin(sgtin);
        return pedigree;
    }
    // pedigree 저장 루틴
    public boolean savePedigree(InfoType_Pedigree pedigree) {
    	pedigreeRepositoryDao.save(pedigree);
		return true;
    }
    // pedigree 삭제 루틴
    public boolean removePedigree(InfoType_Pedigree pedigree) {
    	pedigreeRepositoryDao.delete(pedigree);
		return true;
    }
    
}