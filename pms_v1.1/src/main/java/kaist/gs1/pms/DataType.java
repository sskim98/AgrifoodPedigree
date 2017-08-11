package kaist.gs1.pms;
	
// pedigree 종류별 공통 데이터 타입들을 정의하려 했던 모듈, 현재 사용하지 않음 
//@JacksonXmlRootElement(localName="pedigree", namespace="urn:epcGlobal:Pedigree:xsd:1")
public class DataType extends BaseManager_Signer {
	/*
	@XmlRootElement(name = "LicenseNumber")
	public class LicenseNumber {
		String licenseNumber;
		@JacksonXmlProperty(isAttribute = true)
		String state;
		String agency;
	}
	@XmlRootElement(name = "DocumentInfoType")
	public class DocumentInfoType {
		String serialNumber;
		String version;
	}
	@XmlRootElement(name = "ForeignDataType")
	public class ForeignDataType {
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}
	@XmlRootElement(name = "ItemInfoType")
	public static class ItemInfoType {
		public String lot;
		public String expirationDate;
		public String quantity;
		public String itemSerialNumber;	
	}

	@XmlRootElement(name = "ProductInfoType")
	public static class ProductInfoType {
		
		String drugName;
		
		String manufacturer;
		
		//@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		//ProductCodeType productCode;
		
		String dosageForm; // disuse for agrifood
		
		String strength; // disuse for agrifood
		
		String containerSize;
	}
	@XmlRootElement(name = "TransactionInfoType")
	public static class TransactionInfoType {
		PartnerInfoType senderInfo;
		PartnerInfoType recipientInfo;
		TransactionIdentifierType transactionIdentifier;
		TransactionIdentifierType altRransactionIdentifier;
		TransactionTypeType transactionType;
		String transactionDate;
	}
	@XmlRootElement(name = "PartnerInfoType")
	public class PartnerInfoType {
		AddressType businessAddress;
		AddressType shippingAddress;
		PartnerIdType partnerId;
		LicenseNumber licenseNumber;
		ContactType contactInfo;
	}
	@XmlRootElement(name = "AddressType")
	public class AddressType {
		String businessName;
		String street1;
		String street2;
		String city;
		String stateOrRegion;
		String postalCode;
		String country;
		AddressIdType AddressId;
	}
	@XmlRootElement(name = "TransactionIdentifierType")
	public class TransactionIdentifierType {
		String identifier;
		TransactionIdentifierTypeType identifierType;
	}
	@XmlRootElement(name = "ContactType")
	public class ContactType {
		String name;
		String title;
		String telephone;
		String email;
		String url;
	}
	@XmlRootElement(name = "ReceivingInfoType")
	public static class ReceivingInfoType {
		String dateReceived;//xs:date
		ItemInfoType itemInfo;
	}
	@XmlRootElement(name = "PreviousProductType")
	public class PreviousProductType {
		String serialNumber;
		PreviousProductInfoType previousProductInfo;
		ItemInfoType itemInfo;
		ContactType contactInfo;
	}
	@XmlRootElement(name = "SignatureInfoType")
	public class SignatureInfoType {
		ContactType signerInfo;
		String signatureDate;
		SignatureMeaningType signatureMeaning;
	}
	@XmlRootElement(name = "PreviousProductInfoType")
	public class PreviousProductInfoType {
		String drugName;
		String manufacturer;
		ProductCodeType productCode;
	}
	@XmlRootElement(name = "ProductCodeType")
	public class ProductCodeType {
		String type;
	}
	@XmlRootElement(name = "PartnerIdType")
	public class PartnerIdType {
		String partnerId;
		@JacksonXmlProperty(isAttribute = true)
		String type;
	}
	@XmlRootElement(name = "AddressIdType")
	public class AddressIdType {
		String addressId;
		@JacksonXmlProperty(isAttribute = true)
		String type;
	}
	@XmlRootElement(name = "TransactionIdentifierTypeType")
	public enum TransactionIdentifierTypeType {
		InvoiceNumber, PurchaseOrderNumber, ShippingNumber, ReturnAuthorizationNumber, Other
	}
	@XmlRootElement(name = "SignatureMeaningType")
	public enum SignatureMeaningType {
		Certified, Received, Authenticated, ReceivedAndAuthenticated
	}
	@XmlRootElement(name = "TransactionTypeType")
	public enum TransactionTypeType {
		Sale, Return, Transfer, Other
	}
	@XmlRootElement(name = "ProductCodeValueTypeType")
	public enum ProductCodeValueTypeType {
		NDC442, NDC532, NDC541, NDC542, GTIN
	}
	@XmlRootElement(name = "EncodingType")
	public enum EncodingType {
		base64binary
	}
	@XmlRootElement(name = "PartnerIdValueTypeType")
	public enum PartnerIdValueTypeType {
		GLN
	}
	@XmlRootElement(name = "AddressIdValueTypeType")
	public enum AddressIdValueTypeType {
		GLN
	}
	@XmlRootElement(name = "AltPedigree")
	public static class AltPedigree {
		@JacksonXmlProperty(isAttribute = true)
		boolean wasRepackaged;
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}
	@XmlRootElement(name = "Attachment")
	public static class Attachment {
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}
	*/
}
