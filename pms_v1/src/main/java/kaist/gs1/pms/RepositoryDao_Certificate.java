package kaist.gs1.pms;

import org.springframework.data.repository.CrudRepository;
//Certificate 저장을 위한 repository
public interface RepositoryDao_Certificate extends CrudRepository<InfoType_Certificate, String> {
}
