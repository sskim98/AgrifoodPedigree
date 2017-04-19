package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import kaist.gs1.pms.RepositoryDao_User;

/*
 * PMS에 저장된 EPCIS 이벤트 관리를 위한 공통 루틴
 */
//@Component
public class BaseManager_EPCISEvent extends BaseManager_Signer {

    @Resource
    protected RepositoryDao_EPCISEvent eventRepositoryDao;
    private static final Logger logger = Logger.getLogger(BaseManager_Info.class);
    private Iterable<InfoType_EPCISEvent> events = null;
    		
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