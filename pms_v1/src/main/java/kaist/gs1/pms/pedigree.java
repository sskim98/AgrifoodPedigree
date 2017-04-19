package kaist.gs1.pms;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
	
/******************************************************************************
 * 
 * @author sskim
 *	pedigree implementation
 *		pedigree는 shipped, received pedigree를 감싸기 위한 것으로 namespace 를 제외한 특별한 필드 없음
 */
//@JacksonXmlRootElement(localName="pedigree", namespace="urn:epcGlobal:Pedigree:xsd:1")
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
