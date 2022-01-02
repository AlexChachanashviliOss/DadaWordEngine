package io.github.achacha.dada.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class WordDataConsole {

    private static final Options OPTIONS = buildOptions();

    private static final Map<String, Class<? extends BaseActionHandler>> ACTION_HANDLERS = Map.of(
            "verify", VerifyDatasetActionHandler.class,
            "dedup", DedupActionHandler.class,
            "randomXform", PhonemixActionHandler.class
    );

    public static void main(String[] args) {
        if (args.length < 1) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("WordDataConsole", OPTIONS);
            ACTION_HANDLERS.forEach((key, value) -> {
                        try {
                            BaseActionHandler bah = value.getConstructor().newInstance();
                            System.out.println(key + ": " + bah.getHelp());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
            );
            return;
        }

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(OPTIONS, args);
        } catch (ParseException e) {
            System.out.println("Failed to parse command line args="+ Arrays.toString(args));
            e.printStackTrace();
            return;
        }

        String action = cmd.getOptionValue("action");
        if (StringUtils.isNotBlank(action)) {
            Class<? extends BaseActionHandler> handlerClass = ACTION_HANDLERS.get(action);
            try {
                BaseActionHandler handler = handlerClass.getConstructor().newInstance();
                handler.handle(cmd, System.out);
            } catch (InstantiationException | IllegalAccessException e) {
                System.err.println("Failed to create new instance of the handle class="+handlerClass);
                e.printStackTrace();
            } catch (NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(
                Option.builder()
                        .argName("action")
                        .required()
                        .longOpt("action")
                        .hasArg(true)
                        .desc("Action of this operation")
                        .build()
        );
        options.addOption(
                Option.builder()
                        .argName("dataset")
                        .longOpt("dataset")
                        .hasArg(true)
                        .desc("Data set name [e.g. default, extended, dada2018, etc]")
                        .build()
        );
        options.addOption(
                Option.builder()
                        .argName("outpath")
                        .longOpt("outpath")
                        .hasArg(true)
                        .desc("Output path where result files get written")
                        .build()
        );
        options.addOption(
                Option.builder()
                        .argName("phonemix")
                        .longOpt("phonemix")
                        .hasArg(true)
                        .desc("Phonemix to use [e.g. compacting, aggressive, enhanced]")
                        .build()
        );

        return options;
    }
}
