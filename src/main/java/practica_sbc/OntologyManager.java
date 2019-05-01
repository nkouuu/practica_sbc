package practica_sbc;

import java.io.File;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class OntologyManager {
	static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	static final OWLDataFactory factory = manager.getOWLDataFactory();
	private OWLOntology ontology;
	private IRI ontologyIRI;
	private PrefixManager pm;

	public OntologyManager(int ontologyNumber) throws OWLOntologyCreationException {
		this.ontologyIRI = IRI.create("http://urjc_sbc.com/owl/"+ontologyNumber);
		this.pm = new DefaultPrefixManager(null, null, this.ontologyIRI.toString());
		this.ontology = manager.createOntology(this.ontologyIRI);
	}

	public OWLClass addClass(String name) {
		OWLClass cl = factory.getOWLClass(name, this.pm);
		OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(cl);
		manager.addAxiom(this.ontology, declarationAxiom);
		return cl;
	}

	// Para crear ObjectProperties entre dos clases
	public OWLObjectProperty createObjectProperty(String name, OWLClass domainClass, OWLClass rangeClass) {
		OWLObjectProperty property = factory.getOWLObjectProperty(":" + name, this.pm);
		OWLObjectPropertyDomainAxiom rangeAxiom = factory.getOWLObjectPropertyDomainAxiom(property, domainClass);
		OWLObjectPropertyRangeAxiom domainAxiom = factory.getOWLObjectPropertyRangeAxiom(property, rangeClass);
		manager.addAxiom(this.ontology, rangeAxiom);
		manager.addAxiom(this.ontology, domainAxiom);
		return property;

	}

	// Para crear DataProperties con restriccion
	public OWLDataProperty createDataProperty(String name, OWLClass domainClass, OWLDatatypeRestriction restriction) {
		OWLDataProperty property = factory.getOWLDataProperty(":" + name, this.pm);

		OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(property, domainClass);
		OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(property, restriction);
		manager.addAxiom(this.ontology, rangeAxiom);
		manager.addAxiom(this.ontology, domainAxiom);
		return property;
	}

	// Para crear DataProperties sin restriccion
	public OWLDataProperty createDataProperty(String name, OWLClass domainClass, OWLDatatype type) {
		OWLDataProperty property = factory.getOWLDataProperty(":" + name, this.pm);

		OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(property, domainClass);
		OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(property, type);
		manager.addAxiom(this.ontology, rangeAxiom);
		manager.addAxiom(this.ontology, domainAxiom);
		return property;
	}

	public void createSubclass(OWLClass c1, OWLClassExpression c2) {
		OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(c1, c2);
		manager.addAxiom(this.ontology, ax);
	}

	public void createSubclass(OWLClass c1, OWLClass c2) {
		OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(c1, c2);
		manager.addAxiom(this.ontology, ax);
	}

	public void mappingInstances(ResultSet results, OWLClass ontologyClass, String field) {
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			OWLIndividual ind = factory.getOWLNamedIndividual(soln.get(field).toString());
			OWLClassAssertionAxiom ax = factory.getOWLClassAssertionAxiom(ontologyClass, ind);
			manager.addAxiom(this.ontology, ax);
		}
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	public static OWLOntologyManager getManager() {
		return manager;
	}

	public static OWLDataFactory getFactory() {
		return factory;
	}

	public void saveOntology(String filePath) throws OWLOntologyStorageException {
		File file = new File(filePath);

		manager.saveOntology(this.ontology, IRI.create(file.toURI()));

	}

	public void saveOntology(String filePath, OWLDocumentFormat format) throws OWLOntologyStorageException {
		File file = new File(filePath);

		manager.saveOntology(this.ontology, format, IRI.create(file.toURI()));

	}
}
