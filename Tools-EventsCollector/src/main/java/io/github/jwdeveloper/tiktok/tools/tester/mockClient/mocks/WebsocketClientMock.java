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
package io.github.jwdeveloper.tiktok.tools.tester.mockClient.mocks;

import io.github.jwdeveloper.tiktok.data.requests.LiveConnectionData;
import io.github.jwdeveloper.tiktok.TikTokLiveMessageHandler;
import io.github.jwdeveloper.tiktok.live.LiveClient;
import io.github.jwdeveloper.tiktok.messages.webcast.WebcastResponse;
import io.github.jwdeveloper.tiktok.websocket.SocketClient;
import lombok.Value;

import java.util.Base64;
import java.util.Stack;
import java.util.logging.Logger;


public class WebsocketClientMock implements SocketClient {
    Logger logger;
    Stack<WebcastResponse> responses;
    Stack<MsgStruct> messages;
    TikTokLiveMessageHandler messageHandler;

    LiveClient client;

    Thread thread;

    private boolean isRunning;

    public void setClient(LiveClientMock liveClientMock) {
        this.client = liveClientMock;
    }

    @Value
    public static class MsgStruct {
        String messageType;
        byte[] messageValue;
    }

    public WebsocketClientMock(Logger logger, Stack<WebcastResponse> responses, TikTokLiveMessageHandler messageHandler) {
        this.logger = logger;
        this.responses = responses;
        this.messageHandler = messageHandler;
        messages = new Stack<>();
    }

    public WebsocketClientMock addMessage(String type, String value) {
        var bytes = Base64.getDecoder().decode(value);
        messages.push(new MsgStruct(type, bytes));
        return this;
    }

    public WebsocketClientMock addResponse(String value) {
        var bytes = Base64.getDecoder().decode(value);
        return addResponse(bytes);
    }

    public WebsocketClientMock addResponse(byte[] bytes) {
        try {
            var response = WebcastResponse.parseFrom(bytes);
            return addResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse response from bytes", e);
        }
    }

    public WebsocketClientMock addResponse(WebcastResponse message) {
        responses.push(message);
        return this;
    }



    @Override
    public void start(LiveConnectionData.Response webcastResponse, LiveClient tikTokLiveClient) {
        logger.info("Running message: " + responses.size());


        thread = new Thread(() ->
        {

            while (isRunning)
            {
                while (!responses.isEmpty()) {
                    var response = responses.pop();
                    messageHandler.handle(client, response);
                }
            }

        });
        isRunning = true;
        thread.start();
    }

    @Override
    public void stop() {
        isRunning = false;
        thread.interrupt();
    }
}
