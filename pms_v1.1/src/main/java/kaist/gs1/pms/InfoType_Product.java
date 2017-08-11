package kaist.gs1.pms;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// 제품 정보 타입
@Document(collection="ProductInfo") 	 	
public class InfoType_Product {
	@Id
	private String _id;
	private String name; // 제품 이름
	private String manufacturer; // 생산자
	private String productCode; // 제품 코드 gtin
	private String productCodeType; // gtin
	private String dosageForm; // 용법(의약품의 경우)
	private String strength; // 강도(의약품의 경우)
	private String containerSize; // 포장 단위
	private String lot; // lot 번호
	private String expirationDate; // 유통기한 : 30의 경우 commissioning 이벤트 발생 부터 30일 

	public InfoType_Product(String name, String manufacturer, String productCode, String productCodeType, String dosageForm, String strength, String containerSize, String lot, String expirationDate) {
		this._id = productCode;
		this.name = name;
		this.manufacturer =  manufacturer;
		this.productCode = productCode;
		this.productCodeType =  productCodeType;
		this.dosageForm = dosageForm;
		this.strength =  strength;
		this.containerSize = containerSize;
		this.lot =  lot;
		this.expirationDate =  expirationDate;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductCodeType() {
		return productCodeType;
	}

	public void setProductCodeType(String productCodeType) {
		this.productCodeType = productCodeType;
	}

	public String getDosageForm() {
		return dosageForm;
	}

	public void setDosageForm(String dosageForm) {
		this.dosageForm = dosageForm;
	}

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	public String getContainerSize() {
		return containerSize;
	}

	public void setContainerSize(String containerSize) {
		this.containerSize = containerSize;
	}

	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	
}
