package practica_sbc;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;

public class OntologyCreator {
	private OntologyManager ontologyManager;

	// Clases
	private OntologyManager manager;
	private OntologyManager outputOntologyManager;
	private OWLClass actorClass;
	private OWLClass peliculaClass;
	private OWLClass paisClass;
	private OWLClass actorFamosoClass;

	// Propiedades
	private OWLObjectProperty actuaEn;
	private OWLObjectProperty premioPor;
	private OWLObjectProperty grabadaEn;
	private OWLObjectProperty haActuadoEn;
	private OWLObjectProperty haTriunfadoGraciasA;
	private OWLObjectProperty famosoPor;

	// Queries
	private String dbpedia = "http://dbpedia.org/sparql";
	private String wikidata = "https://query.wikidata.org/sparql";
	private String mondial = "http://servolis.irisa.fr/mondial/sparql";
	private String actoresQuery = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT ?actor ?nombre ?activeYearsStartYear \n"
			+ "WHERE { ?actor a dbo:Actor .\n" + "?actor foaf:name ?nombre."
			+ "        FILTER ( NOT EXISTS { ?actor dbo:activeYearsEndYear ?activeYearsEndYear_21 . } )"
			+ " ?actor dbo:activeYearsStartYear ?activeYearsStartYear ." + " }\n" + "LIMIT 200";
	private String countryQuery = "PREFIX n1: <http://www.semwebtech.org/mondial/10/meta#>"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" + "SELECT DISTINCT *" + "WHERE { ?country a n1:Country ."
			+ " ?country n1:name ?name_21 ." + "}" + "LIMIT 200";
	private String filmQueryString = "PREFIX wd: <http://www.wikidata.org/entity/>"
			+ "PREFIX wdt: <http://www.wikidata.org/prop/direct/>" + "PREFIX wikibase: <http://wikiba.se/ontology#>"
			+ "PREFIX p: <http://www.wikidata.org/prop/>" + "PREFIX v: <http://www.wikidata.org/prop/statement/>"
			+ "PREFIX q: <http://www.wikidata.org/prop/qualifier/>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "SELECT DISTINCT ?q ?film_title ?actor ?genre"
			+ "WHERE {" + " ?q wdt:P31 wd:Q11424." + "?q rdfs:label ?film_title filter (lang(?film_title) = \"en\")."
			+ "?q wdt:P136 ?genreID." + "?genreID rdfs:label ?genre filter (lang(?genre) = \"en\")."
			+ "?q wdt:P161 ?actorID." + " ?actorID rdfs:label ?actor filter (lang(?actor) = \"en\")." + "}limit 200";

	/**
	 * Realizar una query y devolver los resultados
	 * @param queryString Query a realizar
	 * @param service Ruta sobre la cual realizar la query
	 * @return Resultados de la query
	 */
	public ResultSet getData(String queryString, String service) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(service, query); // Query
		ResultSet results = qexec.execSelect();
		return results;
	}

	// Añadir clases a la ontologia
	public void addClases() {
		this.actorClass = this.ontologyManager.addClass("Actor");
		this.peliculaClass = this.ontologyManager.addClass("Pelicula");
		this.paisClass = this.ontologyManager.addClass("Pais");
		this.actorFamosoClass = this.ontologyManager.addClass("ActorFamoso");
		this.ontologyManager.createSubclass(actorFamosoClass, actorClass);

	}

	// Añadir Propiedades entre clases
	public void addObjectProperties() {
		this.actuaEn = this.ontologyManager.createObjectProperty("actua_en", this.actorClass, this.peliculaClass);
		this.premioPor = this.ontologyManager.createObjectProperty("premio_por", this.actorClass, this.peliculaClass);
		this.grabadaEn = this.ontologyManager.createObjectProperty("grabada_en", this.peliculaClass, this.paisClass);
		this.haActuadoEn = this.ontologyManager.createObjectProperty("ha_actuado_en", this.actorClass, this.paisClass);
		this.haTriunfadoGraciasA = this.ontologyManager.createObjectProperty("ha_triunfado_gracias_a",
				this.actorClass,this.peliculaClass );
		this.famosoPor = this.ontologyManager.createObjectProperty("famoso_por", this.actorClass, this.peliculaClass);
	}

	public void addDataProperties() {
		OWLDataFactory factory = this.ontologyManager.getFactory();

		OWLDatatype integerDatatype = factory.getIntegerOWLDatatype();
		OWLDataProperty haHechoPeliculas = this.ontologyManager.createDataProperty("ha_hecho_peliculas", actorClass,
				integerDatatype);
		OWLDataRange intGreaterThan2 = factory.getOWLDatatypeMinInclusiveRestriction(2);
		OWLClassExpression exp = factory.getOWLDataSomeValuesFrom(haHechoPeliculas, intGreaterThan2);
		this.ontologyManager.createSubclass(this.actorFamosoClass, exp);
		
//		OWLDataRange intGreaterThan2 = factory.getOWLDatatypeMinInclusiveRestriction(2);
//		OWLIndividual esp = this.ontologyManager.addInstance(this.paisClass, "España");
//		OWLObjectAllValuesFrom exp2 = factory.getOWLObjectAllValuesFrom(this.grabadaEn, esp );
//		this.ontologyManager.createSubclass(this.actorFamosoClass, exp);
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
		this.addObjectProperties();

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
