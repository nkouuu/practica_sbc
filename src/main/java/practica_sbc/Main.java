package practica_sbc;

import java.io.File;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;

import java.util.*;

public class Main {

	static public void main(String... argv) throws OWLOntologyStorageException, OWLOntologyCreationException {
		/*Creamos dos managers, uno para crear la ontologia y el otro para guardar la
		  ontologia tras aplicarle el razonador*/
		OntologyManager manager = new OntologyManager(1);
		OntologyManager outputOntologyManager = new OntologyManager(2);

		// Creamos la ontologia
		OntologyCreator ontologyCreator = new OntologyCreator(manager);
		OWLOntology ontology = ontologyCreator.create();

//        OWLDatatype type = factory.getOWLDatatype(XSDVocabulary.G_YEAR.getIRI());

		// Creamos razonador ELK .
		
		Reasoner elkReasoner = new Reasoner(ontology);

		if (elkReasoner.isConsistent()) {
			System.out.println("--> La ontologia  es consistente.\n--> Razonando...");
			// Clasificamos la ontologia.
			elkReasoner.classifyOntology();

			// Generamos axiomas inferenciados
			List<InferredAxiomGenerator<? extends OWLAxiom>> gens = elkReasoner.generateInferredAxioms();

			// Metemos los axiomas en una nueva ontologia vacia.
			OWLOntology infOnt = outputOntologyManager.getOntology();
			elkReasoner.generateInferredOntology(infOnt, gens, outputOntologyManager.manager);

			// Creamos ruta para la ontologia en el directorio actual
			File directory = new File(".");
			String path = directory + File.separator + "ontology";
			String inferedPath = directory + File.separator + "inferredOntology";

			// Guardamos la ontologia procesada y sin procesas en los diferentes formatos
			outputOntologyManager.saveOntologyAllFormats(inferedPath);
			manager.saveOntologyAllFormats(path);
			
			elkReasoner.finishReasonerThreads();
			System.out.println("--> Proceso terminado.");
		} else {
			System.out.println("--> La ontologia  no es consistente. Modifícala y prueba otra vez.");
		}

	}
}