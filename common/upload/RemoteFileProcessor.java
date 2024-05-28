// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.upload;

import com.upthinkexperts.common.util.IndexCommand;
import org.jdbi.v3.core.Handle;
import org.apache.commons.io.FileUtils;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.yojito.minima.gson.GsonArray;
import com.yojito.minima.gson.GsonObject;
import com.upthinkexperts.common.util.MathTypeEquationProcessor;
import java.util.Optional;
import com.upthinkexperts.common.domain.DocResources;
import com.upthinkexperts.common.db.DocDB;
import com.upthinkexperts.common.parsing.DocxRelationshipProcessor;
import java.util.concurrent.ExecutionException;
import com.upthinkexperts.common.util.TextIndex;
import java.util.concurrent.TimeoutException;
import com.upthinkexperts.common.util.IndexId;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import com.yojito.minima.db.Database;
import com.upthinkexperts.common.db.UploadJobDB;
import com.amazonaws.services.s3.model.S3Object;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.zip.ZipFile;
import java.io.InputStream;
import com.amazonaws.util.IOUtils;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import com.amazonaws.services.s3.AmazonS3;
import com.upthinkexperts.common.util.IndexingBatch;
import com.upthinkexperts.common.parsing.spec.ParsingSpec;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.zip.ZipEntry;
import com.upthinkexperts.common.domain.JobFile;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Map;
import com.upthinkexperts.common.util.CmsCommonConfig;
import java.io.File;
import com.upthinkexperts.common.domain.UploadJob;
import com.yojito.minima.logging.MinimaLogger;

public class RemoteFileProcessor
{
    private static final MinimaLogger LOGGER;
    private final UploadJob uploadJob;
    private final File localFile;
    private final File localXtractDir;
    private final CmsCommonConfig cmsConfig;
    private final Map<String, Pair<String, String>> docFiles;
    private final Map<String, JobFile> jobFileMap;
    private final Map<String, ZipEntry> jobFileZipMap;
    private final List<CompletableFuture> futureList;
    private final ParsingSpec parsingSpec;
    private final IndexingBatch indexingBatch;
    private boolean reIndexing;
    private AmazonS3 s3Client;
    private String SOFFICE_PATH;
    private String MOGRIFY_PATH;
    
    public RemoteFileProcessor(final UploadJob uploadJob, final CmsCommonConfig context, final ParsingSpec parsingSpec) {
        this.docFiles = new HashMap<String, Pair<String, String>>();
        this.jobFileMap = new HashMap<String, JobFile>();
        this.jobFileZipMap = new HashMap<String, ZipEntry>();
        this.futureList = new ArrayList<CompletableFuture>();
        this.reIndexing = true;
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
        this.SOFFICE_PATH = "";
        this.MOGRIFY_PATH = "";
        this.uploadJob = uploadJob;
        this.cmsConfig = context;
        this.parsingSpec = parsingSpec;
        this.indexingBatch = new IndexingBatch();
        try {
            this.localFile = File.createTempFile(uploadJob.getFileName(), ".froms3");
            (this.localXtractDir = new File("/tmp/" + System.nanoTime())).mkdirs();
            final File oleBinDir = new File(String.format("%s/%s", this.localXtractDir, "olebin"));
            final File mmlDir = new File(String.format("%s/%s", this.localXtractDir, "mml"));
            final File imagesDir = new File(String.format("%s/%s", this.localXtractDir, "images"));
            final File imagesDir2 = new File(String.format("%s/%s", this.localXtractDir, "images2"));
            oleBinDir.mkdirs();
            mmlDir.mkdirs();
            imagesDir.mkdirs();
            imagesDir2.mkdirs();
            this.SOFFICE_PATH = context.getsOfficePath();
            this.MOGRIFY_PATH = context.getMogrifyPath();
        }
        catch (final IOException e) {
            throw new RuntimeException();
        }
    }
    
    public void setReIndexing(final boolean reIndexing) {
        this.reIndexing = reIndexing;
        this.indexingBatch.setReIndexing(reIndexing);
    }
    
    public void downloadAndExtract() throws IOException {
        final String s3FileKey = String.format("%s/%s", this.uploadJob.getUuid(), this.uploadJob.getFileName());
        RemoteFileProcessor.LOGGER.info("Downloading %s", new Object[] { this.uploadJob.getFileName() });
        final S3Object s3Object = this.s3Client.getObject(this.cmsConfig.getBucket(), s3FileKey);
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.localFile));
        IOUtils.copy((InputStream)s3Object.getObjectContent(), (OutputStream)bufferedOutputStream);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        RemoteFileProcessor.LOGGER.info("Downloaded %s to %s", new Object[] { this.uploadJob.getFileName(), this.localFile });
        final ZipFile zipFile = new ZipFile(this.localFile);
        final Set<String> fileNames = zipFile.stream().map(zipEntry -> zipEntry.getName()).collect((Collector<? super Object, ?, Set<String>>)Collectors.toSet());
        this.buildDocFileMap(fileNames, zipFile);
        RemoteFileProcessor.LOGGER.info("docFiles %s", new Object[] { this.docFiles });
        RemoteFileProcessor.LOGGER.info("jobFileMap %s", new Object[] { this.jobFileMap.entrySet().stream().map(e -> (String)e.getKey() + "-> " + e.getValue().toStringPretty()).collect((Collector<? super Object, ?, String>)Collectors.joining("\n")) });
    }
    
    String getFileNameFromPath(final String filePath) {
        final String[] split;
        final String[] a = split = filePath.split("/");
        for (int length = split.length, i = 0; i < length; ++i) {
            final String p = split[i];
            System.out.println(">>> " + p);
        }
        final String last = a[a.length - 1];
        return last;
    }
    
    private void buildDocFileMap(final Set<String> fileNames, final ZipFile zipFile) {
        fileNames.forEach(docFile -> {
            if (docFile.endsWith(".docx")) {
                final String name = this.getFileNameFromPath(docFile);
                final String nameWithoutExetension = name.substring(0, name.length() - ".docx".length());
                System.out.printf("nameWithoutExetension = %s\n", nameWithoutExetension);
                final String[] a = nameWithoutExetension.split("-");
                if (a[a.length - 1].equals("Question")) {
                    System.out.printf("Not reading %s\n", docFile);
                }
                else {
                    final boolean isQuestion = false;
                    final boolean isAnswer = false;
                    if (nameWithoutExetension.contains("Question-digital") || nameWithoutExetension.contains("Question-Digital")) {
                        final boolean isQuestion2 = true;
                        final boolean isAnswer2 = false;
                        final String docId = nameWithoutExetension.substring(0, nameWithoutExetension.length() - "-Question-digital".length());
                        System.out.printf("DocId =%s and its question\n", docId);
                        this.addJobFile(docId, docFile, true, zipFile);
                    }
                    else {
                        final boolean isAnswer3 = true;
                        final boolean isQuestion3 = false;
                        final String docId2 = nameWithoutExetension;
                        System.out.printf("DocId =%s and its answer\n", docId2);
                        this.addJobFile(docId2, docFile, false, zipFile);
                    }
                }
            }
        });
    }
    
    private void addJobFile(final String docId, final String docFile, final boolean isQuestion, final ZipFile zipFile) {
        Pair<String, String> pair = this.docFiles.get(docId);
        final ZipEntry jobFileEntry = (ZipEntry)zipFile.stream().filter(z -> z.getName().equals(docFile)).findFirst().get();
        JobFile jobFile;
        if (pair == null) {
            if (isQuestion) {
                pair = (Pair<String, String>)Pair.of((Object)docFile, (Object)null);
                jobFile = new JobFile(docId, this.uploadJob.getUuid(), docFile, true, this.uploadJob.getTenantKey());
            }
            else {
                pair = (Pair<String, String>)Pair.of((Object)null, (Object)docFile);
                jobFile = new JobFile(docId, this.uploadJob.getUuid(), docFile, false, this.uploadJob.getTenantKey());
            }
        }
        else if (isQuestion) {
            jobFile = new JobFile(docId, this.uploadJob.getUuid(), docFile, true, this.uploadJob.getTenantKey());
            pair = (Pair<String, String>)Pair.of((Object)docFile, (Object)pair.getRight());
        }
        else {
            jobFile = new JobFile(docId, this.uploadJob.getUuid(), docFile, false, this.uploadJob.getTenantKey());
            pair = (Pair<String, String>)Pair.of((Object)pair.getLeft(), (Object)docFile);
        }
        this.jobFileMap.put(docFile, jobFile);
        this.docFiles.put(docId, pair);
        this.jobFileZipMap.put(docFile, jobFileEntry);
    }
    
    public void createFileEntries() {
        if (!this.reIndexing) {
            Database.withinTx(this.cmsConfig.getDb(), handle -> UploadJobDB.insertUploadJobFiles(handle, this.jobFileMap.values(), this.uploadJob.getTenantKey()));
        }
    }
    
    public void prepareForIndexing() throws IOException {
        final ZipFile zipFile = new ZipFile(this.localFile);
        this.docFiles.entrySet().forEach(e -> {
            final Pair pair = e.getValue();
            final JobFile question = (pair.getLeft() != null) ? this.jobFileMap.get(pair.getLeft()) : null;
            final JobFile answer = (pair.getRight() != null) ? this.jobFileMap.get(pair.getRight()) : null;
            final ZipEntry questionEntry = (question != null) ? this.jobFileZipMap.get(pair.getLeft()) : null;
            final ZipEntry answerEntry = (answer != null) ? this.jobFileZipMap.get(pair.getRight()) : null;
            if (question != null || answer != null) {
                final JobFileProcessor processor = new JobFileProcessor(this.cmsConfig, this.uploadJob, question, answer, questionEntry, answerEntry);
                processor.setParsingSpec(this.parsingSpec);
                processor.setZipFile(zipFile);
                processor.setIndexingBatch(this.indexingBatch);
                processor.setReIndexing(this.reIndexing);
                processor.setTempFilePath(this.localXtractDir.getAbsolutePath());
                final Executor workerPool = this.cmsConfig.getWorkerPool();
                final CompletableFuture future = CompletableFuture.runAsync(processor, workerPool);
                this.futureList.add(future);
            }
            return;
        });
        Database.withinTx(this.cmsConfig.getDb(), handle -> UploadJobDB.updateUploadJobStatus(handle, this.uploadJob.getUuid(), UploadJob.UploadJobStatus.INDEXING));
    }
    
    public void batchIndex() throws ExecutionException, InterruptedException {
        try {
            final CompletableFuture[] array = this.futureList.toArray(new CompletableFuture[0]);
            CompletableFuture.allOf((CompletableFuture<?>[])array).get(15L, TimeUnit.MINUTES);
            RemoteFileProcessor.LOGGER.info("Starting indexing batch of  %d documents", new Object[] { this.indexingBatch.getList().size() });
            final IndexId questionIndexId = new IndexId(this.uploadJob.getSubject(), "Question");
            final IndexId answerIndexId = new IndexId(this.uploadJob.getSubject(), "Answer");
            final TextIndex textIndex = this.cmsConfig.getTextIndex();
            textIndex.getSubjectIndex(questionIndexId).batchIndex(this.indexingBatch);
            textIndex.getSubjectIndex(answerIndexId).batchIndex(this.indexingBatch);
            RemoteFileProcessor.LOGGER.info("Finished indexing batch of  %d documents", new Object[] { this.indexingBatch.getList().size() });
            final int total = (int)Database.withinTx(this.cmsConfig.getDb(), handle -> {
                final ArrayList<JobFile> jobFiles = new ArrayList<JobFile>();
                this.indexingBatch.getList().forEach(cmd -> {
                    jobFiles.add(cmd.getAnswer().indexed());
                    jobFiles.add(cmd.getQuestion().indexed());
                    return;
                });
                final int[] counts = UploadJobDB.updateBatchJobFileIndexingStatus(handle, jobFiles);
                int _total = 0;
                final int[] array2;
                int i = 0;
                for (int length = array2.length; i < length; ++i) {
                    final int x = array2[i];
                    _total += x;
                }
                UploadJobDB.updateUploadJobStatus(handle, this.uploadJob.getUuid(), UploadJob.UploadJobStatus.INDEXED);
                return Integer.valueOf(_total);
            });
            RemoteFileProcessor.LOGGER.info("Updated files %s", new Object[] { total });
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        catch (final TimeoutException e2) {
            throw new RuntimeException(e2);
        }
    }
    
    public void processImages() {
        final String IMAGE_PREFIX = String.format("%s/%s", this.localXtractDir, "images");
        final Map<String, String> imageJsonFiles = new HashMap<String, String>();
        this.docFiles.keySet().forEach(docId -> {
            try {
                final String json = DocxRelationshipProcessor.createImageMap(ParsingSpec.DocFormat.CMS, this.uploadJob.getUuid(), IMAGE_PREFIX, docId).toJson();
                final String key = String.format("%s/%s-images.json", this.uploadJob.getUuid(), docId);
                RemoteFileProcessor.LOGGER.debug("Uploading Image JSON file for docId = [%s]", new Object[] { docId });
                this.s3Client.putObject(this.cmsConfig.getBucket(), key, json);
                RemoteFileProcessor.LOGGER.debug("Uploaded Image JSON file for docId = [%s]", new Object[] { docId });
                imageJsonFiles.put(docId, key);
            }
            catch (final Exception e) {
                RemoteFileProcessor.LOGGER.error((Throwable)e, "Error while processImages json for %s", new Object[] { docId });
            }
            return;
        });
        Database.doWithinTx(this.cmsConfig.getDb(), handle -> imageJsonFiles.forEach((docId, s3Key) -> {
            final Optional<DocResources> dbDocResources = DocDB.loadResources(handle, docId);
            if (dbDocResources.isPresent()) {
                final DocResources docResources = dbDocResources.get().withImagesS3Key(s3Key);
                DocDB.updateResources(handle, docId, docResources);
            }
        }));
    }
    
    public Map<String, Pair<List<String>, List<String>>> processEquations() {
        final Map<String, String> equationsJsonFiles = new HashMap<String, String>();
        final Map<String, Pair<List<String>, List<String>>> failedEquations = new HashMap<String, Pair<List<String>, List<String>>>();
        try {
            final MathTypeEquationProcessor equationProcessor = new MathTypeEquationProcessor(this.cmsConfig.getCalabashPath(), this.cmsConfig.getJdkPath());
            equationProcessor.setMinMemory(this.cmsConfig.getCalabashMinMemory());
            equationProcessor.setMaxMemory(this.cmsConfig.getCalabashMinMemory());
            final String OLE_BIN_PREFIX = String.format("%s/%s", this.localXtractDir, "olebin");
            final String MML_PREFIX = String.format("%s/%s", this.localXtractDir, "mml");
            equationProcessor.transformEquations(OLE_BIN_PREFIX, MML_PREFIX);
            this.docFiles.keySet().forEach(docId -> {
                try {
                    final GsonObject equationJson = equationProcessor.createEquationJson(MML_PREFIX, docId);
                    final List<String> failedEquations_q = new ArrayList<String>();
                    final List<String> failedEquations_a = new ArrayList<String>();
                    final GsonArray array = equationJson.getArray("qs");
                    for (int i = 0; i < array.size(); ++i) {
                        final GsonObject eq = array.getJson(i);
                        final boolean useImage = eq.optBoolean("useImage", false);
                        if (useImage) {
                            failedEquations_q.add(eq.getString("name"));
                        }
                    }
                    final GsonArray array2 = equationJson.getArray("as");
                    for (int j = 0; j < array2.size(); ++j) {
                        final GsonObject eq2 = array2.getJson(j);
                        final boolean useImage2 = eq2.optBoolean("useImage", false);
                        if (useImage2) {
                            failedEquations_a.add(eq2.getString("name"));
                        }
                    }
                    failedEquations.put(docId, Pair.of((Object)failedEquations_q, (Object)failedEquations_a));
                    final String json = equationJson.toStringPretty();
                    final String key = String.format("%s/%s-equations.json", this.uploadJob.getUuid(), docId);
                    RemoteFileProcessor.LOGGER.debug("Uploading Equations JSON file for docId = %s", new Object[] { docId });
                    this.s3Client.putObject(this.cmsConfig.getBucket(), key, json);
                    equationsJsonFiles.put(docId, key);
                    RemoteFileProcessor.LOGGER.debug("Uploaded Equations JSON file for docId = %s", new Object[] { docId });
                }
                catch (final Exception e2) {
                    RemoteFileProcessor.LOGGER.error((Throwable)e2, "Error while processing equations json for %s", new Object[] { docId });
                }
                return;
            });
            Database.doWithinTx(this.cmsConfig.getDb(), handle -> equationsJsonFiles.forEach((docId, s3Key) -> {
                final Optional<DocResources> dbDocResources = DocDB.loadResources(handle, docId);
                if (dbDocResources.isPresent()) {
                    final DocResources docResources = dbDocResources.get().withEquationS3Key(s3Key);
                    DocDB.updateResources(handle, docId, docResources);
                }
            }));
            RemoteFileProcessor.LOGGER.debug("Failed Equations => %s", new Object[] { failedEquations });
            return failedEquations;
        }
        catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void cleanup() {
        this.s3Client.shutdown();
    }
    
    public void createImagesForFailedEquations(final Map<String, Pair<List<String>, List<String>>> failedEquations) {
        final String imagesDir = String.format("%s/%s", this.localXtractDir, "images");
        this.docFiles.keySet().forEach(docId -> {
            try {
                final Path qPath = Paths.get(this.localXtractDir.getAbsolutePath(), String.format("%s-%s-oleImageMap.json", docId, "Q"));
                final Path aPath = Paths.get(this.localXtractDir.getAbsolutePath(), String.format("%s-%s-oleImageMap.json", docId, "A"));
                new GsonObject(qPath.toFile().exists() ? Files.readString(qPath) : "{}");
                final GsonObject gsonObject;
                final GsonObject qMap = gsonObject;
                new GsonObject(aPath.toFile().exists() ? Files.readString(aPath) : "{}");
                final GsonObject gsonObject2;
                final GsonObject aMap = gsonObject2;
                if (failedEquations.containsKey(docId)) {
                    final Pair<List<String>, List<String>> pair = (Pair<List<String>, List<String>>)failedEquations.get(docId);
                    final List<String> qsImages = (List<String>)pair.getLeft();
                    final List<String> asImages = (List<String>)pair.getRight();
                    RemoteFileProcessor.LOGGER.debug("For %s -> Question Failed Images %s", new Object[] { docId, qsImages });
                    qsImages.forEach(image -> {
                        try {
                            if (qMap.has(image)) {
                                final String mappedImage = String.format("%s-Q-%s", docId, qMap.getString(image));
                                RemoteFileProcessor.LOGGER.debug("Question Mapping %s to %s", new Object[] { image, mappedImage });
                                this.moveImage(mappedImage);
                            }
                            else {
                                RemoteFileProcessor.LOGGER.warn("Cant find image for docId %s", new Object[] { image, docId });
                            }
                        }
                        catch (final Exception e2) {
                            RemoteFileProcessor.LOGGER.error((Throwable)e2, "Error processing image %s for %s", new Object[] { image, docId });
                        }
                        return;
                    });
                    RemoteFileProcessor.LOGGER.debug("For %s -> Answer Failed Images %s", new Object[] { docId, asImages });
                    asImages.forEach(image -> {
                        try {
                            if (aMap.has(image)) {
                                final String mappedImage2 = String.format("%s-A-%s", docId, aMap.getString(image));
                                RemoteFileProcessor.LOGGER.debug("Answer Mapping %s to %s", new Object[] { image, mappedImage2 });
                                this.moveImage(mappedImage2);
                            }
                            else {
                                RemoteFileProcessor.LOGGER.warn("Cant find image for docId %s", new Object[] { image, docId });
                            }
                        }
                        catch (final Exception e3) {
                            RemoteFileProcessor.LOGGER.error((Throwable)e3, "Error processing image %s for %s", new Object[] { image, docId });
                        }
                    });
                }
            }
            catch (final Exception e) {
                RemoteFileProcessor.LOGGER.error((Throwable)e, "Error while createImagesForFailedEquations equations json for %s", new Object[] { docId });
            }
            return;
        });
        this.makeEmfImages();
    }
    
    private void moveImage(final String image) {
        final String imagesDir = String.format("%s/%s", this.localXtractDir, "images");
        final String images2Dir = String.format("%s/%s", this.localXtractDir, "images2");
        final ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
        processBuilder.command("mv", String.format("%s/%s", imagesDir, image), images2Dir);
        RemoteFileProcessor.LOGGER.debug("Moving image %s => %s", new Object[] { image, processBuilder.command() });
        try {
            final int code = processBuilder.start().waitFor();
            RemoteFileProcessor.LOGGER.debug("Moved image %s => %d", new Object[] { image, code });
        }
        catch (final IOException | InterruptedException e) {
            new RuntimeException(e);
        }
    }
    
    private void makeEmfImages() {
        final String images2Dir = String.format("%s/%s", this.localXtractDir, "images2");
        final String imagesDir = String.format("%s/%s", this.localXtractDir, "images");
        ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT);
        processBuilder.directory(this.localXtractDir);
        final String PATH = processBuilder.environment().get("PATH");
        RemoteFileProcessor.LOGGER.debug("environment %s", new Object[] { processBuilder.environment() });
        processBuilder.command("bash", "-c", String.format("%s --headless --convert-to png --outdir %s %s/*.emf", this.SOFFICE_PATH, images2Dir, images2Dir));
        RemoteFileProcessor.LOGGER.debug("Calling LibreOffice input %s => %s", new Object[] { images2Dir, processBuilder.command() });
        try {
            final Process process = processBuilder.start();
            final int code = process.waitFor();
            RemoteFileProcessor.LOGGER.debug("Code = %d Created images %s", new Object[] { code, imagesDir });
        }
        catch (final IOException | InterruptedException e) {
            new RuntimeException(e);
        }
        processBuilder = new ProcessBuilder(new String[0]);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT);
        processBuilder.command("bash", "-c", String.format("%s -trim %s/*.png", this.MOGRIFY_PATH, images2Dir));
        RemoteFileProcessor.LOGGER.debug("Cropping images %s => %s", new Object[] { images2Dir, processBuilder.command() });
        try {
            final int code2 = processBuilder.start().waitFor();
            RemoteFileProcessor.LOGGER.debug("Code = %d Created images %s", new Object[] { code2, images2Dir });
        }
        catch (final IOException | InterruptedException e) {
            new RuntimeException(e);
        }
        final File generatedPngFilesDir = new File(images2Dir);
        File f = null;
        final File[] listFiles = generatedPngFilesDir.listFiles(f -> f.getName().endsWith(".png"));
        for (int length = listFiles.length, i = 0; i < length; ++i) {
            f = listFiles[i];
            final String newName = String.format("%s-emf.png", f.getName().substring(0, f.getName().length() - 4));
            final File newFile = new File(imagesDir, newName);
            RemoteFileProcessor.LOGGER.debug("Renaming file %s to %s", new Object[] { f.getName(), newFile.getAbsolutePath() });
            f.renameTo(newFile);
        }
    }
    
    public boolean hasDocsForProcessing() {
        final AtomicInteger successfulDocs = new AtomicInteger();
        final List<JobFile> files = (List<JobFile>)Database.withinTx(this.cmsConfig.getDb(), handle -> UploadJobDB.listJobFiles(handle, this.uploadJob.getUuid(), this.uploadJob.getTenantKey()));
        files.forEach(jobFile -> {
            RemoteFileProcessor.LOGGER.debug("JobFile --> %s = %s %b %b %b", new Object[] { jobFile.getDocFile(), jobFile.getError(), jobFile.isIndexed(), jobFile.isUploaded(), jobFile.isIndexed() });
            if (jobFile.getError() == null && jobFile.isIndexed() && jobFile.isUploaded() && jobFile.isIndexed()) {
                successfulDocs.incrementAndGet();
            }
            return;
        });
        return successfulDocs.get() > 0;
    }
    
    public void markComplete() {
        Database.doWithinTx(this.cmsConfig.getDb(), handle -> UploadJobDB.updateUploadJobStatus(handle, this.uploadJob.getUuid(), UploadJob.UploadJobStatus.INDEXED_PROCESSED));
        try {
            this.localFile.delete();
            FileUtils.deleteDirectory(this.localXtractDir);
        }
        catch (final IOException e) {
            RemoteFileProcessor.LOGGER.error((Throwable)e, "Error deleting temporary files", new Object[0]);
        }
    }
    
    static {
        LOGGER = MinimaLogger.getLog((Class)RemoteFileProcessor.class);
    }
}
