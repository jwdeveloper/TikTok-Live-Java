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
package io.github.jwdeveloper.tiktok.extension.recorder.impl.data;

import io.github.jwdeveloper.tiktok.extension.recorder.impl.enums.LiveQuality;
import io.github.jwdeveloper.tiktok.extension.recorder.impl.enums.LiveFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.function.Function;

@Getter
@Setter
public class RecorderSettings {

    private String ffmpegPath;
    private String quality;
    private String format;
    private File outputFile;
    private Function<String,DownloadData> prepareDownloadData;
    private boolean stopOnDisconnect = true;
    /**
     True to Cancel connection to live if the download url is not found
     */
    private boolean cancelConnectionIfNotFound = false;

    public static RecorderSettings DEFAULT() {
        return new RecorderSettings();
    }

    public void setQuality(LiveQuality quality) {
        this.quality = quality.name();
    }

    public void setFormat(LiveFormat format) {
        this.format = format.name();
    }
}