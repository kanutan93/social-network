package ru.hl.socialnetwork.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.hl.socialnetwork.kafka.payload.PostPayload;
import ru.hl.socialnetwork.model.dto.response.PostResponseDto;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.hl.socialnetwork.kafka.payload.PostPayload.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCreatedKafkaListener {

  private static final String POSTS_FEED_CACHE = "postsFeedCache";

  private final CacheManager cacheManager;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "${kafka.topic}")
  @SneakyThrows
  public void consume(ConsumerRecord<String, String> record) {
    String payload = record.value();
    log.info("New message received: {}", payload);

    PostPayload postPayload = objectMapper.readValue(payload, PostPayload.class);

    Integer userId = postPayload.getReceiverUserId();
    Action action = postPayload.getAction();
    PostResponseDto messagePost = postPayload.getPostResponseDto();

    Cache postsFeedCache = cacheManager.getCache(POSTS_FEED_CACHE);
    if (postsFeedCache != null) {
      List postsFeedCacheValue = postsFeedCache.get(userId, List.class);

      if (postsFeedCacheValue != null) {
        LinkedList<PostResponseDto> postsFeed = new LinkedList<>(postsFeedCacheValue);
        switch (action) {
            case CREATE:
                postsFeed.addFirst(messagePost);
                break;
            case UPDATE:
                postsFeed = postsFeed.stream()
                    .filter(post -> !post.getId().equals(messagePost.getId()))
                    .collect(Collectors.toCollection(LinkedList::new));
                postsFeed.addFirst(messagePost);
                break;
            case DELETE:
                postsFeed = postsFeed.stream()
                    .filter(post -> !post.getId().equals(messagePost.getId()))
                    .collect(Collectors.toCollection(LinkedList::new));
                break;
        }
        postsFeedCache.put(userId, postsFeed);
        log.info("Posts feed cache for userId: {} was updated", userId);
      } else {
        log.info("Posts feed cache for userId: {} is empty", userId);
      }
    }
  }
}
