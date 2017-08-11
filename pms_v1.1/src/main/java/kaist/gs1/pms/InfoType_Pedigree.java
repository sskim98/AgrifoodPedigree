package kaist.gs1.pms;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// pedigree 정보 타입
@Document(collection="PedigreeInfo") 	 	
public class InfoType_Pedigree {
	
	@Id
	private String _id;
	private String sgtin; // sgln
	private String type; // initial, shipped, received, imported
	private String modifiedTime; // 최종 수정된 시각
	private String recipientAddress; // 이 pedigree를 전달한 파트너 주소
	private String validationStatus;
	private String validationDetail;
	private String xml; // xml 원문

	public InfoType_Pedigree(String _id, String sgtin, String type, String modifiedTime, String recipientAddress, String xml) {
		this._id = _id;
		this.sgtin = sgtin;
		this.type = type;
		this.modifiedTime = modifiedTime;
		this.recipientAddress = recipientAddress;
		this.xml = xml;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getSgtin() {
		return sgtin;
	}

	public void setSgtin(String sgtin) {
		this.sgtin = sgtin;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	
	public String getRecipientAddress() {
		return recipientAddress;
	}

	public void setRecipientAddress(String recipientAddress) {
		this.recipientAddress = recipientAddress;
	}

	public String getValidationStatus() {
		return validationStatus;
	}

	public void setValidationStatus(String validationStatus) {
		this.validationStatus = validationStatus;
	}

	public String getValidationDetail() {
		return validationDetail;
	}

	public void setValidationDetail(String validationDetail) {
		this.validationDetail = validationDetail;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}


}
