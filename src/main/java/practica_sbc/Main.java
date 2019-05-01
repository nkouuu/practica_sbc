package practica_sbc;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFJsonLDDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
import org.semanticweb.owlapi.vocab.OWLFacet;

import java.util.*;

public class Main {

	//Clases
	static OntologyManager manager;
	static OWLClass actorClass;
	static OWLClass peliculaClass;
	static OWLClass paisClass;
	static OWLClass actorFamosoClass;
	
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
		manager.createObjectProperty("actuaEn", actorClass, peliculaClass);
		manager.createObjectProperty("premioPor", actorClass, peliculaClass);
		manager.createObjectProperty("grabadaEn", peliculaClass, paisClass);
		manager.createObjectProperty("haActuadoEn", actorClass, paisClass);
		manager.createObjectProperty("haTriunfadoGraciasA", peliculaClass, actorClass);
	}

	static public String formatString(String stringToFormat) {
		return stringToFormat.split("@en")[0];
	}

	static public void main(String... argv) throws OWLOntologyStorageException, OWLOntologyCreationException {
		File directory = new File(".");
		manager = new OntologyManager();
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


//        OWLClassExpression hasPartSomeNose = factory.getOWLObjectSomeValuesFrom(hasPart, nose);

//		OWLDataRange intGreaterThan2 = factory.getOWLDatatypeMinInclusiveRestriction(2);
//		OWLClassExpression actorFamoso = factory.getOWLDataSomeValuesFrom(haHechoPeliculas, intGreaterThan2);

		ResultSet actores = getData(actoresQuery, dbpedia);
		ResultSet peliculas = getData(filmQueryString, wikidata);
		ResultSet paises = getData(countryQuery, mondial);
		manager.mappingInstances(actores, actorClass, "Actor_1");
		manager.mappingInstances(peliculas, peliculaClass, "q");
		manager.mappingInstances(paises, paisClass, "country");
		for (OWLAxiom ax : ontology.getLogicalAxioms()) {
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