package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

public final class StreamEndEvent extends Event {

    public StreamEndEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }

    public boolean is(Event.ID id) {
        return Event.ID.StreamEnd == id;
    }
}
