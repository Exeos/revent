package me.exeos.revent.test.example;

import me.exeos.revent.annotation.Subscribe;
import me.exeos.revent.test.EventTest;
import me.exeos.revent.test.events.ExampleEvent;

public class StaticHandler {

    static {
        EventTest.revent.register(StaticHandler.class);
    }

    @Subscribe(target = ExampleEvent.class)
    public static void onExample(ExampleEvent event) {
        event.x = 2;
        event.y = 3;
    }
}
