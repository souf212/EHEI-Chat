package backend.eheichat.eheichat.repository;


import backend.eheichat.eheichat.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChannelIdOrderByTimestampDesc(String channelId, Pageable pageable);
    List<Message> findByChannelIdOrderByTimestampDesc(String channelId);
    long countByChannelId(String channelId);
}
