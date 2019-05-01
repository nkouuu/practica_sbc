package practica_sbc;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

public class Reasoner {
	private OWLReasoner reasoner;
	public Reasoner (OWLOntology ontology){
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		this.reasoner = reasonerFactory.createReasoner(ontology);
		System.out.println(this.reasoner.isConsistent());
	}
	
	public void classifyOntology(){
		this.reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
	}
	
	public List<InferredAxiomGenerator<? extends OWLAxiom>> generateInferredAxioms(){
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		gens.add(new InferredSubClassAxiomGenerator());
		gens.add(new InferredEquivalentClassAxiomGenerator());
		return gens;
	}
	
	public void generateInferredOntology (OWLOntology ontology, List<InferredAxiomGenerator<? extends OWLAxiom>> gens, OWLOntologyManager outputOntologyManager) {
		InferredOntologyGenerator iog = new InferredOntologyGenerator(this.reasoner,
				gens);
		iog.fillOntology(outputOntologyManager.getOWLDataFactory(), ontology);
		

	}
	
	public void finishReasonerThreads(){
		this.reasoner.dispose();
	}

	public OWLReasoner getReasoner() {
		return reasoner;
	}

	
	
}
