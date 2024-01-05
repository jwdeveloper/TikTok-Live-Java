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
package io.github.jwdeveloper.tiktok.live.builder;

import io.github.jwdeveloper.tiktok.data.settings.LiveClientSettings;
import io.github.jwdeveloper.tiktok.listener.TikTokEventListener;
import io.github.jwdeveloper.tiktok.live.LiveClient;
import io.github.jwdeveloper.tiktok.mappers.TikTokMapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface LiveClientBuilder extends EventsBuilder<LiveClientBuilder> {

    /**
     * This method is triggered after default mappings are registered
     * It could be used to OVERRIDE behaviour of mappings and implement custom behaviour
     *
     * Be aware if for example you override WebcastGiftEvent, onGiftEvent() will not be working
     *
     * @param onCustomMappings lambda method
     * @return
     */
    LiveClientBuilder onMapping(Consumer<TikTokMapper> onCustomMappings);


    /**
     * Configuration of client settings
     * @see LiveClientSettings
     * @param onConfigure
     * @return
     */
    LiveClientBuilder configure(Consumer<LiveClientSettings> onConfigure);

    /**
     * @see TikTokEventListener
     * Adding events listener class, its fancy way to register events without using lamda method
     * but actual method in class that implements TikTokEventListener
     * @return
     */
    LiveClientBuilder addListener(TikTokEventListener listener);

    /**
     *
     * @return LiveClient object
     */
    LiveClient build();

    /**
     *
     * @return LiveClient object and connects to TikTok live
     */
    LiveClient buildAndConnect();

    /**
     *
     * @return LiveClient object and connects to TikTok live asynchronously
     */
    CompletableFuture<LiveClient> buildAndConnectAsync();
}
