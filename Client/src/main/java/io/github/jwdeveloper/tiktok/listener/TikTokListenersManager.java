/*
 * Copyright (c) 2023-2023 jwdeveloper jacekwoln@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.jwdeveloper.tiktok.listener;


import io.github.jwdeveloper.tiktok.TikTokLiveEventHandler;
import io.github.jwdeveloper.tiktok.annotations.TikTokEventObserver;
import io.github.jwdeveloper.tiktok.data.events.common.TikTokEvent;
import io.github.jwdeveloper.tiktok.exceptions.TikTokEventListenerMethodException;
import io.github.jwdeveloper.tiktok.exceptions.TikTokLiveException;
import io.github.jwdeveloper.tiktok.live.LiveClient;
import io.github.jwdeveloper.tiktok.live.builder.EventConsumer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class TikTokListenersManager implements ListenersManager {
    private final TikTokLiveEventHandler eventObserver;
    private final List<ListenerBindingModel> bindingModels;

    public TikTokListenersManager(List<TikTokEventListener> listeners, TikTokLiveEventHandler tikTokEventHandler) {
        this.eventObserver = tikTokEventHandler;
        this.bindingModels = new ArrayList<>(listeners.size());
        for (TikTokEventListener listener : listeners) {
            addListener(listener);
        }
    }

    @Override
    public List<TikTokEventListener> getListeners() {
        return bindingModels.stream().map(ListenerBindingModel::getListener).collect(Collectors.toList());
    }

    @Override
    public void addListener(TikTokEventListener listener) {
        Optional<ListenerBindingModel> alreadyExists = bindingModels.stream().filter(e -> e.getListener() == listener).findAny();
        if (alreadyExists.isPresent()) {
            throw new TikTokLiveException("Listener " + listener.getClass() + " has already been registered");
        }
        ListenerBindingModel bindingModel = bindToEvents(listener);

        for (Map.Entry<Class<?>, List<EventConsumer<?>>> eventEntrySet : bindingModel.getEvents().entrySet()) {
            Class<?> eventType = eventEntrySet.getKey();
            for (EventConsumer<?> methods : eventEntrySet.getValue()) {
                eventObserver.subscribe(eventType, methods);
            }
        }
        bindingModels.add(bindingModel);
    }

    @Override
    public void removeListener(TikTokEventListener listener) {
        Optional<ListenerBindingModel> optional = bindingModels.stream().filter(e -> e.getListener() == listener).findAny();
        if (!optional.isPresent()) {
            return;
        }

        ListenerBindingModel bindingModel = optional.get();

        for (Map.Entry<Class<?>, List<EventConsumer<?>>> eventEntrySet : bindingModel.getEvents().entrySet()) {
            Class<?> eventType = eventEntrySet.getKey();
            for (EventConsumer<?> methods : eventEntrySet.getValue()) {
                eventObserver.unsubscribe(eventType, methods);
            }
        }
        bindingModels.remove(optional.get());
    }

    private ListenerBindingModel bindToEvents(TikTokEventListener listener) {

        Class<?> clazz = listener.getClass();
        List<Method> methods = Arrays.stream(clazz.getDeclaredMethods()).filter(m ->
                m.getParameterCount() == 2 &&
                        m.isAnnotationPresent(TikTokEventObserver.class)).collect(Collectors.toList());
        Map<Class<?>, List<EventConsumer<?>>> eventsMap = new HashMap<Class<?>, List<EventConsumer<?>>>();
        for (Method method : methods) {
            Class<?> eventClazz = method.getParameterTypes()[1];

            if (eventClazz.isAssignableFrom(LiveClient.class) &&
                    !eventClazz.equals(LiveClient.class)) {
                throw new TikTokEventListenerMethodException("Method " + method.getName() + "() 1nd parameter must instance of " + LiveClient.class.getName());
            }

            if (eventClazz.isAssignableFrom(TikTokEvent.class) &&
                    !eventClazz.equals(TikTokEvent.class)) {
                throw new TikTokEventListenerMethodException("Method " + method.getName() + "() 2nd parameter must instance of " + TikTokEvent.class.getName());
            }

            EventConsumer<?> eventMethodRef = (liveClient, event) ->
            {
                try {
                    method.setAccessible(true);
                    method.invoke(listener, liveClient, event);
                } catch (Exception e) {
                    throw new TikTokEventListenerMethodException(e);
                }
            };
            eventsMap.computeIfAbsent(eventClazz, (a) -> new ArrayList<>()).add(eventMethodRef);
        }
        return new ListenerBindingModel(listener, eventsMap);
    }
}
