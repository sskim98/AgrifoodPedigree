package kaist.gs1.pms;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.w3c.dom.*;

/******************************************************************************
 * 
 * @author sskim
 *	initialPedigree implementation
 *		| 
 *		|- id
 *		|- documentInfo
 *		|- previousPedigree
 *		|- itemInfo
 *		|- transactionInfo
 *		|- signatureInfo
 */

@XmlRootElement
public class shippedPedigree {

	@JacksonXmlProperty(isAttribute = true)
	public String id = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public DocumentInfoType documentInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public String previousPedigree = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public ItemInfoType itemInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public TransactionInfoType transactionInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	public SignatureInfoType signatureInfo = null;

	// EPCIS event로부터 shipped pedigree를 생성하는 루틴
	shippedPedigree(String sgtin, InfoType_Product product, Document eventDoc, InfoType_Company companyInfo, InfoType_Partner partnerInfo, InfoType_Certificate pmsCertificate) {
		//Document eventDoc = buildDocumentfromString(eventXml);
		// sgtin에서 serial 추출
		this.id = sgtin;
		String[] array = sgtin.split("\\.");
		this.documentInfo = new DocumentInfoType();
		this.documentInfo.serialNumber = array[array.length-1];
		this.documentInfo.version = "version";

		if(pmsCertificate.getCertificateType().equals("Private")) {
			this.documentInfo.privateDocumentInfo = new privateDocumentInfoType();
			this.documentInfo.privateDocumentInfo.issuerAddress = new ContactType();
			this.documentInfo.privateDocumentInfo.issuerAddress.name = companyInfo.getUserName();
			this.documentInfo.privateDocumentInfo.issuerAddress.title = companyInfo.getUserTitle();
			this.documentInfo.privateDocumentInfo.issuerAddress.telephone = companyInfo.getUserTelephone();
			this.documentInfo.privateDocumentInfo.issuerAddress.email = companyInfo.getUserEmail();
			this.documentInfo.privateDocumentInfo.issuerAddress.url = companyInfo.getUserUrl();
			this.documentInfo.privateDocumentInfo.privateTrustCertificate = pmsCertificate.getPrivateRootCertificateString().replaceAll("-----BEGIN CERTIFICATE-----\n", "").replaceAll("-----END CERTIFICATE-----", "").replaceAll("\n", "");
		}

		this.previousPedigree = "previousPedigree";
		// itemInfo 추가
		if(product != null) {
			this.itemInfo = new ItemInfoType();
			this.itemInfo.lot = product.getLot();
			this.itemInfo.expirationDate = product.getExpirationDate();
			this.itemInfo.quantity = "1";
			this.itemInfo.itemSerialNumber = this.documentInfo.serialNumber;
		}

		//transactionInfo : sender 추가
		this.transactionInfo = new TransactionInfoType();
		this.transactionInfo.senderInfo = new PartnerInfoType();
		this.transactionInfo.senderInfo.businessAddress = new AddressType();
		this.transactionInfo.senderInfo.businessAddress.businessName = companyInfo.getName();
		this.transactionInfo.senderInfo.businessAddress.street1 = companyInfo.getStreet();
		this.transactionInfo.senderInfo.businessAddress.street2 = companyInfo.getLocality();
		this.transactionInfo.senderInfo.businessAddress.city = companyInfo.getProvince();
		this.transactionInfo.senderInfo.businessAddress.stateOrRegion = companyInfo.getProvince();
		this.transactionInfo.senderInfo.businessAddress.postalCode = companyInfo.getPostalCode();
		this.transactionInfo.senderInfo.businessAddress.country = companyInfo.getCountry();
		this.transactionInfo.senderInfo.businessAddress.AddressId = new AddressIdType();
		this.transactionInfo.senderInfo.businessAddress.AddressId.addressId = companyInfo.getAddressId();
		this.transactionInfo.senderInfo.businessAddress.AddressId.type = AddressIdValueTypeType.GLN.name();
		this.transactionInfo.senderInfo.shippingAddress = this.transactionInfo.senderInfo.businessAddress;
		this.transactionInfo.senderInfo.partnerId = new PartnerIdType();
		this.transactionInfo.senderInfo.partnerId.partnerId = companyInfo.getAddressId();
		this.transactionInfo.senderInfo.partnerId.type = PartnerIdValueTypeType.GLN.name();
		this.transactionInfo.senderInfo.contactInfo = new ContactType();
		this.transactionInfo.senderInfo.contactInfo.name = companyInfo.getUserName();
		this.transactionInfo.senderInfo.contactInfo.title = companyInfo.getUserTitle();
		this.transactionInfo.senderInfo.contactInfo.telephone = companyInfo.getUserTelephone();
		this.transactionInfo.senderInfo.contactInfo.email = companyInfo.getUserEmail();
		this.transactionInfo.senderInfo.contactInfo.url = companyInfo.getUserUrl();
		//transactionInfo : recipient 추가
		this.transactionInfo.recipientInfo = new PartnerInfoType();
		this.transactionInfo.recipientInfo.businessAddress = new AddressType();
		this.transactionInfo.recipientInfo.businessAddress.businessName = partnerInfo.getName();
		this.transactionInfo.recipientInfo.businessAddress.street1 = partnerInfo.getStreet1();
		this.transactionInfo.recipientInfo.businessAddress.street2 = partnerInfo.getStreet2();
		this.transactionInfo.recipientInfo.businessAddress.city = partnerInfo.getCity();
		this.transactionInfo.recipientInfo.businessAddress.stateOrRegion = partnerInfo.getState();
		this.transactionInfo.recipientInfo.businessAddress.postalCode = partnerInfo.getPostalCode();
		this.transactionInfo.recipientInfo.businessAddress.country = partnerInfo.getCountry();
		this.transactionInfo.recipientInfo.businessAddress.AddressId = new AddressIdType();
		this.transactionInfo.recipientInfo.businessAddress.AddressId.addressId = partnerInfo.getAddressId();
		this.transactionInfo.recipientInfo.businessAddress.AddressId.type = AddressIdValueTypeType.GLN.name();
		this.transactionInfo.recipientInfo.shippingAddress = this.transactionInfo.recipientInfo.businessAddress;
		this.transactionInfo.recipientInfo.partnerId = new PartnerIdType();
		this.transactionInfo.recipientInfo.partnerId.partnerId = partnerInfo.getAddressId();
		this.transactionInfo.recipientInfo.partnerId.type = PartnerIdValueTypeType.GLN.name();
		this.transactionInfo.recipientInfo.contactInfo = new ContactType();
		this.transactionInfo.recipientInfo.contactInfo.name = partnerInfo.getUserName();
		this.transactionInfo.recipientInfo.contactInfo.name = partnerInfo.getUserTitle();
		this.transactionInfo.recipientInfo.contactInfo.name = partnerInfo.getUserTelephone();
		this.transactionInfo.recipientInfo.contactInfo.name = partnerInfo.getUserEmail();
		this.transactionInfo.recipientInfo.contactInfo.name = partnerInfo.getUserUrl();

		//transactionInfo : transactionType 추가
		this.transactionInfo.transactionType = TransactionTypeType.Sale.name();
		this.transactionInfo.transactionDate = eventDoc.getDocumentElement().getElementsByTagName("eventTime").item(0).getTextContent();

		//signatureInfo 추가
		this.signatureInfo = new SignatureInfoType();
		this.signatureInfo.signerInfo = new ContactType();
		this.signatureInfo.signerInfo.name = companyInfo.getUserName();
		this.signatureInfo.signerInfo.title = companyInfo.getUserTitle();
		this.signatureInfo.signerInfo.telephone = companyInfo.getUserTelephone();
		this.signatureInfo.signerInfo.email = companyInfo.getUserEmail();
		this.signatureInfo.signerInfo.url = companyInfo.getUserUrl();
		this.signatureInfo.signatureDate = getSignatureDate();
		this.signatureInfo.signatureMeaning = SignatureMeaningType.Certified.name();
	}


	public class LicenseNumber {
		String licenseNumber;
		@JacksonXmlProperty(isAttribute = true)
		String state;
		String agency;
	}

	public class DocumentInfoType {
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		String serialNumber;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		String version;
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		privateDocumentInfoType privateDocumentInfo;
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
		public privateDocumentInfoType getPrivateDocumentInfo() {
			return privateDocumentInfo;
		}
		public void setPrivateDocumentInfo(privateDocumentInfoType privateDocumentInfo) {
			this.privateDocumentInfo = privateDocumentInfo;
		}
	}
	public class privateDocumentInfoType {
		ContactType issuerAddress;
		String privateTrustCertificate;
		public ContactType getIssuerAddress() {
			return issuerAddress;
		}
		public void setIssuerAddress(ContactType issuerAddress) {
			this.issuerAddress = issuerAddress;
		}
		public String getPrivateTrustCertificate() {
			return privateTrustCertificate;
		}
		public void setPrivateTrustCertificate(String privateTrustCertificate) {
			this.privateTrustCertificate = privateTrustCertificate;
		}
	}

	public class ForeignDataType {
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}

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

	public class ReceivingInfoType {
		String dateReceived;//xs:date
		ItemInfoType itemInfo;
	}

	public class PreviousProductType {
		String serialNumber;
		PreviousProductInfoType previousProductInfo;
		ItemInfoType itemInfo;
		ContactType contactInfo;
	}

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

	public class PreviousProductInfoType {
		String drugName;
		String manufacturer;
		ProductCodeType productCode;
	}

	public class ProductCodeType {
		String type;
	}

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

	public enum TransactionIdentifierTypeType {
		InvoiceNumber, PurchaseOrderNumber, ShippingNumber, ReturnAuthorizationNumber, Other
	}

	public enum SignatureMeaningType {
		Certified, Received, Authenticated, ReceivedAndAuthenticated
	}

	public enum TransactionTypeType {
		Sale, Return, Transfer, Other
	}

	public enum ProductCodeValueTypeType {
		NDC442, NDC532, NDC541, NDC542, GTIN
	}

	public enum EncodingType {
		base64binary
	}

	public enum PartnerIdValueTypeType {
		GLN
	}

	public enum AddressIdValueTypeType {
		GLN
	}

	public class AltPedigree {
		@JacksonXmlProperty(isAttribute = true)
		boolean wasRepackaged;
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}

	public class Attachment {
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public ItemInfoType getItemInfo() {
		return itemInfo;
	}
	public void setItemInfo(ItemInfoType itemInfo) {
		this.itemInfo = itemInfo;
	}
	public TransactionInfoType getTransactionInfo() {
		return transactionInfo;
	}
	public void setTransactionInfo(TransactionInfoType transactionInfo) {
		this.transactionInfo = transactionInfo;
	}
	public SignatureInfoType getSignatureInfo() {
		return signatureInfo;
	}
	public void setSignatureInfo(SignatureInfoType signatureInfo) {
		this.signatureInfo = signatureInfo;
	}
	// 서명일을 얻기 위한 루틴
	private String getSignatureDate() {
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dayTime.format(new Date(time));
	}

}



