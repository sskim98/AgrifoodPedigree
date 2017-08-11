package kaist.gs1.pms;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

/*
 * PMS에 저장된 EPCIS 이벤트 관리를 위한 공통 루틴
 */
//@Component
public class BaseManager_EPCISEvent extends BaseManager_Signer {

    @Resource
    protected RepositoryDao_EPCISEvent eventRepositoryDao;  // EPCIS 이벤트 저장소 DAO
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);  
    private Iterable<InfoType_EPCISEvent> events = null;  // EPCIS 검색 결과 리스트
    		
    public BaseManager_EPCISEvent() {

    }
    
    // 모든 이벤트 검색
    public Iterable<InfoType_EPCISEvent> getAllEvent() {
        events = eventRepositoryDao.findEPCISEventByType("event");
        return events;
    }
    
    // 특정 bizStep을 갖는 이벤트 검색, commissioning, shipping, receiving
    public Iterable<InfoType_EPCISEvent> selectEventByBizStep(String bizStep) {
        events = eventRepositoryDao.findEPCISEventByBizStep(bizStep);
        return events;
    }
    
    // 특정 action을 갖는 이벤트 검색, ADD, OBSERVE
    public Iterable<InfoType_EPCISEvent> selectEventByAction(String action) {
        events = eventRepositoryDao.findEPCISEventByAction(action);
        return events;
    }
    
    // master 데이터 검색
    public InfoType_EPCISEvent getMasterData() {
    	events = eventRepositoryDao.findEPCISEventByType("master");
    	if(events.iterator().hasNext()) {
    		return events.iterator().next();
    	}
    	else {
    		return null;
    	}
    }
    
    // 이벤트 저장 루틴
    public void storeEvent(InfoType_EPCISEvent event) {
        eventRepositoryDao.save(event);
    }
    // 이벤트 삭제 루틴
    public boolean removeEvent(InfoType_EPCISEvent event) {
    	eventRepositoryDao.delete(event);
		return true;
    }
    
    
}