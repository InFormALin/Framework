/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.models.java.extractor.output;

import java.io.File;

import edu.kit.kastel.informalin.framework.models.java.CodeComment;
import edu.kit.kastel.informalin.framework.models.java.JavaClassOrInterface;
import edu.kit.kastel.informalin.framework.models.java.JavaMethod;
import edu.kit.kastel.informalin.framework.models.java.JavaProject;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.kit.kastel.informalin.ontology.OntologyConnector;
import edu.kit.kastel.informalin.ontology.OntologyInterface;

/**
 * @author Jan Keim
 *
 */
// TODO ontology and writer currently do not map methods and comments to class etc.
public class OntologyWriter {
    private static final Logger logger = LogManager.getLogger(OntologyWriter.class);

    private static final String JAVA_BASE_OWL = "https://informalin.github.io/knowledgebases/informalin_base_java.owl";
    private static final String DEFAULT_NAME_SPACE_URI = "https://informalin.github.io/knowledgebases/examples/java-example.owl#";

    private OntClass classOrInterfaceOntClass;
    private OntClass methodOntClass;
    private OntClass codeCommentOntClass;
    private OntProperty javadocContentProperty;
    private OntProperty typeProperty;
    private OntProperty textProperty;
    private OntProperty nameProperty;
    private OntProperty fqnProperty;
    private OntProperty isInterfaceProperty;

    private OntologyInterface ontology = null;
    private String outputFile;

    private OntologyWriter(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Write to a new, empty ontology
     *
     * @param outputFile path of the folder where to save to
     * @return the OntologyWriter
     */
    public static OntologyWriter withEmptyOntology(File outputFile) {
        var ow = new OntologyWriter(outputFile.toString());
        ow.ontology = OntologyConnector.createWithEmptyOntology(DEFAULT_NAME_SPACE_URI);
        return ow;
    }

    /**
     * Write to an existing ontology and extend it.
     *
     * @param existingOntology the path to the existing ontology
     * @param outputFile       the path of the folder where to save to
     * @return the OntologyWriter
     */
    public static OntologyWriter extendExistingOntology(File existingOntology, File outputFile) {
        var ow = new OntologyWriter(outputFile.toString());
        ow.ontology = new OntologyConnector(existingOntology.toString());
        return ow;
    }

    private void init() {
        logger.info("Init writing to ontology");
        if (ontology == null) {
            ontology = OntologyConnector.createWithEmptyOntology(DEFAULT_NAME_SPACE_URI);
        }

        ontology.addOntologyImport(JAVA_BASE_OWL);
        classOrInterfaceOntClass = ontology
                .getClassByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#OWLClass_5c834f48_ae0d_40d8_8ea1_c193dc511593")
                .orElseThrow();
        methodOntClass = ontology
                .getClassByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#OWLClass_baf2a951_c26b_479c_8995_f1439a95aa2f")
                .orElseThrow();
        codeCommentOntClass = ontology
                .getClassByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#OWLClass_fcaa349b_0dde_4b4d_bab3_7971faad212a")
                .orElseThrow();
        javadocContentProperty = ontology.getPropertyByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#javadocContent").orElseThrow();
        typeProperty = ontology.getPropertyByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#type").orElseThrow();
        textProperty = ontology.getPropertyByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#text").orElseThrow();
        nameProperty = ontology.getPropertyByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#name").orElseThrow();
        fqnProperty = ontology.getPropertyByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#fullyQualifiedName").orElseThrow();
        isInterfaceProperty = ontology.getPropertyByIri("https://informalin.github.io/knowledgebases/informalin_base_java.owl#isInterface").orElseThrow();
    }

    public void write(JavaProject project) {
        init();

        logger.info("Start actual writing");
        for (var classOrInterface : project.getClassesAndInterfaces()) {
            writeClassOrInterface(classOrInterface);
        }

        logger.info("Start saving to file \"{}\"", outputFile);
        ontology.save(outputFile);
    }

    private void writeClassOrInterface(JavaClassOrInterface classOrInterface) {
        // create Individual
        var classOrInterfaceName = classOrInterface.getName();
        var individual = ontology.addIndividualToClass(classOrInterfaceName, classOrInterfaceOntClass);

        // add data properties (name, fqn)
        ontology.addPropertyToIndividual(individual, nameProperty, classOrInterfaceName);
        ontology.addPropertyToIndividual(individual, fqnProperty, classOrInterface.getFullyQualifiedName());
        ontology.addPropertyToIndividual(individual, isInterfaceProperty, classOrInterface.isInterface());

        // go over methods
        for (var method : classOrInterface.getAllMethods()) {
            writeMethod(method);
        }

        // go over comments
        for (var comment : classOrInterface.getAllComments()) {
            writeComment(comment, classOrInterface);
        }
    }

    private void writeMethod(JavaMethod method) {
        // create Individual
        var methodName = method.getName();
        var individual = ontology.addIndividualToClass(methodName, methodOntClass);

        // add data properties (name, type, javadocContent)
        ontology.addPropertyToIndividual(individual, nameProperty, methodName);
        ontology.addPropertyToIndividual(individual, typeProperty, method.getType());
        var javadocContent = method.getJavadocContent();
        if (javadocContent != null) {
            ontology.addPropertyToIndividual(individual, javadocContentProperty, javadocContent);
        }

    }

    private void writeComment(CodeComment comment, JavaClassOrInterface parent) {
        // create Individual
        var name = "comment_" + parent.getName() + "_L" + comment.getLineNumber();
        var individual = ontology.addIndividualToClass(name, codeCommentOntClass);

        // add data property (text)
        ontology.addPropertyToIndividual(individual, textProperty, comment.getText());
    }

}
