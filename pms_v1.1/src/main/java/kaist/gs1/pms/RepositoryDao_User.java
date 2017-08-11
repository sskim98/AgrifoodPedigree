package kaist.gs1.pms;

import org.springframework.data.repository.CrudRepository;
//사용자정보 저장을 위한 repository
//사용자 ID로 검색하는 api 정의
public interface RepositoryDao_User extends CrudRepository<InfoType_User, String> {
	public InfoType_User findUserByUserID(String userID); // findByUser �� �����ϸ� user �ʵ�� �˻�, Username ���� �����ϸ� username �ʵ�� �˻�
}
