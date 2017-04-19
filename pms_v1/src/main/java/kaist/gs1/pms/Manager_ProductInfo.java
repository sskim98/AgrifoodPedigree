package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * 제품 정보 관리자, 원래는 GS1 source를 통해 얻어와야 함
 */
@Component
public class Manager_ProductInfo extends BaseManager_Info {
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    
    // 제품 정보를 추가하기 위한 루틴
    public boolean Insert_ProductInfo(String name, String manufacturer, String productCode, String productCodeType, String dosageForm, String strength, String containerSize, String lot, String expirationDate) {
    	// 특정 제품 코드를 가진 제품 정보가 없는 경우
    	InfoType_Product product = this.selectProductInfo(productCode);
        if(product == null) {
        	// 새로운 제품 정보 및 코드를 저장
        	product = new InfoType_Product(name, manufacturer, productCode, productCodeType, dosageForm, strength, containerSize, lot, expirationDate);
        	saveProductInfo(product);
    	return true;
        }
        else {
        	return false;
        }
    }
    // 제품 정보를 업데이트 하기 위한 루틴
    public boolean Update_ProductInfo(String name, String manufacturer, String productCode, String productCodeType, String dosageForm, String strength, String containerSize, String lot, String expirationDate) {
    	// 같은 제품 코드로 저장된 제품 정보가 있는 경우
    	InfoType_Product product = this.selectProductInfo(productCode);
        if(product != null) {
        	// 이전의 제품 정보를 삭제하고 새로운 정보로 저장
        	removeProductInfo(product);
        	product = new InfoType_Product( name, manufacturer, productCode, productCodeType, dosageForm, strength, containerSize, lot, expirationDate);
        	saveProductInfo(product);
        	return true;
        }
        else {
        	return false;
        }
    }
    
    // 특정 제품 코드를 가진 제품 정보를 삭제하는 루틴
    public boolean Delete_ProductInfo(String productCode) {
    	// 제품 코드로 검색하여 검색된 제품 정보를 삭제
    	InfoType_Product product = this.selectProductInfo(productCode);
    	if(product != null) {
    		removeProductInfo(product);
    	}
		return true;
    }

}