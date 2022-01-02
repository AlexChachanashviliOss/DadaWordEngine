package io.github.achacha.dada.tools;

import io.github.achacha.dada.engine.data.WordData;
import io.github.achacha.dada.engine.data.WordsByType;
import org.apache.commons.cli.CommandLine;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DedupActionHandler implements BaseActionHandler {
    @Override
    public String getHelp() {
        return "REQUIRES: -dataset, -outpath";
    }

    /**
     * Remove duplicates and save to output location
     *
     * @param cmd CommandLine with parameters
     * @param out PrintStream for output
     */
    @Override
    public void handle(CommandLine cmd, PrintStream out) {
        String dataset = cmd.getOptionValue("dataset");
        if (dataset == null) {
            out.println("-dataset is required");
            return;
        }
        String basePathString = cmd.getOptionValue("outpath");
        if (basePathString == null) {
            out.println("-outpath is required");
            return;
        }
        Path basePath = Paths.get(basePathString);

        WordData wordData = new WordData("resource:/data/" + dataset);
        out.println("Processing dataset:  resource:/data/" + dataset);
        wordData.getWordsByTypeStream().filter(WordsByType::isDuplicateFound).forEach(byType -> {
            out.println("Duplicates detected, de-duping: " + byType.getType());
            Path outfile = basePath.resolve(byType.getType().getTypeName() + ".csv");
            try (
                    FileOutputStream fos = new FileOutputStream(outfile.toFile());
                    OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.defaultCharset());
                    BufferedWriter writer = new BufferedWriter(osw)
            ) {
                byType.writeWords(writer);
            } catch (IOException e) {
                e.printStackTrace(out);
            }
        });
    }
}
