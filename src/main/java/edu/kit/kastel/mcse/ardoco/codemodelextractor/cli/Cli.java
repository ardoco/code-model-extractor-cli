/* Licensed under MIT 2025-2026. */
package edu.kit.kastel.mcse.ardoco.codemodelextractor.cli;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.kit.kastel.mcse.ardoco.core.api.models.ArchitectureModel;
import edu.kit.kastel.mcse.ardoco.core.api.models.CodeModel;
import edu.kit.kastel.mcse.ardoco.core.api.models.Metamodel;
import edu.kit.kastel.mcse.ardoco.core.api.models.code.CodeItemRepository;
import edu.kit.kastel.mcse.ardoco.tlr.models.connectors.generators.architecture.ArchitectureExtractor;
import edu.kit.kastel.mcse.ardoco.tlr.models.connectors.generators.architecture.listing.ComponentListingArchitectureExtractor;
import edu.kit.kastel.mcse.ardoco.tlr.models.connectors.generators.architecture.pcm.PcmExtractor;
import edu.kit.kastel.mcse.ardoco.tlr.models.connectors.generators.architecture.uml.UmlExtractor;
import edu.kit.kastel.mcse.ardoco.tlr.models.connectors.generators.code.AllLanguagesExtractor;

public final class Cli {
    private static final Logger logger = LoggerFactory.getLogger(Cli.class);

    private Cli() {
        throw new IllegalAccessError("Utility Class");
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
        }
        switch (args[0]) {
            case "code" -> runCode(args);
            case "architecture" -> runArchitecture(args);
            default -> {
                logger.error("Unknown mode: {}", args[0]);
                printUsage();
            }
        }
    }

    private static void runCode(String[] args) {
        if (args.length != 3) {
            printUsage();
        }
        File codeDirectory = new File(args[1]);
        if (!codeDirectory.isDirectory()) {
            logger.error("Provided path {} is no directory", args[1]);
            printUsage();
        }
        File destination = new File(args[2]);
        if (destination.exists() && !destination.isFile()) {
            logger.error("Destination is not a valid file: {}", args[2]);
            printUsage();
        }

        CodeItemRepository codeItemRepository = new CodeItemRepository();
        AllLanguagesExtractor codeExtractor = new AllLanguagesExtractor(codeItemRepository, codeDirectory.getAbsolutePath(),
                Metamodel.CODE_WITH_COMPILATION_UNITS_AND_PACKAGES);
        CodeModel model = codeExtractor.extractModel();
        codeExtractor.writeOutCodeModel(model, destination);
    }

    private static void runArchitecture(String[] args) {
        if (args.length != 4) {
            printUsage();
        }
        String format = args[1];
        File modelFile = new File(args[2]);
        if (!modelFile.isFile()) {
            logger.error("Provided path {} is not a file", args[2]);
            printUsage();
        }
        File destination = new File(args[3]);
        if (destination.exists() && !destination.isFile()) {
            logger.error("Destination is not a valid file: {}", args[3]);
            printUsage();
        }

        ArchitectureExtractor extractor = switch (format) {
            case "uml" -> new UmlExtractor(modelFile.getAbsolutePath(), Metamodel.ARCHITECTURE_WITH_COMPONENTS_AND_INTERFACES);
            case "pcm" -> new PcmExtractor(modelFile.getAbsolutePath(), Metamodel.ARCHITECTURE_WITH_COMPONENTS_AND_INTERFACES);
            case "component_listing" -> new ComponentListingArchitectureExtractor(modelFile.getAbsolutePath(),
                    Metamodel.ARCHITECTURE_WITH_COMPONENTS_AND_INTERFACES);
            default -> {
                logger.error("Unknown architecture format: {}. Supported: uml, pcm, component_listing", format);
                printUsage();
                yield null;
            }
        };

        ArchitectureModel model = extractor.extractModel();
        extractor.writeOutArchitectureModel(model, destination);
    }

    private static void printUsage() {
        logger.info("Usage:");
        logger.info("  java -jar code-model-extractor.jar code <directory> <destination_file>");
        logger.info("  java -jar code-model-extractor.jar architecture <uml|pcm|component_listing> <model_file> <destination_file>");
        System.exit(1);
    }
}
