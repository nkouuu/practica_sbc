package practica_sbc;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.util.*;

public class Main {
	static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	static final OWLDataFactory factory = manager.getOWLDataFactory();
	static String dbpedia = "http://dbpedia.org/sparql";
	static String wikidata = "https://query.wikidata.org/sparql";
	static String mondial = "http://servolis.irisa.fr/mondial/sparql";
	static String peliculasQuery = "PREFIX wd: <http://www.wikidata.org/entity/>"
			+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>" + "PREFIX wikibase: <http://wikiba.se/ontology#>"
			+ "PREFIX p: <http://www.wikidata.org/prop/>" + "PREFIX v: <http://www.wikidata.org/prop/statement/>"
			+ "PREFIX q: <http://www.wikidata.org/prop/qualifier/>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "SELECT DISTINCT ?q ?film_title ?actor ?genre" + "WHERE {" + " ?q wdt:P31 wd:Q11424."
			+ "?q rdfs:label ?film_title filter (lang(?film_title) = \"en\")." + "?q wdt:P136 ?genreID."
			+ "?genreID rdfs:label ?genre filter (lang(?genre) = \"en\")." + "?q wdt:P161 ?actorID."
			+ " ?actorID rdfs:label ?actor filter (lang(?actor) = \"en\")." + "}limit 200";
	static String actoresQuery = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT ?Actor_1 \n"
			+ "WHERE { ?Actor_1 a dbo:Actor .\n" + "?Actor_1 foaf:name ?nombre."
			+ "        FILTER ( NOT EXISTS { ?Actor_1 dbo:activeYearsEndYear ?activeYearsEndYear_21 . } ) }\n"
			+ "LIMIT 200";
	static String paisesQuery = "PREFIX n1: <http://www.semwebtech.org/mondial/10/meta#>"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT ?country ?name_21" + "WHERE { ?country a n1:Country ."
			+ " ?country n1:name ?name_21 ." + "}" + "LIMIT 200";
	

	static public ArrayList<String> getPaises() {
		String countryQuery = "PREFIX n1: <http://www.semwebtech.org/mondial/10/meta#>"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT *" + "WHERE { ?country a n1:Country ."
				+ " ?country n1:name ?name_21 ." + "}" + "LIMIT 200";
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://servolis.irisa.fr/mondial/sparql",
				countryQuery); // Query

		ResultSet results = qexec.execSelect();
		ArrayList<String> paises = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			paises.add(soln.getLiteral("name_21").toString());
		}
		return paises;
	}

	static public ArrayList<String> getActores() {
		String actoresQuery = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT ?Actor_1 ?nombre \n"
				+ "WHERE { ?Actor_1 a dbo:Actor .\n" + "?Actor_1 foaf:name ?nombre."
				+ "        FILTER ( NOT EXISTS { ?Actor_1 dbo:activeYearsEndYear ?activeYearsEndYear_21 . } ) }\n"
				+ "LIMIT 200";

		Query query = QueryFactory.create(actoresQuery);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(dbpedia, query);

		ResultSet results = qexec.execSelect();
		ArrayList<String> actores = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			actores.add(formatString(soln.getLiteral("nombre").toString()));
		}
		qexec.close();
		return actores;
	}
	
	/*static public void mappingInstances (OWLClass ontologyClass, ArrayList<String> instances, PrefixManager dbpm, OWLOntology ontology, String type) {
		for (int i = 0; i < instances.size(); i++) {
			String name = instances.get(i);
			OWLNamedIndividual instance = factory.getOWLNamedIndividual("#" + name.toString(), dbpm);
			if(type.equals("actores")){
				OWLClassAssertionAxiom isActor = factory.getOWLClassAssertionAxiom(ontologyClass, instance);
				manager.addAxiom(ontology, isActor);
			} else if(type.equals("peliculas")) {
				OWLClassAssertionAxiom isPelicula = factory.getOWLClassAssertionAxiom(ontologyClass, instance);
				manager.addAxiom(ontology, isPelicula);
			} else if(type.equals("paises")){
				OWLClassAssertionAxiom isPais = factory.getOWLClassAssertionAxiom(ontologyClass, instance);
				manager.addAxiom(ontology, isPais);
			}
			
		}
		
	}*/
	
	static public void mappingInstances(ResultSet results, OWLClass ontologyClass, PrefixManager dbpm, OWLOntology ontology, String type){
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			if(type.equals("actores")){
				OWLIndividual actor = factory.getOWLNamedIndividual(soln.get("Actor_1").toString());
				OWLClassAssertionAxiom isActor = factory.getOWLClassAssertionAxiom(ontologyClass, actor);
				manager.addAxiom(ontology, isActor);
			} else if(type.equals("paises")){
				OWLNamedIndividual pais = factory.getOWLNamedIndividual(soln.get("country").toString());
				OWLClassAssertionAxiom isPais = factory.getOWLClassAssertionAxiom(ontologyClass, pais);
				manager.addAxiom(ontology, isPais);
			}  else if(type.equals("peliculas")) {
				OWLIndividual pelicula = factory.getOWLNamedIndividual(soln.get("q").toString());
				OWLClassAssertionAxiom isPelicula = factory.getOWLClassAssertionAxiom(ontologyClass, pelicula);
				manager.addAxiom(ontology, isPelicula);
			}
			
		}
	}
	
	static public String formatString (String stringToFormat){
		return stringToFormat.split("@en")[0];
	}

	static public ArrayList<String> getPeliculas() {
		String filmQueryString = "PREFIX wd: <http://www.wikidata.org/entity/>"
				+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>" + "PREFIX wikibase: <http://wikiba.se/ontology#>"
				+ "PREFIX p: <http://www.wikidata.org/prop/>" + "PREFIX v: <http://www.wikidata.org/prop/statement/>"
				+ "PREFIX q: <http://www.wikidata.org/prop/qualifier/>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "SELECT DISTINCT ?q ?film_title ?actor ?genre" + "WHERE {" + " ?q wdt:P31 wd:Q11424."
				+ "?q rdfs:label ?film_title filter (lang(?film_title) = \"en\")." + "?q wdt:P136 ?genreID."
				+ "?genreID rdfs:label ?genre filter (lang(?genre) = \"en\")." + "?q wdt:P161 ?actorID."
				+ " ?actorID rdfs:label ?actor filter (lang(?actor) = \"en\")." + "}limit 200";
		Query filmQuery = QueryFactory.create(filmQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", filmQuery); // Query
		ResultSet results = qexec.execSelect();
		ArrayList<String> peliculas = new ArrayList<String>();
		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			peliculas.add(formatString(soln.getLiteral("film_title").toString()));
		}
		return peliculas;

	}
	
	static public ResultSet getData(String queryString, String service) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query); // Query
		ResultSet results = qexec.execSelect();
		return results;
	}

	static public void main(String... argv) throws OWLOntologyStorageException, OWLOntologyCreationException {
		File directory = new File(".");
		File file = new File(directory + File.separator + "ontology.owl");
		PrefixManager dbpm = new DefaultPrefixManager("<http://dbpedia.org/ontology/>");

		// OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);
		OWLClass actorClass = factory.getOWLClass("Actor");
		OWLClass peliculaClass = factory.getOWLClass("Pelicula");
		OWLClass paisClass = factory.getOWLClass("Pais");
		OWLObjectProperty hacePeliculas = factory.getOWLObjectProperty("hacePeliculas");
		OWLObjectPropertyDomainAxiom rangeAxiom = factory.getOWLObjectPropertyDomainAxiom(hacePeliculas, actorClass);
		OWLObjectPropertyRangeAxiom domainAxiom = factory.getOWLObjectPropertyRangeAxiom(hacePeliculas, actorClass);
		OWLDataProperty pelis = factory.getOWLDataProperty("pelis");
		OWLDatatype integerDatatype = factory.getIntegerOWLDatatype();

		OWLDataPropertyRangeAxiom pelisRange = factory.getOWLDataPropertyRangeAxiom(pelis, integerDatatype);

		OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(actorClass);
		OWLDeclarationAxiom declarationAxiomPelicula = factory.getOWLDeclarationAxiom(peliculaClass);
		OWLDeclarationAxiom declarationAxiomPais = factory.getOWLDeclarationAxiom(paisClass);
		OWLOntology ontology = manager.createOntology();

		manager.addAxiom(ontology, rangeAxiom);
		manager.addAxiom(ontology, domainAxiom);
		manager.addAxiom(ontology, pelisRange);
		manager.addAxiom(ontology, declarationAxiom);
		manager.addAxiom(ontology, declarationAxiomPelicula);
		manager.addAxiom(ontology, declarationAxiomPais);

		/*ArrayList<String> actores = getActores();
		ArrayList<String> peliculas = getPeliculas();
		ArrayList<String> paises = getPaises();
		mappingInstances(actorClass, actores, dbpm, ontology, "actores");
		mappingInstances(peliculaClass, peliculas, dbpm, ontology, "peliculas");
		mappingInstances(paisClass, paises, dbpm, ontology, "paises");*/
		
		ResultSet actores = getData(actoresQuery, dbpedia);
		ResultSet paises = getData(paisesQuery, mondial);
		ResultSet peliculas = getData(peliculasQuery, wikidata);
		mappingInstances(actores, actorClass, dbpm, ontology, "actores");
		mappingInstances(paises, paisClass, dbpm, ontology, "paises");
		mappingInstances(peliculas, peliculaClass, dbpm, ontology, "peliculas");
		
		for (OWLAxiom ax : ontology.getLogicalAxioms()) {
			System.out.println(ax);
		}
		
		manager.saveOntology(ontology, IRI.create(file.toURI()));

	}
}