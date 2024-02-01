package sgd.batch.loader.models;

import java.io.File;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sgd.batch.loader.constants.Action;

@Data
public class Metadata {

    private File sourceFile;
    private String serie;
    private List<Map<String, String>> fileRecords;

    public static boolean hasValidAction(String actionLine) {
        String actionKeyValue = actionLine.split("=")[1];
        return Action.INSERT.equalsIgnoreCase(actionKeyValue);
    }

    @Override
    public String toString() {
        String format = "Metadata [sourceFile=%s, serie=%s, fileRecords=%s]";
        return String.format(format, sourceFile, serie, fileRecords);
    }

}
