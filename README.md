# Code Model Extractor Cli
A small cli that can generate code and architecture model files for ArDoCo.

## Install
Run `mvn package`

## Usage

### Code model

Run `java -jar code-model-extractor.jar code <directory> <destination_file>`

E.g., `java -jar target/code-model-extractor.jar code ./src/main/java model.json`

### Architecture model

Run `java -jar code-model-extractor.jar architecture <uml|pcm|component_listing> <model_file> <destination_file>`

E.g., `java -jar target/code-model-extractor.jar architecture uml ./model.uml architecture.json`
