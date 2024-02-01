package sgd.batch.loader.step;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;

import sgd.batch.loader.constants.Delimiter;
import sgd.batch.loader.models.Metadata;

public class MetadataFileReader implements ItemReader<Metadata> {

    private final List<File> metadataFiles;
    private final Iterator<File> iterator;

    public MetadataFileReader(String inputDirectory) {
        metadataFiles = getOnlyMetadataFilesFromDirectory(inputDirectory);
        iterator = metadataFiles.iterator();
    }

    private List<File> getOnlyMetadataFilesFromDirectory(String inputDirectory) {
        File rootDirectory = new File(inputDirectory);
        FilenameFilter filter = (dir, name) -> name.endsWith(".load");
        return Arrays.asList(rootDirectory.listFiles(filter));
    }

    @Override
    @Nullable
    public Metadata read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return iterator.hasNext() ? readMetadataFromFile(iterator.next()) : null;
    }

    private Metadata readMetadataFromFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        Metadata metadata = extractMetadataHeaders(lines);
        metadata.setFileRecords(extractFileRecords(lines));
        metadata.setSourceFile(file);
        return metadata;
    }

    private Metadata extractMetadataHeaders(List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío");
        }
        Metadata metadata = new Metadata();
        String firstLine = lines.get(0).trim();
        if (!Metadata.hasValidAction(firstLine)) {
            throw new IllegalArgumentException("Accion inválida");
        }
        String secondLine = lines.get(1).trim().toUpperCase();
        if (!secondLine.matches("^SERIE=[a-zA-Z]+$")) {
            throw new IllegalArgumentException("No se encontró la serie en el archivo");
        }
        metadata.setSerie(secondLine.split("=")[1]);
        String thirdLine = lines.get(2).trim();
        if (!Delimiter.EOD.equals(thirdLine)) {
            throw new IllegalArgumentException("No se encontró el delimitador (<<EOD>>) de inicio de archivo");
        }
        return metadata;
    }

    private List<Map<String, String>> extractFileRecords(List<String> lines) {
        int lineAfterDelimiter = 2;
        int delimiterCounter = 1;
        List<Map<String, String>> fileRecords = new ArrayList<>();
        Map<String, String> fileRecordsContent = new TreeMap<>();
        for (int lineNumber = lineAfterDelimiter; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber).trim();
            if (Delimiter.EOD.equals(line)) {
                delimiterCounter++;
                if (delimiterCounter == Delimiter.MAX_DELIMITER_OCCURRENCES) {
                    fileRecords.add(new TreeMap<>(fileRecordsContent));
                    fileRecordsContent.clear();
                    delimiterCounter = 1;
                }
                continue;
            }
            String[] keyValue = line.split("=");
            fileRecordsContent.put(keyValue[0], keyValue[1]);
        }
        if (fileRecords.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron registros en el archivo");
        }
        return fileRecords;
    }

}
