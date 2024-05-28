// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.yojito.minima.gson.GsonArray;
import com.yojito.minima.gson.GsonObject;
import java.io.IOException;
import java.io.File;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import com.yojito.minima.logging.MinimaLogger;

public class MathTypeEquationProcessor
{
    private static final MinimaLogger LOGGER;
    public static final String OLE_BIN = "olebin";
    public static final String MML = "mml";
    static final String EQ_OLE_ERROR = "(ENOENT) No such file or directory - Equation Native";
    private final String calabashPath;
    private final String jdkPath;
    protected int minMemory;
    private int maxMemory;
    
    public MathTypeEquationProcessor(final String calabashPath, final String jdkPath) {
        this.minMemory = 128;
        this.maxMemory = 256;
        this.calabashPath = calabashPath;
        this.jdkPath = jdkPath;
    }
    
    public void setMinMemory(final int minMemory) {
        this.minMemory = minMemory;
    }
    
    public void setMaxMemory(final int maxMemory) {
        this.maxMemory = maxMemory;
    }
    
    public void transformEquations(final String sourceDir, final String targetDir) throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
        final String EXEC = "./calabash/calabash.sh";
        final String O = "-o";
        final String RESULT = "result=mmlgen.xml";
        final String XPL = "calabash/extensions/transpect/mathtype-extension/xpl/mathtype2mml-directory.xpl";
        final String SOURCE_DIR = String.format("source-dir=file://%s/", sourceDir);
        final String STORE_MML = "store-mml=true";
        final String TARGET_DIR = String.format("target-dir=file://%s/", targetDir);
        final String TYPE = "type=bin";
        final String[] command = { EXEC, O, RESULT, XPL, SOURCE_DIR, STORE_MML, TARGET_DIR, TYPE };
        MathTypeEquationProcessor.LOGGER.info("Final Command " + (String)Arrays.asList(command).stream().collect((Collector<? super Object, ?, String>)Collectors.joining(" ")), new Object[0]);
        String PATH = processBuilder.environment().get("PATH");
        final String xmsArg = String.format("-Xms%dM", this.minMemory);
        PATH = this.jdkPath + File.pathSeparator + PATH;
        processBuilder.environment().put("PATH", PATH);
        processBuilder.environment().put("SYSPROPS", xmsArg);
        processBuilder.environment().put("HEAP", "" + this.maxMemory);
        MathTypeEquationProcessor.LOGGER.info("ENV MAP %s", new Object[] { processBuilder.environment() });
        processBuilder.directory(new File(this.calabashPath)).command(command);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT);
        final long l1 = System.currentTimeMillis();
        final Process process = processBuilder.start();
        final int code = process.waitFor();
        final long l2 = System.currentTimeMillis();
        MathTypeEquationProcessor.LOGGER.info("Finished with code : %s, Time taken = %d", new Object[] { code, l2 - l1 });
    }
    
    public GsonObject createEquationJson(final String dir, final String docId) throws IOException {
        final GsonObject doc = new GsonObject();
        final GsonArray questions = new GsonArray();
        final GsonArray answers = new GsonArray();
        final File directory = Paths.get(dir, new String[0]).toFile();
        String name = null;
        final String[] files = directory.list((f, name) -> name.startsWith(docId));
        final String qPrefix = String.format("%s-Q", docId);
        final String aPrefix = String.format("%s-A", docId);
        final File rootDir = directory.getParentFile();
        final Path q_Path = Paths.get(rootDir.getAbsolutePath(), String.format("%s-%s-oleImageMap.json", docId, "Q"));
        final Path a_Path = Paths.get(rootDir.getAbsolutePath(), String.format("%s-%s-oleImageMap.json", docId, "A"));
        final GsonObject qMap = new GsonObject(q_Path.toFile().exists() ? Files.readString(q_Path) : "{}");
        final GsonObject aMap = new GsonObject(a_Path.toFile().exists() ? Files.readString(a_Path) : "{}");
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
                if (f.startsWith(qPrefix)) {
                    eqImageName = qMap.optString(name, (String)null);
                }
                else {
                    eqImageName = aMap.optString(name, (String)null);
                }
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
            if (f.startsWith(qPrefix)) {
                questions.add(eq);
            }
            else if (f.startsWith(aPrefix)) {
                answers.add(eq);
            }
        }
        doc.put("qs", questions);
        doc.put("as", answers);
        return doc;
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)MathTypeEquationProcessor.class);
    }
}
