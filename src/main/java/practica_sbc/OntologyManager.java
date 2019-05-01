package practica_sbc;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

//Con esta clase controlamos todos los cambios realizados sobre la ontologia
public class OntologyManager {
	// Declaramos las propiedades necesarias para el manejo de la ontologia
	static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	static final OWLDataFactory factory = manager.getOWLDataFactory();
	private OWLOntology ontology;
	private IRI ontologyIRI;
	private PrefixManager pm;

	public OntologyManager(int ontologyNumber) throws OWLOntologyCreationException {
		this.ontologyIRI = IRI.create("http://urjc_sbc.com/owl/" + ontologyNumber + "/");
		this.pm = new DefaultPrefixManager(null, null, this.ontologyIRI.toString());
		this.ontology = manager.createOntology(this.ontologyIRI);
	}

	/**
	 * Crear una clase en la ontologia
	 * 
	 * @param name Nombre de la clase
	 * @return La clase generada
	 */
	public OWLClass addClass(String name) {
		OWLClass cl = factory.getOWLClass(":" + name, this.pm);
		OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(cl);
		manager.addAxiom(this.ontology, declarationAxiom);
		return cl;
	}

	/**
	 * Crear una propiedad entre dos clases
	 * 
	 * @param name        Nombre de la propiedad
	 * @param domainClass Dominio
	 * @param rangeClass  Rango
	 * @return La propiedad generada
	 */
	public OWLObjectProperty createObjectProperty(String name, OWLClass domainClass, OWLClass rangeClass) {
		OWLObjectProperty property = factory.getOWLObjectProperty(":" + name, this.pm);
		OWLObjectPropertyDomainAxiom rangeAxiom = factory.getOWLObjectPropertyDomainAxiom(property, domainClass);
		OWLObjectPropertyRangeAxiom domainAxiom = factory.getOWLObjectPropertyRangeAxiom(property, rangeClass);
		manager.addAxiom(this.ontology, rangeAxiom);
		manager.addAxiom(this.ontology, domainAxiom);
		return property;

	}

	/**
	 * Crear una propiedad con restriccion para una clase
	 * 
	 * @param name        Nombre de la propiedad
	 * @param domainClass Dominio
	 * @param restriction Restriccion
	 * @return La propiedad generada
	 */
	public OWLDataProperty createDataProperty(String name, OWLClass domainClass, OWLDatatypeRestriction restriction) {
		OWLDataProperty property = factory.getOWLDataProperty(":" + name, this.pm);

		OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(property, domainClass);
		OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(property, restriction);
		manager.addAxiom(this.ontology, rangeAxiom);
		manager.addAxiom(this.ontology, domainAxiom);
		return property;
	}

	/**
	 * Crear una propiedad para una clase, con un tipo de dato asignado.
	 * 
	 * @param name        Nombre de la propiedad
	 * @param domainClass Dominio
	 * @param type        Tipo de dato
	 * @return La propiedad generada
	 */
	public OWLDataProperty createDataProperty(String name, OWLClass domainClass, OWLDatatype type) {
		OWLDataProperty property = factory.getOWLDataProperty(":" + name, this.pm);

		OWLDataPropertyDomainAxiom domainAxiom = factory.getOWLDataPropertyDomainAxiom(property, domainClass);
		OWLDataPropertyRangeAxiom rangeAxiom = factory.getOWLDataPropertyRangeAxiom(property, type);
		manager.addAxiom(this.ontology, rangeAxiom);
		manager.addAxiom(this.ontology, domainAxiom);
		return property;
	}

	public void createSubclass(OWLClass hijo, OWLClassExpression padre) {
		OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(hijo, padre);
		manager.addAxiom(this.ontology, ax);
	}

	/**
	 * Definir una relacion de subClases para dos clases recibidas
	 * 
	 * @param c1 Clase hija
	 * @param c2 Clase padre
	 */
	public void createSubclass(OWLClass hijo, OWLClass padre) {
		OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(hijo, padre);
		manager.addAxiom(this.ontology, ax);
	}

	/**
	 * Añadir todos los resultados de una query a la ontologia en un determinada
	 * clase
	 * 
	 * @param results       Resultados de la query
	 * @param ontologyClass Clase a la que añadir
	 * @param field         Campo de la query que referencia el valor de la
	 *                      instancia
	 */
	public void mappingInstances(ResultSet results, OWLClass ontologyClass, String field) {
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			OWLIndividual ind = this.addInstance(ontologyClass, soln.get(field).toString());
			//Cogemos los nombres de todos los campos devueltos
			Iterator<String> it = soln.varNames();
			while (it.hasNext()) {
				String prop = it.next();
				OWLDatatype type =  factory.getIntegerOWLDatatype();
				OWLDataProperty property = this.createDataProperty(prop, ontologyClass, type);
				OWLLiteral value = factory.getOWLLiteral(soln.get(prop).toString());
				this.addDataPropertyToInstance(property, ind, value);			
			}
		}
	}

	/**
	 * Añadir todos los resultados de una query a la ontologia en un determinada
	 * clase y ademas cogiendo otras propiedades del resultado
	 * 
	 * @param results       Resultados de la query
	 * @param ontologyClass Clase a la que añadir
	 * @param field         Campo de la query que referencia el valor de la
	 *                      instancia
	 * @param properties    Conjunto de propiedades cada una con su nombre de la
	 *                      query y su tipo de dato
	 */
//	public void mappingInstancesWithProperties(ResultSet results, OWLClass ontologyClass, String field,
//			ArrayList<Property> properties) {
//		while (results.hasNext()) {
//			QuerySolution soln = results.nextSolution();
//			OWLIndividual ind = this.addInstance(ontologyClass, soln.get(field).toString());
//			for (Property prop : properties) {
//				OWLDataProperty property = this.createDataProperty(prop.getName(), ontologyClass, prop.getType());
//				OWLLiteral value = factory.getOWLLiteral(soln.get(prop.getName()).toString());
//				this.addDataPropertyToInstance(property, ind, value);
//			}
//		}
//	}

	/**
	 * Añadir una instancia de una clase
	 * 
	 * @param ontologyClass Clase a la que pertenecerá la instancia
	 * @param value         Valor de la instancia
	 * @return Instancia creada
	 */
	public OWLIndividual addInstance(OWLClass ontologyClass, String value) {
		OWLIndividual ind = factory.getOWLNamedIndividual(":" + value, this.pm);
		OWLClassAssertionAxiom ax = factory.getOWLClassAssertionAxiom(ontologyClass, ind);
		manager.addAxiom(this.ontology, ax);
		return ind;
	}

	public void addDataPropertyToInstance(OWLDataProperty property, OWLIndividual ind, OWLLiteral value) {
		OWLDataPropertyAssertionAxiom propertyAssertion = factory.getOWLDataPropertyAssertionAxiom(property, ind,
				value);
		manager.addAxiom(this.ontology, propertyAssertion);
	}

	public void addObjectPropertyToInstance(OWLObjectProperty property, OWLIndividual ind, OWLIndividual ind2) {
		OWLObjectPropertyAssertionAxiom propertyAssertion = factory.getOWLObjectPropertyAssertionAxiom(property, ind,
				ind2);
		manager.addAxiom(this.ontology, propertyAssertion);
	}

	/**
	 * Guardar la ontologia en un archivo
	 * 
	 * @param filePath Ruta del archivo en el que guardar
	 * @throws OWLOntologyStorageException
	 */
	public void saveOntology(String filePath) throws OWLOntologyStorageException {
		File file = new File(filePath);

		manager.saveOntology(this.ontology, IRI.create(file.toURI()));

	}

	/**
	 * Guardar ontologia con formato especifico
	 * 
	 * @param filePath Ruta del archivo en el que guardar
	 * @param format   Formato
	 * @throws OWLOntologyStorageException
	 */
	public void saveOntology(String filePath, OWLDocumentFormat format) throws OWLOntologyStorageException {
		File file = new File(filePath);

		manager.saveOntology(this.ontology, format, IRI.create(file.toURI()));

	}

	public IRI getOntologyIRI() {
		return ontologyIRI;
	}

	public void setOntologyIRI(IRI ontologyIRI) {
		this.ontologyIRI = ontologyIRI;
	}

	public PrefixManager getPm() {
		return pm;
	}

	public void setPm(PrefixManager pm) {
		this.pm = pm;
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

}
