/*
 * Copyright (c) 2023-2024 jwdeveloper jacekwoln@gmail.com
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
package io.github.jwdeveloper.tiktok.extension.collector.impl;

import io.github.jwdeveloper.tiktok.extension.collector.api.CollectorEvent;
import io.github.jwdeveloper.tiktok.extension.collector.api.Storage;
import io.github.jwdeveloper.tiktok.extension.collector.api.settings.CollectorListenerSettings;
import org.bson.Document;

import java.util.Map;
import java.util.function.Function;

public class DataCollector {

    private final Storage storage;

    public DataCollector(Storage storage) {
        this.storage = storage;
    }

    public void connect() {
        storage.connect();
    }

    public void disconnect() {
        storage.disconnect();
    }

    public DataCollectorListener newListener() {
        return newListener(Map.of());
    }

    public DataCollectorListener newListener(Map<String, Object> additionalFields) {
        return newListener(additionalFields, (live, document) -> true);
    }

    public DataCollectorListener newListener(Map<String, Object> additionalFields,
                                             CollectorEvent filter) {
        var settings = new CollectorListenerSettings();
        settings.setExtraFields(additionalFields);
        settings.setFilter(filter);
        return new DataCollectorListener(storage, settings);
    }
}