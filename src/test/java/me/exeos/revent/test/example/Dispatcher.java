package me.exeos.revent.test.example;

import me.exeos.revent.test.EventTest;
import me.exeos.revent.test.events.ExampleEvent;

public class Dispatcher {

    public int run() {
        int x = 1;
        int y = 2;
        ExampleEvent event = new ExampleEvent(x, y);
        EventTest.revent.fire(event);
        x = event.x;
        y = event.y;

        return x * y;
    }
}
