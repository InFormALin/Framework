/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.java.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import edu.kit.kastel.informalin.framework.models.java.JavaProject;
import edu.kit.kastel.informalin.framework.models.java.extractor.output.OntologyWriter;
import edu.kit.kastel.informalin.framework.models.java.extractor.visitors.JavaFileVisitor;
import org.apache.commons.cli.*;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Keim
 */
public class JavaCodeModelExtractor {
    private static final Logger logger = LoggerFactory.getLogger(JavaCodeModelExtractor.class);

    private static final String CMD_HELP = "h";
    private static final String CMD_IN_DIR = "i";
    private static final String CMD_OUT_DIR = "o";
    private static final String CMD_IN_OWL = "e";
    private static final String CMD_OUT_TYPE = "t";

    private enum Output {
        JSON, OWL
    }

    private static Options options;

    private JavaCodeModelExtractor() {
        throw new IllegalAccessError();
    }

    public static void main(String[] args) {
        CommandLine cmd;
        try {
            cmd = parseCommandLine(args);
        } catch (IllegalArgumentException | ParseException e) {
            logger.error(e.getMessage());
            printUsage();
            return;
        }

        if (cmd.hasOption(CMD_HELP)) {
            printUsage();
            return;
        }

        Path inputDir;
        File outputFile;
        File extendFile;
        inputDir = ensureDir(cmd.getOptionValue(CMD_IN_DIR));
        outputFile = new File(cmd.getOptionValue(CMD_OUT_DIR));
        extendFile = cmd.hasOption(CMD_IN_OWL) ? new File(cmd.getOptionValue(CMD_IN_OWL)) : null;
        Output out = Output.valueOf(cmd.getOptionValue(CMD_OUT_TYPE));

        runExtraction(inputDir, outputFile, extendFile, out);
    }

    private static void runExtraction(Path startingDir, File outputFile, File extendFile, Output out) {
        logger.info("Start extracting \"{}\".", startingDir);
        var javaFileVisitor = new JavaFileVisitor();
        // walk all files and run the JavaFileVisitor
        try {
            Files.walkFileTree(startingDir, javaFileVisitor);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e.getCause());
        }
        // afterwards, process information and save them
        processAndSaveInformation(javaFileVisitor.getProject(), outputFile, extendFile, out);
    }

    private static void processAndSaveInformation(JavaProject javaProject, File outputFile, File extendFile, Output out) {
        // process
        // no process for now
        if (logger.isInfoEnabled()) {
            var numClasses = javaProject.getClassesAndInterfaces().size();
            logger.info("Extraction finished with {} extracted classes and interfaces.", numClasses);
        }

        // finally, save the information
        if (out == Output.OWL)
            saveOntology(javaProject, outputFile, extendFile);
        else
            saveToJSON(javaProject, outputFile);

    }

    private static void saveToJSON(JavaProject javaProject, File outputFile) {
        ObjectMapper oom = new ObjectMapper();
        oom.setVisibility(oom.getSerializationConfig()
                .getDefaultVisibilityChecker() //
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)//
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)//
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)//
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

        try {
            oom.writeValue(outputFile, javaProject);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void saveOntology(JavaProject project, File outputFile, File extendFile) {
        logger.info("Writing to ontology");
        OntologyWriter writer;
        if (extendFile == null) {
            writer = OntologyWriter.withEmptyOntology(outputFile);
        } else {
            logger.info("Extending existing ontology at \"{}\"", extendFile);
            writer = OntologyWriter.extendExistingOntology(extendFile, outputFile);
        }

        writer.write(project);
        logger.info("Finished saving. Exiting now...");
    }

    private static void printUsage() {
        var formatter = new HelpFormatter();
        formatter.printHelp("JavaCodeModelExtractor.jar", options);
    }

    private static CommandLine parseCommandLine(String[] args) throws ParseException {
        options = new Options();
        Option opt;

        // Define Options ..
        opt = new Option(CMD_HELP, "help", false, "print this message");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option(CMD_IN_DIR, "in", true, "path to the input directory");
        opt.setRequired(true);
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option(CMD_IN_OWL, "extend", true, "path to the owl file that should be extended (instead of creating from empty)");
        opt.setRequired(false);
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option(CMD_OUT_DIR, "out", true, "path to the output file that should be used for saving");
        opt.setRequired(true);
        opt.setType(String.class);
        options.addOption(opt);

        opt = new Option(CMD_OUT_TYPE, "output-format", true, "specified output format: one of " + Arrays.toString(Output.values()));
        opt.setRequired(true);
        opt.setType(Output.class);
        options.addOption(opt);

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);

    }

    /**
     * Ensure that a directory exists (or create).
     *
     * @param path the path to the file
     * @return the file
     */
    private static Path ensureDir(String path) {
        var file = new File(path);
        if (file.isDirectory() && file.exists()) {
            return Paths.get(file.toURI());
        }

        file.mkdirs();
        return Paths.get(file.toURI());

    }

}
