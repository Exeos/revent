package me.exeos.revent.test.events;

import me.exeos.revent.event.Event;

public class ExampleEvent extends Event {

    public int x, y;

    public ExampleEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
