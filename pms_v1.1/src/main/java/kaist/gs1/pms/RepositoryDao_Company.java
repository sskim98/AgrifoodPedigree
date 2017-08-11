package kaist.gs1.pms;

import org.springframework.data.repository.CrudRepository;
//회사정보 저장을 위한 repository
//도메인, 즉 itc.kaist.ac.kr 과 같은 도메인으로 검색하는 api 정의
public interface RepositoryDao_Company extends CrudRepository<InfoType_Company, String> {
	public InfoType_Company findCompanyByDomain(String domain); // findByUser �� �����ϸ� user �ʵ�� �˻�, Username ���� �����ϸ� username �ʵ�� �˻�
}
