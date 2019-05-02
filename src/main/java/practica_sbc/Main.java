package practica_sbc;

import java.io.File;

import org.semanticweb.owlapi.formats.RDFJsonLDDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;

import java.util.*;

public class Main {

	static public void main(String... argv) throws OWLOntologyStorageException, OWLOntologyCreationException {
		//Creamos dos managers, uno para crear la ontologia y el otro para guardar la ontologia tras aplicarle el razonador
		OntologyManager manager = new OntologyManager(1);
		OntologyManager outputOntologyManager = new OntologyManager(2);
		
		//Creamos la ontologia
		OntologyCreator ontologyCreator = new OntologyCreator(manager);
		OWLOntology ontology = ontologyCreator.create();
		
//		PrefixManager dbpm = new DefaultPrefixManager("<http://dbpedia.org/ontology/>");
		
		

//		OWLDatatype integerDatatype = factory.getIntegerOWLDatatype();
//
//		OWLDatatypeRestriction minRestriction = factory.getOWLDatatypeMinInclusiveRestriction(2);
//		OWLDataProperty haHechoPeliculas = manager.createDataProperty("haHechoPeliculas", actorClass, integerDatatype);
	
//        OWLDatatype type = factory.getOWLDatatype(XSDVocabulary.G_YEAR.getIRI());

		// Creamos razonador ELK .
		Reasoner elkReasoner = new Reasoner(ontology);
		
		// Clasificamos la ontologia.
		elkReasoner.classifyOntology();
		
		// Generamos axiomas inferenciados
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = elkReasoner.generateInferredAxioms();
		
		// Metemos los axiomas en una nueva ontologia vacia.
		OWLOntology infOnt = outputOntologyManager.getOntology();
		elkReasoner.generateInferredOntology(infOnt, gens,outputOntologyManager.manager );
//		for (OWLAxiom ax : infOnt.getLogicalAxioms()) {
//			System.out.println(ax);
//		}
		
		//Creamos ruta para la ontologia en el directorio actual
		File directory = new File(".");
		String filePath = directory + File.separator + "ontology";

		//Creamos los formatos a exportar
		TurtleDocumentFormat turtleFormat = new TurtleDocumentFormat();
		OWLDocumentFormat ontologyFormat = new RDFJsonLDDocumentFormat();

		//Guardamos la ontologia en los diferentes formatos
		outputOntologyManager.saveOntology(filePath + ".jsonld", ontologyFormat);
		outputOntologyManager.saveOntology(filePath + ".ttl", turtleFormat);
		outputOntologyManager.saveOntology(filePath + ".owl");
		elkReasoner.finishReasonerThreads();
	}
}