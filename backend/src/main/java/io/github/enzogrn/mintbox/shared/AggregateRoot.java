package io.github.enzogrn.mintbox.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base class for aggregate roots.
 *
 * <p>An aggregate records the domain events its business methods cause. The application
 * service that saved the aggregate then publishes them and clears them. See
 * {@code docs/adr/0009-framework-free-domain-model.md}.
 *
 * <p>Two aggregates are equal when they have the same identifier, whatever their state:
 * an aggregate is an entity, defined by its identity.
 *
 * @param <ID> the type of the aggregate's identifier
 */
public abstract class AggregateRoot<ID> {

    private final List<Object> domainEvents = new ArrayList<>();

    public abstract ID id();

    protected void registerEvent(Object event) {
        domainEvents.add(Objects.requireNonNull(event, "event must not be null"));
    }

    /** Returns the events recorded since the last call to {@link #clearDomainEvents()}. */
    public List<Object> domainEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof AggregateRoot<?> aggregate
                && getClass() == aggregate.getClass()
                && id().equals(aggregate.id());
    }

    @Override
    public final int hashCode() {
        return id().hashCode();
    }
}
