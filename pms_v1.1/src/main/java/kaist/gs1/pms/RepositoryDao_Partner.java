package kaist.gs1.pms;

import org.springframework.data.repository.CrudRepository;
//파트너정보 저장을 위한 repository
//주소, 즉 https://localhost:443/import 과 같은 https pedigree 전송 URI로 검색하는  api 정의
public interface RepositoryDao_Partner extends CrudRepository<InfoType_Partner, String> {
	public InfoType_Partner findPartnerByAddressId(String addressId); // findByUser �� �����ϸ� user �ʵ�� �˻�, Username ���� �����ϸ� username �ʵ�� �˻�
}
