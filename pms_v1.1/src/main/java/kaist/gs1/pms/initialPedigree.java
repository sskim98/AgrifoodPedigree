package kaist.gs1.pms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlElement;
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
 *		|- serialNumber
 *		|- productInfo
 *		|- itemInfo
 *		|- transactionInfo 
 *		|- receivingInfo
 *		|- altPedigree
 *		|- attachment
 */
@XmlRootElement
public class initialPedigree {
	@JacksonXmlProperty(isAttribute = true)
	private String id = null;
	private String serialNumber = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // object가 null이 아닌 경우에만 xml로 변경시 node 출력하는 옵션
	private ProductInfoType productInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
	private ItemInfoType itemInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private TransactionInfoType transactionInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private ReceivingInfoType receivingInfo = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private AltPedigree altPedigree = null;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Attachment attachment = null;
	
	// initial pedigree를 직접 생성하기 위한 루틴, 현재는 EPCIS event를 받아서 생성
	public initialPedigree(String gtin, String productName, String manufacturer, String productCode, String containerSize, String lot, String expirationDate, String quantity, String itemSerialNumber) {
		// serialNumber
		this.id = "initial"+gtin;
		this.serialNumber = gtin;
		// productInfo
		this.productInfo = new ProductInfoType();
		this.productInfo.drugName = productName;
		this.productInfo.manufacturer = manufacturer;
		this.productInfo.productCode = new ProductCodeType();
		this.productInfo.productCode.code = productCode;
		this.productInfo.productCode.type = "gtin";
		this.productInfo.dosageForm = "";
		this.productInfo.strength = "";
		this.productInfo.containerSize = containerSize;
		// itemInfo
		this.itemInfo = new ItemInfoType();
		this.itemInfo.lot = lot;
		this.itemInfo.expirationDate = expirationDate;
		this.itemInfo.quantity = quantity;
		this.itemInfo.itemSerialNumber = itemSerialNumber;
		
	}
	// initial pedigree 생성을 EPCIS event를 받아서 생성
	public initialPedigree(String sgtin, InfoType_Product product, Document xmlDoc) {
		// serialNumber 추출
		this.id = sgtin;
		String[] array = sgtin.replaceAll(".*:", "").split("\\.");
		this.serialNumber = array[array.length-1];
		
		//productInfo 추가
		if(product != null) {
			this.productInfo = new ProductInfoType();
			this.productInfo.drugName = product.getName();
			this.productInfo.manufacturer = product.getManufacturer();
			this.productInfo.productCode = new ProductCodeType();
			this.productInfo.productCode.code = product.getProductCode();
			this.productInfo.productCode.type = product.getProductCodeType();
			this.productInfo.dosageForm = product.getDosageForm();
			this.productInfo.strength = product.getStrength();
			this.productInfo.containerSize = product.getContainerSize();
			// itemInfo 추가
			this.itemInfo = new ItemInfoType();
			this.itemInfo.lot = product.getLot();
			this.itemInfo.expirationDate = getExpirationDate(product.getExpirationDate());
			this.itemInfo.quantity = "1";
			this.itemInfo.itemSerialNumber = serialNumber;
		}
	}

	public class LicenseNumber {
		String licenseNumber;
		@JacksonXmlProperty(isAttribute = true)
		String state;
		String agency;
	}

	public class DocumentInfoType {
		String serialNumber;
		String version;
	}

	public class ForeignDataType {
		String serialNumbner;
		String mimeType;
		EncodingType encoding;
		String data;
	}

	public  class ItemInfoType {
		private String lot;
		private String expirationDate;
		private String quantity;
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
		
		private String drugName;
		
		private String manufacturer;
		
		@JsonInclude(JsonInclude.Include.NON_NULL) // serialization without null objects
		ProductCodeType productCode;
		
		private String dosageForm; // disuse for agrifood
		
		private String strength; // disuse for agrifood
		
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

		public ProductCodeType getProductCode() {
			return productCode;
		}

		public void setProductCode(ProductCodeType productCode) {
			this.productCode = productCode;
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
		PartnerInfoType senderInfo;
		PartnerInfoType recipientInfo;
		TransactionIdentifierType transactionIdentifier;
		TransactionIdentifierType altTransactionIdentifier;
		TransactionTypeType transactionType;
		String transactionDate;
		
	}

	public class PartnerInfoType {
		AddressType businessAddress;
		AddressType shippingAddress;
		PartnerIdType partnerId;
		LicenseNumber licenseNumber;
		ContactType contactInfo;
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
	}

	public class TransactionIdentifierType {
		String identifier;
		TransactionIdentifierTypeType identifierType;
	}

	public class ContactType {
		String name;
		String title;
		String telephone;
		String email;
		String url;
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
		SignatureMeaningType signatureMeaning;
	}

	public class PreviousProductInfoType {
		String drugName;
		String manufacturer;
		ProductCodeType productCode;
	}

	public class ProductCodeType {
		String code;
		@JacksonXmlProperty(isAttribute = true)
		String type;
		
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
	}

	public class PartnerIdType {
		String partnerId;
		@JacksonXmlProperty(isAttribute = true)
		String type;
	}

	public class AddressIdType {
		String addressId;
		@JacksonXmlProperty(isAttribute = true)
		String type;
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
		NDC442, NDC532, NDC541, NDC542, GTIN, SGTIN
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
	@XmlElement
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	@XmlElement
	public ProductInfoType getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(ProductInfoType productInfo) {
		this.productInfo = productInfo;
	}
	@XmlElement
	public ItemInfoType getItemInfo() {
		return itemInfo;
	}

	public void setItemInfo(ItemInfoType itemInfo) {
		this.itemInfo = itemInfo;
	}
	@XmlElement
	public TransactionInfoType getTransactionInfo() {
		return transactionInfo;
	}

	public void setTransactionInfo(TransactionInfoType transactionInfo) {
		this.transactionInfo = transactionInfo;
	}
	@XmlElement
	public ReceivingInfoType getReceivingInfo() {
		return receivingInfo;
	}

	public void setReceivingInfo(ReceivingInfoType receivingInfo) {
		this.receivingInfo = receivingInfo;
	}
	@XmlElement
	public AltPedigree getAltPedigree() {
		return altPedigree;
	}

	public void setAltPedigree(AltPedigree altPedigree) {
		this.altPedigree = altPedigree;
	}
	@XmlElement
	public Attachment getAttachment() {
		return attachment;
	}

	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	// 제품 유효기간 계산을 위한 루틴
	private String getExpirationDate(String expirationDate) {
		// 제품 정보는 제품당 gtin마다 최대 유효기간이 일단위(day)로 정의되어 있음
		// 유효기간을 initial pedigree 생성 시간으로부터 최대 유통기한 정보를 추가하여 생성
		long time = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(Integer.parseInt(expirationDate)); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dayTime.format(new Date(time));
	}
}
