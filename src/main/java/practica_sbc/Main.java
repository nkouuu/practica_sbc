package practica_sbc;

import java.io.File;

import org.apache.jena.query.*;
import org.semanticweb.owlapi.formats.RDFJsonLDDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import java.util.*;

public class Main {

	//Clases
	static OntologyManager manager;
	static OntologyManager outputOntologyManager;
	static OWLClass actorClass;
	static OWLClass peliculaClass;
	static OWLClass paisClass;
	static OWLClass actorFamosoClass;
	
	//Propiedades
	static OWLObjectProperty actuaEn;
	static OWLObjectProperty premioPor ;
	static OWLObjectProperty grabadaEn ;
	static OWLObjectProperty haActuadoEn ;
	static OWLObjectProperty haTriunfadoGraciasA;
	
	// Queries
	static String dbpedia = "http://dbpedia.org/sparql";
	static String wikidata = "https://query.wikidata.org/sparql";
	static String mondial = "http://servolis.irisa.fr/mondial/sparql";
	static String actoresQuery = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT ?Actor_1 ?nombre \n"
			+ "WHERE { ?Actor_1 a dbo:Actor .\n" + "?Actor_1 foaf:name ?nombre."
			+ "        FILTER ( NOT EXISTS { ?Actor_1 dbo:activeYearsEndYear ?activeYearsEndYear_21 . } ) }\n"
			+ "LIMIT 200";
	static String countryQuery = "PREFIX n1: <http://www.semwebtech.org/mondial/10/meta#>"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT *" + "WHERE { ?country a n1:Country ."
			+ " ?country n1:name ?name_21 ." + "}" + "LIMIT 200";
	static String filmQueryString = "PREFIX wd: <http://www.wikidata.org/entity/>"
			+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>" + "PREFIX wikibase: <http://wikiba.se/ontology#>"
			+ "PREFIX p: <http://www.wikidata.org/prop/>" + "PREFIX v: <http://www.wikidata.org/prop/statement/>"
			+ "PREFIX q: <http://www.wikidata.org/prop/qualifier/>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "SELECT DISTINCT ?q ?film_title ?actor ?genre"
			+ "WHERE {" + " ?q wdt:P31 wd:Q11424." + "?q rdfs:label ?film_title filter (lang(?film_title) = \"en\")."
			+ "?q wdt:P136 ?genreID." + "?genreID rdfs:label ?genre filter (lang(?genre) = \"en\")."
			+ "?q wdt:P161 ?actorID." + " ?actorID rdfs:label ?actor filter (lang(?actor) = \"en\")." + "}limit 200";

	// Devolver resultados de una query
	static public ResultSet getData(String queryString, String service) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query); // Query
		ResultSet results = qexec.execSelect();
		return results;
	}

	static public void addClases() {
		actorClass = manager.addClass("Actor");
		peliculaClass = manager.addClass("Pelicula");
		paisClass = manager.addClass("Pais");
		actorFamosoClass = manager.addClass("ActorFamoso");
		manager.createSubclass(actorFamosoClass, actorClass);

	}

	// Añadir Propiedades
	static public void addProperties() {
		 actuaEn = manager.createObjectProperty("actuaEn", actorClass, peliculaClass);
		 premioPor = manager.createObjectProperty("premioPor", actorClass, peliculaClass);
		 grabadaEn = manager.createObjectProperty("grabadaEn", peliculaClass, paisClass);
		 haActuadoEn = manager.createObjectProperty("haActuadoEn", actorClass, paisClass);
		 haTriunfadoGraciasA = manager.createObjectProperty("haTriunfadoGraciasA", peliculaClass, actorClass);
	}

	static public String formatString(String stringToFormat) {
		return stringToFormat.split("@en")[0];
	}

	static public void main(String... argv) throws OWLOntologyStorageException, OWLOntologyCreationException {
		File directory = new File(".");
		manager = new OntologyManager(1);
		outputOntologyManager = new OntologyManager(2);
		OWLDataFactory factory = manager.getFactory();
		OWLOntology ontology = manager.getOntology();
		PrefixManager dbpm = new DefaultPrefixManager("<http://dbpedia.org/ontology/>");
		addClases();
		addProperties();

		OWLDatatype integerDatatype = factory.getIntegerOWLDatatype();

		OWLDatatypeRestriction minRestriction = factory.getOWLDatatypeMinInclusiveRestriction(2);
		OWLDataProperty haHechoPeliculas = manager.createDataProperty("haHechoPeliculas", actorClass, integerDatatype);
		manager.createDataProperty("haHechoPeliculas", actorFamosoClass, minRestriction);
//		manager.createDataProperty("comenzoATrabajarComoAdulto", actorClass, minRestriction);
		
//		Añadir propiedad a instancia
		OWLIndividual act = factory.getOWLNamedIndividual(":"+"Actor de prueba",manager.getPm());
		OWLClassAssertionAxiom ax1 = factory.getOWLClassAssertionAxiom(actorClass, act);
//		OWLIndividual peli = factory.getOWLNamedIndividual(":"+"Pelicula de prueba",manager.getPm());
//		OWLClassAssertionAxiom ax2 = factory.getOWLClassAssertionAxiom(actorClass, peli);
		manager.getManager().addAxiom(manager.getOntology(), ax1);
//		manager.getManager().addAxiom(manager.getOntology(), ax2);
		OWLDataPropertyAssertionAxiom propertyAssertion = factory.getOWLDataPropertyAssertionAxiom(haHechoPeliculas, act,4);
	    manager.getManager().addAxiom(ontology, propertyAssertion);

//        OWLClassExpression hasPartSomeNose = factory.getOWLObjectSomeValuesFrom(hasPart, nose);

//		OWLDataRange intGreaterThan2 = factory.getOWLDatatypeMinInclusiveRestriction(2);
//		OWLClassExpression actorFamoso = factory.getOWLDataSomeValuesFrom(haHechoPeliculas, intGreaterThan2);

		ResultSet actores = getData(actoresQuery, dbpedia);
		ResultSet peliculas = getData(filmQueryString, wikidata);
		ResultSet paises = getData(countryQuery, mondial);
		manager.mappingInstances(actores, actorClass, "Actor_1");
		manager.mappingInstances(peliculas, peliculaClass, "q");
		manager.mappingInstances(paises, paisClass, "country");
		
		// Create an ELK reasoner.
		Reasoner elkReasoner = new Reasoner(ontology);
		// Classify the ontology.
		elkReasoner.classifyOntology();
		// To generate an inferred ontology we use implementations of
		// inferred axiom generators
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = elkReasoner.generateInferredAxioms();
		
		// Put the inferred axioms into a fresh empty ontology.
		OWLOntology infOnt = outputOntologyManager.getOntology();
		elkReasoner.generateInferredOntology(infOnt, gens,outputOntologyManager.manager );
		for (OWLAxiom ax : infOnt.getLogicalAxioms()) {
			System.out.println(ax);
		}
		String filePath = directory + File.separator + "ontology";

		TurtleDocumentFormat turtleFormat = new TurtleDocumentFormat();
		OWLDocumentFormat ontologyFormat = new RDFJsonLDDocumentFormat();

		manager.saveOntology(filePath + ".jsonld", ontologyFormat);
		manager.saveOntology(filePath + ".ttl", turtleFormat);
		manager.saveOntology(filePath + ".owl");

	}
}