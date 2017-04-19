package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// EPCIS 이벤트 정보 타입
@Document(collection="EPCISEvent") 	 	
public class InfoType_EPCISEvent {
	private String _id;
	private String type; // master, event
	private String lastEventRecordTime; // 최종 쿼리 시간
	private String eventTime; // 이벤트 시간
	private String recordTime; // 기록 시간
	private String epc; // epc 번호
	private String bizStep; // business 단계 : commissioning, receiving, shipping
	private String action; // ADD, OBSERVE
	private String lot; // lot 번호
	private String quantity; // 수량
	private String source_possessing_party; // 송신자 회사주소 sgln
	private String source_location; // 송신자 발송지(창고) 주소 sgln
	private String destination_owning_party; // 수신자 회사 주소 sgln
	private String destination_location; // 수신자 수신지(창고) 주소 sgln
	private String eventXml; // 이벤트 xml 원문
	
	public InfoType_EPCISEvent( String type, String lastEventRecordTime, String eventTime, String recordTime, String epc, String bizStep, String action, String lot, String quantity, String source_possessing_party, String source_location, String destination_owning_party, String destination_location, String eventXml) {
		this.type = type;
		this.lastEventRecordTime = lastEventRecordTime;
		this.eventTime = eventTime;
		this.recordTime = recordTime;
		this.epc = epc;
		this.bizStep = bizStep;
		this.action = action;
		this.lot = lot;
		this.quantity = quantity;
		this.source_possessing_party = source_possessing_party;
		this.source_location = source_location;
		this.destination_owning_party = destination_owning_party;
		this.destination_location = destination_location;
		this.eventXml = eventXml;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLastEventRecordTime() {
		return lastEventRecordTime;
	}

	public void setLastEventRecordTime(String lastEventRecordTime) {
		this.lastEventRecordTime = lastEventRecordTime;
	}

	public String getEventTime() {
		return eventTime;
	}

	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}

	public String getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}

	public String getEpc() {
		return epc;
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}

	public String getBizStep() {
		return bizStep;
	}

	public void setBizStep(String bizStep) {
		this.bizStep = bizStep;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	public String getLot() {
		return lot;
	}

	public void setLot(String lot) {
		this.lot = lot;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getSource_possessing_party() {
		return source_possessing_party;
	}

	public void setSource_possessing_party(String source_possessing_party) {
		this.source_possessing_party = source_possessing_party;
	}

	public String getSource_location() {
		return source_location;
	}

	public void setSource_location(String source_location) {
		this.source_location = source_location;
	}

	public String getDestination_owning_party() {
		return destination_owning_party;
	}

	public void setDestination_owning_party(String destination_owning_party) {
		this.destination_owning_party = destination_owning_party;
	}

	public String getDestination_location() {
		return destination_location;
	}

	public void setDestination_location(String destination_location) {
		this.destination_location = destination_location;
	}

	public String getEventXml() {
		return eventXml;
	}

	public void setEventXml(String eventXml) {
		this.eventXml = eventXml;
	}
}
