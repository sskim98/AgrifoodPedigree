package kaist.gs1.pms;

import org.springframework.data.repository.CrudRepository;
//pedigree 정보 저장을 위한 repository
//pedigree 내 sgtin으로 검색하는 api 정의
public interface RepositoryDao_Pedigree extends CrudRepository<InfoType_Pedigree, String> {
	public InfoType_Pedigree findPedigreeBySgtin(String sgtin); // findByUser �� �����ϸ� user �ʵ�� �˻�, Username ���� �����ϸ� username �ʵ�� �˻�
}
