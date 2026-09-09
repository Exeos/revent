package me.exeos.revent;

import me.exeos.revent.annotation.Subscribe;
import me.exeos.revent.event.Event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.*;

public class Revent {

    public static final int DEFAULT_POS = -1;
    private final Map<Class<?>, Set<Method>> clazzSubCache = new HashMap<>();
    private final Map<Class<? extends Event>, Map<Object, Map<Integer, Set<MethodHandle>>>> registeredByEvent = new HashMap<>();

    public void fire(Event event) {
        fire(event, DEFAULT_POS);
    }

    public void fire(Event event, int position) {
        Map<Object, Map<Integer, Set<MethodHandle>>> ownerSubMap = registeredByEvent.get(event.getClass());
        if (ownerSubMap == null) {
            return;
        }

        for (Map<Integer, Set<MethodHandle>> innerMap : ownerSubMap.values()) {
            Set<MethodHandle> handles = innerMap.get(position);
            if (handles == null || handles.isEmpty()) {
                continue;
            }

            for (MethodHandle handle : handles) {
                try {
                    handle.invoke(event);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void register(Object owner) {
        boolean ownerIsStatic = owner instanceof Class<?>;
        Class<?> ownerClass = ownerIsStatic ? (Class<?>) owner : owner.getClass();

        Set<Method> annotatedMethods = getSubAnnotatedMethods(ownerClass);
        if (annotatedMethods.isEmpty()) {
            return;
        }

        // remove owner from event map
        for (var ownerSubMap : registeredByEvent.values()) {
            ownerSubMap.remove(owner);
        }

        // register owner subs
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        for (Method method : annotatedMethods) {
            int position = method.getDeclaredAnnotation(Subscribe.class).position();
            @SuppressWarnings("unchecked") // validation in getSubAnnotatedMethods guarantees this
            Class<? extends Event> target = (Class<? extends Event>) method.getParameterTypes()[0];

            MethodType methodType = MethodType.methodType(void.class, target);
            MethodHandle handle;
            try {
                if (ownerIsStatic) {
                    handle = lookup.findStatic(ownerClass, method.getName(), methodType);
                } else {
                    handle = lookup.findVirtual(ownerClass, method.getName(), methodType).bindTo(owner);
                }
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            registeredByEvent.computeIfAbsent(target, _ -> new HashMap<>())
                    .computeIfAbsent(owner, _ -> new HashMap<>())
                    .computeIfAbsent(position, _ -> new HashSet<>())
                    .add(handle);
        }
    }

    public void unregister(Object instance) {
        for (var ownerSubMap : registeredByEvent.values()) {
            ownerSubMap.remove(instance);
        }
    }

    private Set<Method> getSubAnnotatedMethods(Class<?> sourceClass) {
        Set<Method> annotatedMethods = clazzSubCache.get(sourceClass);
        if (annotatedMethods != null) {
            return annotatedMethods;
        }

        annotatedMethods = new HashSet<>();
        for (Method method : sourceClass.getDeclaredMethods()) {
            Subscribe subAnnotation = method.getDeclaredAnnotation(Subscribe.class);
            if (subAnnotation == null) {
                continue;
            }

            if (method.getReturnType() != void.class || method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                throw new RuntimeException("Invalid method descriptor for handler method. Needs to return void and have exactly one parameter (The event)");
            }

            annotatedMethods.add(method);
        }

        clazzSubCache.put(sourceClass, annotatedMethods);
        return annotatedMethods;
    }
}
