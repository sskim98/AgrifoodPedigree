package kaist.gs1.pms;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// 파트너 정보 타입
@Document(collection="PartnerInfo") 	 	
public class InfoType_Partner {
	
	private String name; // 이름
	private String street1; // 주소 1
	private String street2; // 주소 2
	private String city; // 도시
	private String state; // 주
	private String postalCode; // 우편번호
	private String country; // 국가 코드
	@Id
	private String addressId; // sgln
	private String pmsAddress; // pedigree management server address http://localhost:8081/pms
	private String importAddress; // pedigree를 전달할 목적 주소 https://localhost:433/pms/import
	private String userName; // 담당자 이름
	private String userTitle; // 담당자 직위
	private String userTelephone; // 담당자 연락처
	private String userEmail; // 담당자 이메일
	private String userUrl; // 파트너 URL : http://itc.kaist.ac.kr
	private String privateCertificate; // 사설인증서를 이용하는 경우 사설 인증서
	
	public InfoType_Partner(String name, String street1, String street2, String city, String state, String postalCode, String country, String addressId, String pmsAddress, String importAddress, String userName, String userTitle, String userTelephone, String userEmail, String userUrl, String privateCertificate) {
		this.name = name;
		this.street1 =  street1;
		this.street2 = street2;
		this.city =  city;
		this.state = state;
		this.postalCode =  postalCode;
		this.country = country;
		this.addressId =  addressId;
		this.pmsAddress = pmsAddress;
		this.importAddress =  importAddress;
		this.userName = userName;
		this.userTitle = userTitle;
		this.userTelephone = userTelephone;
		this.userEmail = userEmail;
		this.userUrl = userUrl;
		this.privateCertificate = privateCertificate;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getStreet1() {
		return street1;
	}


	public void setStreet1(String street1) {
		this.street1 = street1;
	}


	public String getStreet2() {
		return street2;
	}


	public void setStreet2(String street2) {
		this.street2 = street2;
	}


	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getPostalCode() {
		return postalCode;
	}


	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
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


	public String getPrivateCertificate() {
		return privateCertificate;
	}


	public void setPrivateCertificate(String privateCertificate) {
		this.privateCertificate = privateCertificate;
	}


	
}
