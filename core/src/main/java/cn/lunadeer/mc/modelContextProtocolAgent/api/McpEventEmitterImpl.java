package cn.lunadeer.mc.modelContextProtocolAgent.api;

import cn.lunadeer.mc.modelContextProtocolAgentSDK.api.McpEventEmitter;
import cn.lunadeer.mc.modelContextProtocolAgentSDK.api.SubscriptionFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Implementation of the McpEventEmitter interface.
 * <p>
 * Manages event subscriptions and emits events to subscribers.
 * </p>
 *
 * @author ZhangYuheng
 * @since 1.0.0
 */
public class McpEventEmitterImpl implements McpEventEmitter {

    private final Map<String, List<SubscriptionImpl>> subscriptions = new ConcurrentHashMap<>();

    @Override
    public void emit(String eventId, Object eventData) {
        List<SubscriptionImpl> subscribers = subscriptions.get(eventId);
        if (subscribers == null || subscribers.isEmpty()) {
            return;
        }

        for (SubscriptionImpl subscription : subscribers) {
            try {
                subscription.getCallback().accept(eventData);
            } catch (Exception e) {
                // Log error but continue with other subscribers
                System.err.println("Error in event callback for " + eventId + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void emit(String eventId, Object eventData, Predicate<Subscription> filter) {
        List<SubscriptionImpl> subscribers = subscriptions.get(eventId);
        if (subscribers == null || subscribers.isEmpty()) {
            return;
        }

        for (SubscriptionImpl subscription : subscribers) {
            if (filter.test(subscription)) {
                try {
                    subscription.getCallback().accept(eventData);
                } catch (Exception e) {
                    // Log error but continue with other subscribers
                    System.err.println("Error in event callback for " + eventId + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    public Subscription subscribe(String eventId, SubscriptionFilter filter, String subscriberId) {
        SubscriptionImpl subscription = new SubscriptionImpl(
                UUID.randomUUID().toString(),
                eventId,
                filter,
                subscriberId,
                eventData -> {
                    // Default callback - will be overridden by subscriber
                }
        );

        subscriptions.computeIfAbsent(eventId, k -> new ArrayList<>()).add(subscription);
        return subscription;
    }

    @Override
    public void unsubscribe(String subscriptionId) {
        subscriptions.values().forEach(subscribers -> {
            subscribers.removeIf(sub -> sub.getId().equals(subscriptionId));
        });
    }

    @Override
    public void unsubscribeAll(String subscriberId) {
        subscriptions.values().forEach(subscribers -> {
            subscribers.removeIf(sub -> sub.getSubscriberId().equals(subscriberId));
        });
    }

    /**
     * Gets the number of active subscriptions for an event.
     *
     * @param eventId the event ID
     * @return the number of subscriptions
     */
    public int getSubscriptionCount(String eventId) {
        List<SubscriptionImpl> subscribers = subscriptions.get(eventId);
        return subscribers != null ? subscribers.size() : 0;
    }

    /**
     * Clears all subscriptions (for testing or cleanup).
     */
    public void clearAll() {
        subscriptions.clear();
    }

    /**
     * Implementation of Subscription interface.
     */
    private static class SubscriptionImpl implements McpEventEmitter.Subscription {

        private final String id;
        private final String eventId;
        private final SubscriptionFilter filter;
        private final String subscriberId;
        private final java.util.function.Consumer<Object> callback;

        public SubscriptionImpl(
                String id,
                String eventId,
                SubscriptionFilter filter,
                String subscriberId,
                java.util.function.Consumer<Object> callback
        ) {
            this.id = id;
            this.eventId = eventId;
            this.filter = filter;
            this.subscriberId = subscriberId;
            this.callback = callback;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getEventId() {
            return eventId;
        }

        @Override
        public String getSubscriberId() {
            return subscriberId;
        }

        @Override
        public Object getFilterParameter(String key) {
            return filter != null ? filter.get(key) : null;
        }

        /**
         * Gets the callback function.
         *
         * @return the callback
         */
        public java.util.function.Consumer<Object> getCallback() {
            return callback;
        }
    }
}
