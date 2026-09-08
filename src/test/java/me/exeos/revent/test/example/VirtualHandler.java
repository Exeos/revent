package me.exeos.revent.test.example;

import me.exeos.revent.annotation.Subscribe;
import me.exeos.revent.test.EventTest;
import me.exeos.revent.test.events.ExampleEvent;

public class VirtualHandler {

    public VirtualHandler() {
        EventTest.revent.register(this);
    }

    @Subscribe(target = ExampleEvent.class)
    public void onExample(ExampleEvent event) {
        event.x = 2;
        event.y = 3;
    }
}
