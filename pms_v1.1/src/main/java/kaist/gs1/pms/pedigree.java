package kaist.gs1.pms;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
	
/******************************************************************************
 * 
 * @author sskim
 *	pedigree implementation
 *		pedigree는 shipped, received pedigree를 감싸기 위한 것으로 namespace 를 제외한 특별한 필드 없음
 */

public class pedigree {
	@JacksonXmlProperty(isAttribute = true)
	private String namespace;
	
	pedigree() {
		this.namespace = "urn:epcGlobal:Pedigree:xsd:1";
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
}
