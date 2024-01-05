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
package io.github.jwdeveloper.tiktok.http;

import io.github.jwdeveloper.tiktok.data.settings.HttpClientSettings;
import io.github.jwdeveloper.tiktok.exceptions.TikTokLiveRequestException;
import lombok.AllArgsConstructor;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class HttpClient {
    private final HttpClientSettings httpClientSettings;
    private final String url;


    public <T> Optional<HttpResponse<T>> toResponse(HttpResponse.BodyHandler<T> bodyHandler) {
        var client = prepareClient();
        var request = prepareGetRequest();
        try
        {
            var response = client.send(request, bodyHandler);
            if(response.statusCode() != 200)
            {
                return Optional.empty();
            }



            return Optional.of(response);
        } catch (Exception e) {
            throw new TikTokLiveRequestException(e);
        }
    }


    public Optional<String> toJsonResponse() {
        var optional = toResponse(HttpResponse.BodyHandlers.ofString());
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var response = optional.get();


        var body = response.body();
        return Optional.of(body);
    }

    public Optional<byte[]> toBinaryResponse() {
        var optional = toResponse(HttpResponse.BodyHandlers.ofByteArray());
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        var body = optional.get().body();
        return Optional.of(body);
    }


    public URI toUrl() {
        var stringUrl = prepareUrlWithParameters(url, httpClientSettings.getParams());
        return URI.create(stringUrl);
    }

    private HttpRequest prepareGetRequest() {
        var requestBuilder = HttpRequest.newBuilder().GET();
        requestBuilder.uri(toUrl());
        requestBuilder.timeout(httpClientSettings.getTimeout());
        httpClientSettings.getHeaders().forEach(requestBuilder::setHeader);

        httpClientSettings.getOnRequestCreating().accept(requestBuilder);
        return requestBuilder.build();
    }

    private java.net.http.HttpClient prepareClient() {
        var builder = java.net.http.HttpClient.newBuilder()
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .cookieHandler(new CookieManager())
                .connectTimeout(httpClientSettings.getTimeout());


        httpClientSettings.getOnClientCreating().accept(builder);
        return builder.build();
    }

    private String prepareUrlWithParameters(String url, Map<String, Object> parameters) {
        if (parameters.isEmpty()) {
            return url;
        }

        return url + "?" + parameters.entrySet().stream().map(entry ->
        {
            var encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            var encodedValue = URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8);
            return encodedKey + "=" + encodedValue;
        }).collect(Collectors.joining("&"));
    }
}
