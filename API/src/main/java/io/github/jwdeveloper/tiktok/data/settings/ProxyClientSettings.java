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
package io.github.jwdeveloper.tiktok.data.settings;

import io.github.jwdeveloper.tiktok.data.dto.ProxyData;
import lombok.*;

import java.net.*;
import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter
public class ProxyClientSettings implements Iterator<ProxyData>
{
    private boolean enabled, lastSuccess;
    private Rotation rotation = Rotation.CONSECUTIVE;
    private final List<ProxyData> proxyList = new ArrayList<>();
    private int index = 0;
    private boolean autoDiscard = true;
    private Proxy.Type type = Proxy.Type.DIRECT;
    private Consumer<ProxyData> onProxyUpdated = x -> {};

    public boolean addProxy(String addressPort) {
        return proxyList.add(ProxyData.map(addressPort));
    }

    public boolean addProxy(String address, int port) {
        return addProxy(new InetSocketAddress(address, port));
    }

    public boolean addProxy(InetSocketAddress inetAddress) {
        return proxyList.add(new ProxyData(inetAddress.getHostString(), inetAddress.getPort()));
    }

    public void addProxies(List<String> list) {
        list.forEach(this::addProxy);
    }

    @Override
    public boolean hasNext() {
        return !proxyList.isEmpty();
    }

    @Override
    public ProxyData next() {
		return lastSuccess ? proxyList.get(index) : rotate();
	}

    public ProxyData rotate() {
        var nextProxy = switch (rotation)
        {
            case CONSECUTIVE -> {
                index = (index+1) % proxyList.size();
                yield proxyList.get(index).clone();
            }
            case RANDOM -> {
                index = new Random().nextInt(proxyList.size());
                yield proxyList.get(index).clone();
            }
            case NONE -> proxyList.get(index).clone();
        };
        onProxyUpdated.accept(nextProxy);
        return nextProxy;
    }

    @Override
    public void remove() {
        proxyList.remove(index);
    }

    public void setIndex(int index) {
        if (index == 0 && proxyList.isEmpty())
            this.index = 0;
        else {
            if (index < 0 || index >= proxyList.size())
                throw new IndexOutOfBoundsException("Index " + index + " exceeds list of size: " + proxyList.size());
            this.index = index;
        }
    }
    public ProxyClientSettings clone()
    {
        ProxyClientSettings settings = new ProxyClientSettings();
        settings.setEnabled(enabled);
        settings.setRotation(rotation);
        settings.setIndex(index);
        settings.setType(type);
        settings.setOnProxyUpdated(onProxyUpdated);
        proxyList.forEach(proxyData -> settings.addProxy(proxyData.getAddress(), proxyData.getPort()));
        return settings;
    }

    public enum Rotation
    {
        /** Rotate addresses consecutively, from proxy 0 -> 1 -> 2 -> ...etc. */
        CONSECUTIVE,
        /** Rotate addresses randomly, from proxy 0 -> 69 -> 420 -> 1 -> ...etc. */
        RANDOM,
        /** Don't rotate addresses at all, pin to the indexed address. */
        NONE
    }
}