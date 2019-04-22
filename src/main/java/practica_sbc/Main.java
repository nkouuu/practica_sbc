package practica_sbc;

import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class Main {
    static public void main(String...argv) {
    	String sparqlQueryString1 = "PREFIX dbont: <http://dbpedia.org/ontology/> " +
    	        "PREFIX dbp: <http://dbpedia.org/property/>" +
    	        "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>" +
    	        "   SELECT ?musician  ?place" +
    	        "   WHERE { " +
    	        "       ?musician dbont:birthPlace ?place ." +
    	        "   }";

    	Query query = QueryFactory.create(sparqlQueryString1);
    	QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

    	ResultSet results = qexec.execSelect();
    	ResultSetFormatter.out(System.out, results, query);       

    	qexec.close() ;
    }
}