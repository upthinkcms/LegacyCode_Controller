// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import java.io.IOException;
import java.nio.file.Path;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.yojito.minima.gson.GsonArray;
import com.yojito.minima.gson.GsonObject;
import com.yojito.minima.logging.MinimaLogger;

public class QBankEquationProcessor extends MathTypeEquationProcessor
{
    private static final MinimaLogger LOGGER;
    
    public QBankEquationProcessor(final String calabashPath, final String jdkPath) {
        super(calabashPath, jdkPath);
    }
    
    public GsonObject createEquationJson2(final String dir, final String docId, final String docName) throws IOException {
        final GsonObject doc = new GsonObject();
        final GsonArray questions = new GsonArray();
        final File directory = Paths.get(dir, new String[0]).toFile();
        String name = null;
        final String[] files = directory.list((f, name) -> name.startsWith(docId));
        final File rootDir = directory.getParentFile();
        final Path q_Path = Paths.get(rootDir.getAbsolutePath(), String.format("%s-oleImageMap.json", docName));
        final GsonObject qMap = new GsonObject(q_Path.toFile().exists() ? Files.readString(q_Path) : "{}");
        final String[] array = files;
        for (int length = array.length, i = 0; i < length; ++i) {
            final String f = array[i];
            final GsonObject eq = new GsonObject();
            final String[] a = f.split("-");
            name = a[a.length - 1];
            String content = Files.readString(Paths.get(directory.getAbsolutePath(), f));
            if (content.contains("(ENOENT) No such file or directory - Equation Native")) {
                name = name.substring(0, name.length() - 4) + ".bin";
                String eqImageName = null;
                eqImageName = qMap.optString(name, (String)null);
                if (eqImageName != null) {
                    eqImageName = eqImageName.substring(0, eqImageName.length() - 4) + "-emf.png";
                    eq.put("name", name);
                    eq.put("useImage", Boolean.valueOf(true));
                    eq.put("image", eqImageName);
                }
            }
            else {
                name = name.substring(0, name.length() - 4) + ".bin";
                content = content.replaceAll(" display=\\\"block\\\">", ">");
                content = content.replaceAll("mml:", "");
                eq.put("name", name);
                eq.put("mathml", content);
                eq.put("useImage", Boolean.valueOf(false));
            }
            questions.add(eq);
        }
        doc.put("qs", questions);
        return doc;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)QBankEquationProcessor.class);
    }
}
