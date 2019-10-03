package tcb.pr0x79.mapping;

@FunctionalInterface
public interface Mapper<I> {
	/**
	 * Maps a string identifier to a the specified generic type.
	 *
	 * @param identifier
	 * @param search
	 * @return
	 */
	I map(String identifier, SearchType search);

	enum SearchType {
		/**
		 * When a element accessor target is being searched
		 */
		ACCESSOR,

		/**
		 * When a field name generator is being searched
		 */
		NAME_GENERATOR,

		/**
		 * When a method interceptor target is being searched
		 */
		INTERCEPTOR,

		/**
		 * When a local variable target is being searched
		 */
		LOCAL_VARIABLE,

		/**
		 * When a method interceptor entry instruction is being searched
		 */
		INTERCEPTOR_ENTRY,

		/**
		 * When a method interceptor exit instruction is being searched
		 */
		INTERCEPTOR_EXIT,

		OTHER;
	}
}
