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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
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
	@Nonnull
	private static final String KOALA = "<?xml version=\"1.0\"?>\n"
			+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns=\"http://protege.stanford.edu/plugins/owl/owl-library/koala.owl#\" xml:base=\"http://protege.stanford.edu/plugins/owl/owl-library/koala.owl\">\n"
			+ "  <owl:Ontology rdf:about=\"\"/>\n"
			+ "  <owl:Class rdf:ID=\"Female\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty><owl:hasValue><Gender rdf:ID=\"female\"/></owl:hasValue></owl:Restriction></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Marsupials\"><owl:disjointWith><owl:Class rdf:about=\"#Person\"/></owl:disjointWith><rdfs:subClassOf><owl:Class rdf:about=\"#Animal\"/></rdfs:subClassOf></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Student\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Person\"/><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasValue></owl:Restriction><owl:Restriction><owl:someValuesFrom><owl:Class rdf:about=\"#University\"/></owl:someValuesFrom><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"KoalaWithPhD\"><owl:versionInfo>1.2</owl:versionInfo><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:hasValue><Degree rdf:ID=\"PhD\"/></owl:hasValue><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasDegree\"/></owl:onProperty></owl:Restriction><owl:Class rdf:about=\"#Koala\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"University\"><rdfs:subClassOf><owl:Class rdf:ID=\"Habitat\"/></rdfs:subClassOf></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Koala\"><rdfs:subClassOf><owl:Restriction><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</owl:hasValue><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:someValuesFrom><owl:Class rdf:about=\"#DryEucalyptForest\"/></owl:someValuesFrom><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Animal\"><rdfs:seeAlso>Male</rdfs:seeAlso><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:cardinality><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><owl:versionInfo>1.1</owl:versionInfo></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Forest\"><rdfs:subClassOf rdf:resource=\"#Habitat\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Rainforest\"><rdfs:subClassOf rdf:resource=\"#Forest\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"GraduateStudent\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasDegree\"/></owl:onProperty><owl:someValuesFrom><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><Degree rdf:ID=\"BA\"/><Degree rdf:ID=\"BS\"/></owl:oneOf></owl:Class></owl:someValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Student\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Parent\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Animal\"/><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass><rdfs:subClassOf rdf:resource=\"#Animal\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"DryEucalyptForest\"><rdfs:subClassOf rdf:resource=\"#Forest\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Quokka\"><rdfs:subClassOf><owl:Restriction><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasValue><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"TasmanianDevil\"><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"MaleStudentWith3Daughters\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Student\"/><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty><owl:hasValue><Gender rdf:ID=\"male\"/></owl:hasValue></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty><owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">3</owl:cardinality></owl:Restriction><owl:Restriction><owl:allValuesFrom rdf:resource=\"#Female\"/><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Degree\"/>\n  <owl:Class rdf:ID=\"Gender\"/>\n"
			+ "  <owl:Class rdf:ID=\"Male\"><owl:equivalentClass><owl:Restriction><owl:hasValue rdf:resource=\"#male\"/><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty></owl:Restriction></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Person\"><rdfs:subClassOf rdf:resource=\"#Animal\"/><owl:disjointWith rdf:resource=\"#Marsupials\"/></owl:Class>\n"
			+ "  <owl:ObjectProperty rdf:ID=\"hasHabitat\"><rdfs:range rdf:resource=\"#Habitat\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:ObjectProperty>\n"
			+ "  <owl:ObjectProperty rdf:ID=\"hasDegree\"><rdfs:domain rdf:resource=\"#Person\"/><rdfs:range rdf:resource=\"#Degree\"/></owl:ObjectProperty>\n"
			+ "  <owl:ObjectProperty rdf:ID=\"hasChildren\"><rdfs:range rdf:resource=\"#Animal\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:ObjectProperty>\n"
			+ "  <owl:FunctionalProperty rdf:ID=\"hasGender\"><rdfs:range rdf:resource=\"#Gender\"/><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#ObjectProperty\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:FunctionalProperty>\n"
			+ "  <owl:FunctionalProperty rdf:ID=\"isHardWorking\"><rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#boolean\"/><rdfs:domain rdf:resource=\"#Person\"/><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#DatatypeProperty\"/></owl:FunctionalProperty>\n"
			+ "  <Degree rdf:ID=\"MA\"/>\n</rdf:RDF>";

	public ResultSet getCountries() {
		String countryQuery = "PREFIX n1: <http://www.semwebtech.org/mondial/10/meta#>"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
                + "SELECT DISTINCT *"
                + "WHERE { ?country a n1:Country ."
                + " ?country n1:name ?name_21 ."
                + "}"
                + "LIMIT 200";
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://servolis.irisa.fr/mondial/sparql", countryQuery); // Query

		ResultSet results = qexec.execSelect();
		return results;
//		while (results.hasNext()) {
//			QuerySolution soln = results.nextSolution();
//			System.out.println(soln.getLiteral("name_21"));
//		}
	}
	static public ResultSet getActores() {
		String actoresQuery = "PREFIX dbo: <http://dbpedia.org/ontology/>\n" + "SELECT DISTINCT ?Actor_1\n"
				+ "WHERE { ?Actor_1 a dbo:Actor .\n"
				+ "        FILTER ( NOT EXISTS { ?Actor_1 dbo:activeYearsEndYear ?activeYearsEndYear_21 . } ) }\n"
				+ "LIMIT 200";

		Query query = QueryFactory.create(actoresQuery);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(dbpedia, query);

		ResultSet results = qexec.execSelect();
		qexec.close();
		return results;
	}
	public ResultSet getPeliculas() {
		String filmQueryString = "PREFIX wd: <http://www.wikidata.org/entity/>"
				+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>" + "PREFIX wikibase: <http://wikiba.se/ontology#>"
				+ "PREFIX p: <http://www.wikidata.org/prop/>" + "PREFIX v: <http://www.wikidata.org/prop/statement/>"
				+ "PREFIX q: <http://www.wikidata.org/prop/qualifier/>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "SELECT DISTINCT ?q ?film_title ?actor ?genre"
				+ "WHERE {" + " ?q wdt:P31 wd:Q11424."
				+ "?q rdfs:label ?film_title filter (lang(?film_title) = \"en\")." + "?q wdt:P136 ?genreID."
				+ "?genreID rdfs:label ?genre filter (lang(?genre) = \"en\")." + "?q wdt:P161 ?actorID."
				+ " ?actorID rdfs:label ?actor filter (lang(?actor) = \"en\")." + "}limit 100";
		Query filmQuery = QueryFactory.create(filmQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("https://query.wikidata.org/sparql", filmQuery); // Query
		ResultSet results = qexec.execSelect();
		return results;

	}
	


	static public void main(String... argv) throws OWLOntologyStorageException, OWLOntologyCreationException {
		File directory = new File(".");
		File file = new File(directory + File.separator + "ontology.owl");
		PrefixManager dbpm = new DefaultPrefixManager("<http://dbpedia.org/ontology/>");

//		OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);
		OWLClass actorClass = factory.getOWLClass("Actor");
		OWLObjectProperty hacePeliculas = factory.getOWLObjectProperty("hacePeliculas");
		OWLObjectPropertyDomainAxiom rangeAxiom = factory.getOWLObjectPropertyDomainAxiom(hacePeliculas, actorClass);
		OWLObjectPropertyRangeAxiom domainAxiom = factory.getOWLObjectPropertyRangeAxiom(hacePeliculas, actorClass);
		OWLDataProperty pelis = factory.getOWLDataProperty("pelis");
        OWLDatatype integerDatatype = factory.getIntegerOWLDatatype();

        OWLDataPropertyRangeAxiom pelisRange = factory.getOWLDataPropertyRangeAxiom(pelis, integerDatatype);
        
		OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(actorClass);
		OWLOntology ontology = manager.createOntology();

        manager.addAxiom(ontology, rangeAxiom);
        manager.addAxiom(ontology, domainAxiom);
        manager.addAxiom(ontology, pelisRange);
        manager.addAxiom(ontology, declarationAxiom);
		
        manager.saveOntology(ontology, IRI.create(file.toURI()));
        
		

		
	}
}