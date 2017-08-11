package kaist.gs1.pms;

import org.springframework.data.repository.CrudRepository;
//제품정보 저장을 위한 repository
// 원래 GS1 source를 통해 제품정보를 얻어와야 하나 우선 내부적으로 입력한 후 이용하는 형태로 구현
//상품의 code로 검색하는 api 정의
public interface RepositoryDao_Product extends CrudRepository<InfoType_Product, String> {
	public InfoType_Product findProductByProductCode(String productCode); // findByUser �� �����ϸ� user �ʵ�� �˻�, Username ���� �����ϸ� username �ʵ�� �˻�
}
