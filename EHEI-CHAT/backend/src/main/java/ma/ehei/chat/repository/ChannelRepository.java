package ma.ehei.chat.repository;

import ma.ehei.chat.model.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends MongoRepository<Channel, String> {
    Optional<Channel> findByName(String name);
    boolean existsByName(String name);
    List<Channel> findByMemberIdsContaining(String userId);
    List<Channel> findByTypeAndIsActive(String type, boolean isActive);
    List<Channel> findByParticipantIdsContaining(String userId);
}
