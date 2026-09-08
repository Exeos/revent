package me.exeos.revent.test;

import me.exeos.revent.Revent;
import me.exeos.revent.test.example.Dispatcher;
import me.exeos.revent.test.example.StaticHandler;
import me.exeos.revent.test.example.VirtualHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EventTest {

    public static final Revent revent = new Revent();

    @Test
    public void virtualOwner() {
        new VirtualHandler();
        Dispatcher dispatcher = new Dispatcher();
        assertEquals(6, dispatcher.run());
    }

    @Test
    public void staticOwner() {
        new StaticHandler();
        Dispatcher dispatcher = new Dispatcher();
        assertEquals(6, dispatcher.run());
    }
}
