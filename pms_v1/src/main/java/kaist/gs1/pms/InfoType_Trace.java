package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// pedigree의 이동경로 정보 타입
public class InfoType_Trace{
	private String sgtin; // pedigree sgtin
	private String company; // 이동경로 회사 이름
	private String type; // pedigree 타입
	private String eventTime; // 이벤트 시간
	private String validity; // 페디그리 유효성 검증 결과

	public InfoType_Trace(String sgtin, String company, String type, String eventTime, String validity) {
		this.sgtin = sgtin;
		this.company = company;
		this.type = type;
		this.eventTime = eventTime;
		this.validity = validity;
	}

	public String getSgtin() {
		return sgtin;
	}

	public void setSgtin(String sgtin) {
		this.sgtin = sgtin;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}
}
