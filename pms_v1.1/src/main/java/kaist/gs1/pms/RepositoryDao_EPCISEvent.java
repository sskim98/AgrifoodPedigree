package kaist.gs1.pms;

import org.springframework.data.repository.CrudRepository;
//fetch된 EPCIS 이벤트 저장을 위한 repository
//type: event, master.   bizStep: commissioning, shipping, receiving.   action: ADD, OBSERVE
public interface RepositoryDao_EPCISEvent extends CrudRepository<InfoType_EPCISEvent, String> {
	public Iterable<InfoType_EPCISEvent> findEPCISEventByType(String type); 
	public Iterable<InfoType_EPCISEvent> findEPCISEventByBizStep(String bizStep);
	public Iterable<InfoType_EPCISEvent> findEPCISEventByAction(String action);
}
