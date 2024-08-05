package net.tegulis.cassandra;

import cql.CQLLexer;
import cql.CQLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] ignored) {
		InputStreamReader inputStreamReader = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		List<String> inputLines = bufferedReader.lines().toList();
		// Oh, so dirty
		String inputString = String.join("\n", inputLines);
		// Create a lexer that feeds off of input CharStream
		CQLLexer lexer = new CQLLexer(CharStreams.fromString(inputString));
		SyntaxErrorToRuntimeExceptionErrorListener syntaxErrorListener = new SyntaxErrorToRuntimeExceptionErrorListener();
		lexer.removeErrorListeners();
		lexer.addErrorListener(syntaxErrorListener);
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		CQLParser parser = new CQLParser(commonTokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(syntaxErrorListener);
		ParseTree tree = parser.root();
		ParseTreeWalker walker = new ParseTreeWalker();
		CreateTableListener listener = new CreateTableListener(commonTokenStream);
		walker.walk(listener, tree);
		Map<Integer, CreateTableDefinition> createTableDefinitionsByFirstLine = new HashMap<>();
		listener.getCreateTableDefinitions().forEach(
				ctd -> createTableDefinitionsByFirstLine.put(ctd.firstLineNumber, ctd)
		);
		for (int i = 0; i < inputLines.size(); i++) {
			int lineNumber = i + 1;
			if (createTableDefinitionsByFirstLine.containsKey(lineNumber)) {
				CreateTableDefinition createTableDefinition = createTableDefinitionsByFirstLine.get(lineNumber);
				createTableDefinition.toString().lines().forEach(
						line -> System.out.println("// " + line)
				);
			}
			System.out.println(inputLines.get(i));
		}
	}

}
