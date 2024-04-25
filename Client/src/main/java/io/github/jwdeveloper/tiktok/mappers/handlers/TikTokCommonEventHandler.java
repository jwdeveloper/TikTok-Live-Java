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
package io.github.jwdeveloper.tiktok.mappers.handlers;

import io.github.jwdeveloper.tiktok.data.events.*;
import io.github.jwdeveloper.tiktok.data.events.common.TikTokEvent;
import io.github.jwdeveloper.tiktok.data.events.envelop.TikTokChestEvent;
import io.github.jwdeveloper.tiktok.data.events.poll.TikTokPollEndEvent;
import io.github.jwdeveloper.tiktok.data.events.poll.TikTokPollEvent;
import io.github.jwdeveloper.tiktok.data.events.poll.TikTokPollStartEvent;
import io.github.jwdeveloper.tiktok.data.events.poll.TikTokPollUpdateEvent;
import io.github.jwdeveloper.tiktok.data.events.room.TikTokRoomPinEvent;
import io.github.jwdeveloper.tiktok.data.models.chest.Chest;
import io.github.jwdeveloper.tiktok.messages.enums.EnvelopeDisplay;
import io.github.jwdeveloper.tiktok.messages.webcast.*;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;

public class TikTokCommonEventHandler
{

    @SneakyThrows
    public TikTokEvent handleWebcastControlMessage(byte[] msg) {
        WebcastControlMessage message = WebcastControlMessage.parseFrom(msg);
        switch (message.getAction()) {
            case STREAM_PAUSED: return new TikTokLivePausedEvent();
            case STREAM_ENDED: return new TikTokLiveEndedEvent();
            case STREAM_UNPAUSED: return new TikTokLiveUnpausedEvent();
            default: return new TikTokUnhandledControlEvent(message);
        }
    }

    @SneakyThrows
    public TikTokEvent handlePinMessage(byte[] msg) {
        WebcastRoomPinMessage pinMessage = WebcastRoomPinMessage.parseFrom(msg);
        WebcastChatMessage chatMessage = WebcastChatMessage.parseFrom(pinMessage.getPinnedMessage());
        TikTokCommentEvent chatEvent = new TikTokCommentEvent(chatMessage);
        return new TikTokRoomPinEvent(pinMessage, chatEvent);
    }

    //TODO Probably not working
    @SneakyThrows
    public TikTokEvent handlePollEvent(byte[] msg) {
        WebcastPollMessage poolMessage = WebcastPollMessage.parseFrom(msg);
        switch (poolMessage.getMessageType()) {
            case MESSAGETYPE_SUBSUCCESS: return new TikTokPollStartEvent(poolMessage);
            case MESSAGETYPE_ANCHORREMINDER: return new TikTokPollEndEvent(poolMessage);
            case MESSAGETYPE_ENTERROOMEXPIRESOON: return new TikTokPollUpdateEvent(poolMessage);
            default: return new TikTokPollEvent(poolMessage);
        }
    }

    @SneakyThrows
    public List<TikTokEvent> handleEnvelop(byte[] data) {
        WebcastEnvelopeMessage msg = WebcastEnvelopeMessage.parseFrom(data);
        if (msg.getDisplay() != EnvelopeDisplay.EnvelopeDisplayNew) {
            return Collections.emptyList();
        }
        int totalDiamonds = msg.getEnvelopeInfo().getDiamondCount();
        int totalUsers = msg.getEnvelopeInfo().getPeopleCount();
        Chest chest = new Chest(totalDiamonds, totalUsers);

        return Collections.singletonList(new TikTokChestEvent(chest, msg));
    }

}
