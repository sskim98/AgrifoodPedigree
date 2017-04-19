package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// 회사 정보 타입
@Document(collection="CompanyInfo") 	 	
public class InfoType_Company {
	//private String _id;
	private String street; // 세부 주소
	private String country; // 국가 코드
	private String province; // 도시, 주 정보
	private String locality; // 구 
	private String name; // 회사 이름
	private String department; // 부서명
	private String postalCode; // 우편번호
	@Id
	private String domain; // itc.kaist.ac.kr
	private String email; // sskim98@itc.kaist.ac.kr
	private String epcisAddress; // http://localhost:8080/epcis
	private String addressId; // sgln
	private String pmsAddress; // http://localhost:8081/pms  :  pedigree management server address
	private String importAddress; // https://localhost:8444/pms/import  :  pedigree를 전달하기 위한 목적 주소
	private String userName; // 담당자 이름
	private String userTitle; // 담당자 직위
	private String userTelephone; // 담당자 연락처
	private String userEmail; // 담당자 이메일
	private String userUrl; // 회사 URL : http://itc.kaist.ac.kr

	public InfoType_Company(String country, String province, String locality, String name, String department, String domain, String email, String epcisAddress, String street, String postalCode, String addressId, String pmsAddress, String importAddress, String userName,	String userTitle, String userTelephone,	String userEmail, String userUrl) {
		this.country = country;
		this.province = province;
		this.locality = locality;
		this.name = name;
		this.department = department;
		this.domain = domain;
		this.email = email;
		this.epcisAddress = epcisAddress;
		this.street = street;
		this.postalCode = postalCode;
		this.addressId = addressId;
		this.pmsAddress = pmsAddress;
		this.importAddress = importAddress;
		this.userName = userName;
		this.userTitle = userTitle;
		this.userTelephone = userTelephone;
		this.userEmail = userEmail;
		this.userUrl = userUrl;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEpcisAddress() {
		return epcisAddress;
	}

	public void setEpcisAddress(String epcisAddress) {
		this.epcisAddress = epcisAddress;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
	
	public String getPmsAddress() {
		return pmsAddress;
	}

	public void setPmsAddress(String pmsAddress) {
		this.pmsAddress = pmsAddress;
	}

	public String getImportAddress() {
		return importAddress;
	}

	public void setImportAddress(String importAddress) {
		this.importAddress = importAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserTitle() {
		return userTitle;
	}

	public void setUserTitle(String userTitle) {
		this.userTitle = userTitle;
	}

	public String getUserTelephone() {
		return userTelephone;
	}

	public void setUserTelephone(String userTelephone) {
		this.userTelephone = userTelephone;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

	
}
