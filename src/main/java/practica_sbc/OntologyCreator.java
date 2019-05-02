package practica_sbc;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class OntologyCreator {
	private OntologyManager ontologyManager;

	// Clases
	static OntologyManager manager;
	static OntologyManager outputOntologyManager;
	static OWLClass actorClass;
	static OWLClass peliculaClass;
	static OWLClass paisClass;
	static OWLClass actorFamosoClass;

	// Propiedades
	static OWLObjectProperty actuaEn;
	static OWLObjectProperty premioPor;
	static OWLObjectProperty grabadaEn;
	static OWLObjectProperty haActuadoEn;
	static OWLObjectProperty haTriunfadoGraciasA;
	static OWLObjectProperty famosoPor;

	// Queries
	static String dbpedia = "http://dbpedia.org/sparql";
	static String wikidata = "https://query.wikidata.org/sparql";
	static String mondial = "http://servolis.irisa.fr/mondial/sparql";
	static String actoresQuery = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT ?actor ?nombre ?activeYearsStartYear \n"
			+ "WHERE { ?actor a dbo:Actor .\n" + "?actor foaf:name ?nombre."
			+ "        FILTER ( NOT EXISTS { ?actor dbo:activeYearsEndYear ?activeYearsEndYear_21 . } )"
			+ " ?actor dbo:activeYearsStartYear ?activeYearsStartYear ." + " }\n" + "LIMIT 200";
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
	public ResultSet getData(String queryString, String service) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query); // Query
		ResultSet results = qexec.execSelect();
		return results;
	}

	// Añadir clases a la ontologia
	public void addClases() {
		actorClass = this.ontologyManager.addClass("Actor");
		peliculaClass = this.ontologyManager.addClass("Pelicula");
		paisClass = this.ontologyManager.addClass("Pais");
		actorFamosoClass = this.ontologyManager.addClass("ActorFamoso");
		ontologyManager.createSubclass(actorFamosoClass, actorClass);

	}

	// Añadir Propiedades
	public void addProperties() {
		actuaEn = this.ontologyManager.createObjectProperty("actuaEn", actorClass, peliculaClass);
		premioPor = this.ontologyManager.createObjectProperty("premioPor", actorClass, peliculaClass);
		grabadaEn = this.ontologyManager.createObjectProperty("grabadaEn", peliculaClass, paisClass);
		haActuadoEn = this.ontologyManager.createObjectProperty("haActuadoEn", actorClass, paisClass);
		haTriunfadoGraciasA = this.ontologyManager.createObjectProperty("haTriunfadoGraciasA", peliculaClass, actorClass);
		famosoPor = this.ontologyManager.createObjectProperty("famosoPor", actorClass, peliculaClass);
	}

	public String formatString(String stringToFormat) {
		return stringToFormat.split("@en")[0];
	}

	public OntologyCreator(OntologyManager manager) {
		this.ontologyManager = manager;
	}

	// Funcion principal que controla la creacion de la ontologia
	public OWLOntology create() {

		// Creamos las clases y las propiedades entre ellas.
		this.addClases();
		this.addProperties();

		// Hacemos las querys por los tres tipos de datos y obtenemos los resultados
		ResultSet actores = this.getData(actoresQuery, dbpedia);
		ResultSet peliculas = this.getData(filmQueryString, wikidata);
		ResultSet paises = this.getData(countryQuery, mondial);

		// Mapeamos las instancias a su clase correspondiente
		this.ontologyManager.mappingInstances(actores, actorClass, "actor");
		this.ontologyManager.mappingInstances(peliculas, peliculaClass, "q");
		this.ontologyManager.mappingInstances(paises, paisClass, "country");

		return this.ontologyManager.getOntology();
	}
}
