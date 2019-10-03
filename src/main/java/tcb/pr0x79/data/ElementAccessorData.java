package tcb.pr0x79.data;

import tcb.pr0x79.mapping.identification.AnnotatedElementDescription;

/**
 * Used to store accessors and field generation data
 *
 */
public final class ElementAccessorData<N, I> {
	private final String identifierId;
	private final boolean setter;
	private final N accessorNode;
	private final I identifier;

	private AnnotatedElementDescription identified;

	ElementAccessorData(String identifierId, N accessorNode, I identifier) {
		this(identifierId, false, accessorNode, identifier);
	}

	ElementAccessorData(String identifierId, boolean setter, N accessorNode, I identifier) {
		this.identifierId = identifierId;
		this.setter = setter;
		this.accessorNode = accessorNode;
		this.identifier = identifier;
	}

	/**
	 * Returns whether this field accessor is a setter (and if false, is a getter)
	 *
	 * @return
	 */
	public boolean isSetter() {
		return this.setter;
	}

	/**
	 * Returns the tcb.pr0x79.proxy node
	 *
	 * @return
	 */
	public N getAccessorNode() {
		return this.accessorNode;
	}

	/**
	 * Returns the element identifier of the method to be proxied
	 *
	 * @return
	 */
	public I getIdentifier() {
		return this.identifier;
	}

	/**
	 * Returns the identified method
	 *
	 * @return
	 */
	public AnnotatedElementDescription getIdentifiedDescription() {
		return this.identified;
	}

	public boolean setIdentifiedDescription(AnnotatedElementDescription elementDescription) {
		this.identified = elementDescription;
		return true;
	}

	public String getIdentifierId() {
		return identifierId;
	}
}