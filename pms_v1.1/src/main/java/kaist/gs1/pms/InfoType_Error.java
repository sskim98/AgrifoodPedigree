package kaist.gs1.pms;

import org.springframework.data.mongodb.core.mapping.Document;

// 인증서 정보 타입
@Document(collection="ErrorInfo") 	 	
public class InfoType_Error {
	private String code;  //error 번호
	private String detail;  // 상세 사유
	
	
	public InfoType_Error( String code, String detail) {
		this.code = code;
		this.detail = detail;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getDetail() {
		return detail;
	}


	public void setDetail(String detail) {
		this.detail = detail;
	}

}
