// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import org.zwobble.mammoth.internal.util.Maps;
import org.apache.commons.compress.utils.IOUtils;
import org.zwobble.mammoth.internal.util.Base64Encoding;
import java.util.Objects;
import java.util.Map;
import org.zwobble.mammoth.images.Image;
import org.im4java.core.Operation;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import java.io.IOException;
import java.util.Set;
import org.zwobble.mammoth.Result;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import org.zwobble.mammoth.DocumentConverter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.File;

public class Doc2XML
{
    public static void main(final String[] args) throws IOException {
        final String dir = "/work/myworkspace/druta/projects/upthinkexperts/docs/prepared";
        final String filePath = dir + File.separator + "21349-25-25.26AP.docx";
        final String fileName = "21349-25-25.26AP.docx";
        final AtomicInteger ai = new AtomicInteger();
        final List<String> images = new ArrayList<String>();
        final DocumentConverter converter = new DocumentConverter().imageConverter(image -> {
            System.out.println("Image ->> " + image.getContentType());
            Objects.requireNonNull(image);
            final String base64 = Base64Encoding.streamToBase64(image::getInputStream);
            final byte[] bytes = IOUtils.toByteArray(image.getInputStream());
            final String imageFileName = "image" + ai.incrementAndGet() + ".emf";
            Files.write(Paths.get(imageFileName, new String[0]), bytes, new OpenOption[0]);
            final String src = "data:" + image.getContentType() + ";base64," + base64;
            images.add(imageFileName);
            return Maps.map((Object)"src", "./image" + ai.get() + ".png");
        });
        final Result<String> result = (Result<String>)converter.convertToHtml(new File(dir, fileName));
        final String html = (String)result.getValue();
        final Set<String> warnings = result.getWarnings();
        final StringBuilder stringBuilder = new StringBuilder("<html><body>").append(html).append("</body></html>");
        Files.write(Paths.get("1.html", new String[0]), stringBuilder.toString().getBytes(), new OpenOption[0]);
        images.forEach(image -> convert(image, image + ".jpg"));
    }
    
    public static void convert(final String input, final String output) {
        try {
            final IMOperation img = new IMOperation();
            img.addImage();
            img.addImage();
            final ConvertCmd convert = new ConvertCmd();
            convert.run((Operation)img, new Object[] { input, output });
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
