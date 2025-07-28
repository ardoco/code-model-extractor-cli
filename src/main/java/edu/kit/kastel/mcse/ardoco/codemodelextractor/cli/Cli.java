/* Licensed under MIT 2025. */
package edu.kit.kastel.mcse.ardoco.codemodelextractor.cli;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.kastel.mcse.ardoco.core.api.models.CodeModel;
import edu.kit.kastel.mcse.ardoco.core.api.models.Metamodel;
import edu.kit.kastel.mcse.ardoco.core.api.models.code.CodeItemRepository;
import edu.kit.kastel.mcse.ardoco.tlr.models.connectors.generators.code.AllLanguagesExtractor;

public final class Cli {
    private static final Logger logger = LoggerFactory.getLogger(Cli.class);

    private Cli() {
        throw new IllegalAccessError("Utility Class");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
        }
        File codeDirectory = new File(args[0]);
        if (!codeDirectory.isDirectory()) {
            logger.error("Provided path {} is no directory", args[0]);
            printUsage();
        }
        File destination = new File(args[1]);
        if (destination.exists() && !destination.isFile()) {
            logger.error("Destination is not a valid file: {}", args[1]);
            printUsage();
        }

        CodeItemRepository codeItemRepository = new CodeItemRepository();
        AllLanguagesExtractor codeExtractor = new AllLanguagesExtractor(codeItemRepository, codeDirectory.getAbsolutePath(),
                Metamodel.CODE_WITH_COMPILATION_UNITS_AND_PACKAGES);
        CodeModel model = codeExtractor.extractModel();
        codeExtractor.writeOutCodeModel(model, destination);
    }

    private static void printUsage() {
        logger.info("Usage: java -jar code-model-extractor.jar <<directory>> <<destination_file>>");
        System.exit(1);
    }
}
