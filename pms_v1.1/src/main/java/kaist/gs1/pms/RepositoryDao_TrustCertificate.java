package kaist.gs1.pms;

import org.springframework.data.repository.CrudRepository;
//Certificate 저장을 위한 repository
public interface RepositoryDao_TrustCertificate extends CrudRepository<InfoType_TrustCertificate, String> {
	public Iterable<InfoType_TrustCertificate> findTrustCertificateByHasConfidence(String HasConfidence); // findByUser �� �����ϸ� user �ʵ�� �˻�, Username ���� �����ϸ� username �ʵ�� �˻�
	public InfoType_TrustCertificate findTrustCertificateBySerialNumber(String SerialNumber); // findByUser �� �����ϸ� user �ʵ�� �˻�, Username ���� �����ϸ� username �ʵ�� �˻�
}
