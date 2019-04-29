package practica_sbc;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class OntologyCreator {
	static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	static final OWLDataFactory factory = manager.getOWLDataFactory();

	public OntologyCreator() {}
	public 	OntologyCreator(String actoreURI,String peliculasURI,String paisesURI) {
		
	}
	
	public static void saveOntology() throws OWLOntologyStorageException, OWLOntologyCreationException {
		// Get hold of an ontology manager
//		OWLOntology ontology = load(manager);
		// Now save a local copy of the ontology. (Specify a path appropriate to
		// your setup)
		File directory = new File(".");
		File file = new File(directory + File.separator + "ontology.owl");
//		manager.saveOntology(ontology, IRI.create(file.toURI()));
	}
	
}
