package tcb.pr0x79.proxy;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import tcb.pr0x79.Bootstrapper;
import tcb.pr0x79.mapping.identification.type.ClassIdentifier;
import tcb.pr0x79.mapping.identification.type.FieldIdentifier;
import tcb.pr0x79.mapping.identification.type.MethodIdentifier;
import tcb.pr0x79.mapping.identification.type.InstructionTypeIdentifier;
import tcb.pr0x79.proxy.mappings.MappingsParser;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Instrumentor implements tcb.pr0x79.Instrumentor {
	@Override
	public void initBootstrapper(Bootstrapper bootstrapper) {
		//This is called as soon as the java agent is initialized

		System.out.println("Instrumentor initialized!");


		System.out.println("Loading mappings");

		//The class, method, field and instruction mappings can be loaded from .json files as demonstrated

		JsonParser parser = new JsonParser();

		JsonElement classMappingsJson = parser.parse(new JsonReader(new InputStreamReader(this.getClass().getResourceAsStream("/mappings/class_mappings.json"))));
		Map<String, ClassIdentifier> classIdentifiers = new HashMap<>();
		MappingsParser.parseClassIdentifiers(classMappingsJson.getAsJsonObject(), classIdentifiers);
		bootstrapper.getMapperRegistry().registerClassMapper("json", (identifier, type) -> classIdentifiers.get(identifier));

		JsonElement fieldMappingsJson = parser.parse(new JsonReader(new InputStreamReader(this.getClass().getResourceAsStream("/mappings/field_mappings.json"))));
		Map<String, FieldIdentifier> fieldIdentifiers = new HashMap<>();
		MappingsParser.parseFieldIdentifiers(fieldMappingsJson.getAsJsonObject(), fieldIdentifiers);
		bootstrapper.getMapperRegistry().registerFieldMapper("json", (identifier, type) -> fieldIdentifiers.get(identifier));

		JsonElement methodMappingsJson = parser.parse(new JsonReader(new InputStreamReader(this.getClass().getResourceAsStream("/mappings/method_mappings.json"))));
		Map<String, MethodIdentifier> methodIdentifiers = new HashMap<>();
		MappingsParser.parseMethodIdentifiers(methodMappingsJson.getAsJsonObject(), methodIdentifiers);
		bootstrapper.getMapperRegistry().registerMethodMapper("json", (identifier, type) -> methodIdentifiers.get(identifier));

		JsonElement instructionMappingsJson = parser.parse(new JsonReader(new InputStreamReader(this.getClass().getResourceAsStream("/mappings/instruction_mappings.json"))));
		Map<String, InstructionTypeIdentifier> instructionIdentifier = new HashMap<>();
		MappingsParser.parseInstructionIdentifiers(instructionMappingsJson.getAsJsonObject(), instructionIdentifier);
		bootstrapper.getMapperRegistry().registerInstructionMapper("json", (identifier, type) -> instructionIdentifier.get(identifier));

		System.out.println("Registering accessors\n");

		//The accessor interfaces are registered here. Their classes must _not_ be loaded before or during initBootstrapper

		bootstrapper.getAccessors().registerAccessor("tcb.pr0x79.proxy.accessors.MainAccessor");
		bootstrapper.getAccessors().registerAccessor("tcb.pr0x79.proxy.accessors.SomeClassAccessor");
	}

	@Override
	public void onBootstrapperException(Exception ex) {
		//Any exceptions caused by the bootstrapper, bytecode modification or an identifier is redirected here
		//Useful for advanced exception handling

		ex.printStackTrace();
		System.exit(-1);
	}
}
