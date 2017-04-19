package kaist.gs1.pms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import kaist.gs1.pms.shippedPedigree.AddressIdType;
import kaist.gs1.pms.shippedPedigree.AddressType;
import kaist.gs1.pms.shippedPedigree.ContactType;
import kaist.gs1.pms.shippedPedigree.DocumentInfoType;
import kaist.gs1.pms.shippedPedigree.EncodingType;
import kaist.gs1.pms.shippedPedigree.ItemInfoType;
import kaist.gs1.pms.shippedPedigree.LicenseNumber;
import kaist.gs1.pms.shippedPedigree.PartnerIdType;
import kaist.gs1.pms.shippedPedigree.PartnerInfoType;
import kaist.gs1.pms.shippedPedigree.PreviousProductInfoType;
import kaist.gs1.pms.shippedPedigree.ProductCodeType;
import kaist.gs1.pms.shippedPedigree.SignatureInfoType;
import kaist.gs1.pms.shippedPedigree.SignatureMeaningType;
import kaist.gs1.pms.shippedPedigree.TransactionIdentifierType;
import kaist.gs1.pms.shippedPedigree.TransactionIdentifierTypeType;
	

/******************************************************************************
 * 
 * @author sskim
 *	initialPedigree implementation
 *		| 
 *		|- id
 *		|- documentInfo
 *		|- previousPedigree
 *		|- itemInfo
 *		|- signatureInfo
 */
//@JacksonXmlRootElement(localName="pedigree", namespace="urn:epcGlobal:Pedigree:xsd:1")
@XmlRootElement
public class receivedPedigree {
	
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public DocumentInfoType documentInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public String previousPedigree = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public ReceivingInfoType receivingInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public SignatureInfoType signatureInfo = null;
	@JacksonXmlProperty(isAttribute = true)
	public String id = null;
	// EPCIS event를 기반으로 received Pedigree 생성
	receivedPedigree(String sgtin, InfoType_Product product, Document eventDoc, InfoType_Company companyInfo) {
		//Document eventDoc = buildDocumentfromString(eventXml);
		// sgtin에서 serial 추출
		this.id = sgtin;
		String[] array = sgtin.split("\\.");
		this.documentInfo = new DocumentInfoType();
		this.documentInfo.serialNumber = array[array.length-1];
		this.documentInfo.version = "version";
		this.previousPedigree = "previousPedigree";
		
		
		//receivingInfo 추가
		this.receivingInfo = new ReceivingInfoType();
		this.receivingInfo.dateReceived = eventDoc.getElementsByTagName("eventTime").item(0).getTextContent();
		if(product != null) {
			this.receivingInfo.itemInfo = new ItemInfoType();
			this.receivingInfo.itemInfo.lot = product.getLot();
			this.receivingInfo.itemInfo.expirationDate = product.getExpirationDate();
			this.receivingInfo.itemInfo.quantity = "1";
			this.receivingInfo.itemInfo.itemSerialNumber = this.documentInfo.serialNumber;
		}
		
		//signatureInfo 추가
		this.signatureInfo = new SignatureInfoType();
		this.signatureInfo.signerInfo = new ContactType();
		this.signatureInfo.signerInfo.name = companyInfo.getUserName();
		this.signatureInfo.signerInfo.title = companyInfo.getUserTitle();
		this.signatureInfo.signerInfo.telephone = companyInfo.getUserTelephone();
		this.signatureInfo.signerInfo.email = companyInfo.getUserEmail();
		this.signatureInfo.signerInfo.url = companyInfo.getUserUrl();
		this.signatureInfo.signatureDate = getSignatureDate();
		this.signatureInfo.signatureMeaning = SignatureMeaningType.ReceivedAndAuthenticated.name();
		
	}
	
	//@XmlRootElement(name = "LicenseNumber")
	public class LicenseNumber {
		String licenseNumber;
		@JacksonXmlProperty(isAttribute = true)
		String state;
		String agency;
	}
	//@XmlRootElement(name = "DocumentInfoType")
	public class DocumentInfoType {
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		String serialNumber;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		String version;
		public String getSerialNumber() {
			return serialNumber;
		}
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
	}
	//@XmlRootElement(name = "ForeignDataType")
	public class ForeignDataType {
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}
	//@XmlRootElement(name = "ItemInfoType")
	public  class ItemInfoType {
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String lot;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String expirationDate;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String quantity;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String itemSerialNumber;
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
		public String getQuantity() {
			return quantity;
		}
		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
		public String getItemSerialNumber() {
			return itemSerialNumber;
		}
		public void setItemSerialNumber(String itemSerialNumber) {
			this.itemSerialNumber = itemSerialNumber;
		}
	}

	//@XmlRootElement(name = "ProductInfoType")
	public class ProductInfoType {
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String drugName;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String manufacturer;

		//@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		//ProductCodeType productCode;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String dosageForm; // disuse for agrifood
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String strength; // disuse for agrifood
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String containerSize;

		public String getDrugName() {
			return drugName;
		}

		public void setDrugName(String drugName) {
			this.drugName = drugName;
		}

		public String getManufacturer() {
			return manufacturer;
		}

		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
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


	}
	//@XmlRootElement(name = "TransactionInfoType")
	public class TransactionInfoType {
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private PartnerInfoType senderInfo;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private PartnerInfoType recipientInfo;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private TransactionIdentifierType transactionIdentifier;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private TransactionIdentifierType altRransactionIdentifier;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String transactionType;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		private String transactionDate;
		public PartnerInfoType getSenderInfo() {
			return senderInfo;
		}
		public void setSenderInfo(PartnerInfoType senderInfo) {
			this.senderInfo = senderInfo;
		}
		public PartnerInfoType getRecipientInfo() {
			return recipientInfo;
		}
		public void setRecipientInfo(PartnerInfoType recipientInfo) {
			this.recipientInfo = recipientInfo;
		}
		public TransactionIdentifierType getTransactionIdentifier() {
			return transactionIdentifier;
		}
		public void setTransactionIdentifier(TransactionIdentifierType transactionIdentifier) {
			this.transactionIdentifier = transactionIdentifier;
		}
		public TransactionIdentifierType getAltRransactionIdentifier() {
			return altRransactionIdentifier;
		}
		public void setAltRransactionIdentifier(TransactionIdentifierType altRransactionIdentifier) {
			this.altRransactionIdentifier = altRransactionIdentifier;
		}
		public String getTransactionType() {
			return transactionType;
		}
		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}
		public String getTransactionDate() {
			return transactionDate;
		}
		public void setTransactionDate(String transactionDate) {
			this.transactionDate = transactionDate;
		}

	}
	//@XmlRootElement(name = "PartnerInfoType")
	public class PartnerInfoType {
		AddressType businessAddress;
		AddressType shippingAddress;
		PartnerIdType partnerId;
		LicenseNumber licenseNumber;
		ContactType contactInfo;
		public AddressType getBusinessAddress() {
			return businessAddress;
		}
		public void setBusinessAddress(AddressType businessAddress) {
			this.businessAddress = businessAddress;
		}
		public AddressType getShippingAddress() {
			return shippingAddress;
		}
		public void setShippingAddress(AddressType shippingAddress) {
			this.shippingAddress = shippingAddress;
		}
		public PartnerIdType getPartnerId() {
			return partnerId;
		}
		public void setPartnerId(PartnerIdType partnerId) {
			this.partnerId = partnerId;
		}
		public LicenseNumber getLicenseNumber() {
			return licenseNumber;
		}
		public void setLicenseNumber(LicenseNumber licenseNumber) {
			this.licenseNumber = licenseNumber;
		}
		public ContactType getContactInfo() {
			return contactInfo;
		}
		public void setContactInfo(ContactType contactInfo) {
			this.contactInfo = contactInfo;
		}

	}
	//@XmlRootElement(name = "AddressType")
	public class AddressType {
		String businessName;
		String street1;
		String street2;
		String city;
		String stateOrRegion;
		String postalCode;
		String country;
		AddressIdType AddressId;
		public String getBusinessName() {
			return businessName;
		}
		public void setBusinessName(String businessName) {
			this.businessName = businessName;
		}
		public String getStreet1() {
			return street1;
		}
		public void setStreet1(String street1) {
			this.street1 = street1;
		}
		public String getStreet2() {
			return street2;
		}
		public void setStreet2(String street2) {
			this.street2 = street2;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getStateOrRegion() {
			return stateOrRegion;
		}
		public void setStateOrRegion(String stateOrRegion) {
			this.stateOrRegion = stateOrRegion;
		}
		public String getPostalCode() {
			return postalCode;
		}
		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public AddressIdType getAddressId() {
			return AddressId;
		}
		public void setAddressId(AddressIdType addressId) {
			AddressId = addressId;
		}


	}
	//@XmlRootElement(name = "TransactionIdentifierType")
	public class TransactionIdentifierType {
		String identifier;
		TransactionIdentifierTypeType identifierType;
		public String getIdentifier() {
			return identifier;
		}
		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}
		public TransactionIdentifierTypeType getIdentifierType() {
			return identifierType;
		}
		public void setIdentifierType(TransactionIdentifierTypeType identifierType) {
			this.identifierType = identifierType;
		}


	}
	//@XmlRootElement(name = "ContactType")
	public class ContactType {
		String name;
		String title;
		String telephone;
		String email;
		String url;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}


	}
	//@XmlRootElement(name = "ReceivingInfoType")
	public class ReceivingInfoType {
		String dateReceived;//xs:date
		ItemInfoType itemInfo;
		public String getDateReceived() {
			return dateReceived;
		}
		public void setDateReceived(String dateReceived) {
			this.dateReceived = dateReceived;
		}
		public ItemInfoType getItemInfo() {
			return itemInfo;
		}
		public void setItemInfo(ItemInfoType itemInfo) {
			this.itemInfo = itemInfo;
		}
	}
	//@XmlRootElement(name = "PreviousProductType")
	public class PreviousProductType {
		String serialNumber;
		PreviousProductInfoType previousProductInfo;
		ItemInfoType itemInfo;
		ContactType contactInfo;
	}
	//@XmlRootElement(name = "SignatureInfoType")
	public class SignatureInfoType {
		ContactType signerInfo;
		String signatureDate;
		String signatureMeaning;
		public ContactType getSignerInfo() {
			return signerInfo;
		}
		public void setSignerInfo(ContactType signerInfo) {
			this.signerInfo = signerInfo;
		}
		public String getSignatureDate() {
			return signatureDate;
		}
		public void setSignatureDate(String signatureDate) {
			this.signatureDate = signatureDate;
		}
		public String getSignatureMeaning() {
			return signatureMeaning;
		}
		public void setSignatureMeaning(String signatureMeaning) {
			this.signatureMeaning = signatureMeaning;
		}


	}
	//@XmlRootElement(name = "PreviousProductInfoType")
	public class PreviousProductInfoType {
		String drugName;
		String manufacturer;
		ProductCodeType productCode;
	}
	//@XmlRootElement(name = "ProductCodeType")
	public class ProductCodeType {
		String type;
	}
	//@XmlRootElement(name = "PartnerIdType")
	public class PartnerIdType {
		String partnerId;
		@JacksonXmlProperty(isAttribute = true)
		String type;
		public String getPartnerId() {
			return partnerId;
		}
		public void setPartnerId(String partnerId) {
			this.partnerId = partnerId;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}


	}
	//@XmlRootElement(name = "AddressIdType")
	public class AddressIdType {
		String addressId;
		@JacksonXmlProperty(isAttribute = true)
		String type;
		public String getAddressId() {
			return addressId;
		}
		public void setAddressId(String addressId) {
			this.addressId = addressId;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}

	}
	//@XmlRootElement(name = "TransactionIdentifierTypeType")
	public enum TransactionIdentifierTypeType {
		InvoiceNumber, PurchaseOrderNumber, ShippingNumber, ReturnAuthorizationNumber, Other
	}
	//@XmlRootElement(name = "SignatureMeaningType")
	public enum SignatureMeaningType {
		Certified, Received, Authenticated, ReceivedAndAuthenticated
	}
	//@XmlRootElement(name = "TransactionTypeType")
	public enum TransactionTypeType {
		Sale, Return, Transfer, Other
	}
	//@XmlRootElement(name = "ProductCodeValueTypeType")
	public enum ProductCodeValueTypeType {
		NDC442, NDC532, NDC541, NDC542, GTIN
	}
	//@XmlRootElement(name = "EncodingType")
	public enum EncodingType {
		base64binary
	}
	//@XmlRootElement(name = "PartnerIdValueTypeType")
	public enum PartnerIdValueTypeType {
		GLN
	}
	//@XmlRootElement(name = "AddressIdValueTypeType")
	public enum AddressIdValueTypeType {
		GLN
	}
	//@XmlRootElement(name = "AltPedigree")
	public class AltPedigree {
		@JacksonXmlProperty(isAttribute = true)
		boolean wasRepackaged;
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}
	//@XmlRootElement(name = "Attachment")
	public class Attachment {
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}
		public DocumentInfoType getDocumentInfo() {
			return documentInfo;
		}
		public void setDocumentInfo(DocumentInfoType documentInfo) {
			this.documentInfo = documentInfo;
		}
		public String getPreviousPedigree() {
			return previousPedigree;
		}
		public void setPreviousPedigree(String previousPedigree) {
			this.previousPedigree = previousPedigree;
		}
		public ReceivingInfoType getReceivingInfo() {
			return receivingInfo;
		}
		public void setReceivingInfo(ReceivingInfoType receivingInfo) {
			this.receivingInfo = receivingInfo;
		}
		public SignatureInfoType getSignatureInfo() {
			return signatureInfo;
		}
		public void setSignatureInfo(SignatureInfoType signatureInfo) {
			this.signatureInfo = signatureInfo;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		// 서명일자를 얻는 루틴
		private String getSignatureDate() {
			long time = System.currentTimeMillis(); 
			SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
			return dayTime.format(new Date(time));
		}
}



