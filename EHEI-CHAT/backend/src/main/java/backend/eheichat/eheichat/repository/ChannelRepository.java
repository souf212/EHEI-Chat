package backend.eheichat.eheichat.repository;


import backend.eheichat.eheichat.model.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends MongoRepository<Channel, String> {
    Optional<Channel> findByName(String name);
    List<Channel> findByMemberIdsContaining(String userId);
    List<Channel> findByTypeAndIsActive(String type, boolean isActive);
    boolean existsByName(String name);
}