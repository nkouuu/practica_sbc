package practica_sbc;

import org.semanticweb.owlapi.model.OWLDatatype;

/**
 * Con esta clase almacenamos la informacion de las propiedades que queremos recoger de las querys junto con su tipo de dato
 * @author nicolaealexe and diegomendez
 *
 */
public class Property {
	private String name;
	private OWLDatatype type;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OWLDatatype getType() {
		return type;
	}

	public void setType(OWLDatatype type) {
		this.type = type;
	}

	public Property(String name,OWLDatatype type) {
		this.name = name;
		this.type= type;
	}
}
