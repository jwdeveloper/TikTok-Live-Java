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
package io.github.jwdeveloper.tiktok;
import java.util.HashMap;
import java.util.Map;

public class ReadmeGenerator {
    public static void main(String[] args) {
        ReadmeGenerator generator = new ReadmeGenerator();
        generator.generate();

    }

    public void generate() {
        String template = FilesUtility.getFileFromResource(ReadmeGenerator.class, "template.md");
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("version", getCurrentVersion());
        variables.put("code-content", new CodeExamplesGenerator().run());
        variables.put("events-content", new EventsInfoGenerator().run());
        variables.put("listener-content",new ListenerExampleGenerator().run());

        template = TemplateUtility.generateTemplate(template, variables);
        String outputPath = "C:\\Users\\ja\\IdeaProjects\\TikTokLiveJava\\Tools-ReadmeGenerator\\src\\main\\resources\\output.md";
        FilesUtility.saveFile(outputPath, template);
    }

    public String getCurrentVersion() {
        String version = System.getenv("version");
        return version == null ? "NOT_FOUND" : version;
    }

    public String getCodeExample(String path) {
        String content = FilesUtility.loadFileContent(path);
        content = content.substring(content.indexOf("*/") + 2);
        return content;
    }

}
