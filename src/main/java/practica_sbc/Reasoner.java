package practica_sbc;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;

public class Reasoner {
	private OWLReasoner reasoner;

	public Reasoner(OWLOntology ontology) {
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		this.reasoner = reasonerFactory.createReasoner(ontology);
	}
	
	public boolean isConsistent() {
		return this.reasoner.isConsistent();
	}
	
	/**
	 * Indicamos al razonador que tipos de datos queremos que procese
	 */
	public void classifyOntology() {
		this.reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.CLASS_ASSERTIONS,
				InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY,
				InferenceType.OBJECT_PROPERTY_ASSERTIONS);

	}

	public List<InferredAxiomGenerator<? extends OWLAxiom>> generateInferredAxioms() {
		List<InferredAxiomGenerator<? extends OWLAxiom>> generators = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		generators.add(new InferredSubClassAxiomGenerator());
		generators.add(new InferredClassAssertionAxiomGenerator());
		generators.add(new InferredDataPropertyCharacteristicAxiomGenerator());
		generators.add(new InferredEquivalentClassAxiomGenerator());
		generators.add(new InferredEquivalentDataPropertiesAxiomGenerator());
		generators.add(new InferredEquivalentObjectPropertyAxiomGenerator());
		generators.add(new InferredInverseObjectPropertiesAxiomGenerator());
		generators.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
		generators.add(new InferredPropertyAssertionGenerator());

		generators.add(new InferredSubClassAxiomGenerator());
		generators.add(new InferredSubDataPropertyAxiomGenerator());
		generators.add(new InferredSubObjectPropertyAxiomGenerator());
		
		List<InferredIndividualAxiomGenerator<? extends OWLIndividualAxiom>> individualAxioms =
	            new ArrayList<InferredIndividualAxiomGenerator<? extends OWLIndividualAxiom>>();
	        generators.addAll(individualAxioms);

		generators.add(new InferredDisjointClassesAxiomGenerator());
		return generators;
	}

	public void generateInferredOntology(OWLOntology ontology, List<InferredAxiomGenerator<? extends OWLAxiom>> gens,
			OWLOntologyManager outputOntologyManager) {
		InferredOntologyGenerator iog = new InferredOntologyGenerator(this.reasoner, gens);
		iog.fillOntology(outputOntologyManager.getOWLDataFactory(), ontology);

	}

	public void finishReasonerThreads() {
		this.reasoner.dispose();
	}

	public OWLReasoner getReasoner() {
		return reasoner;
	}

}
