package net.tegulis.cassandra;

import cql.CQLLexer;
import cql.CQLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public final class GrammarTests {

	public static void parseGrammar(String input) {
		CQLLexer lexer = new CQLLexer(CharStreams.fromString(input));
		SyntaxErrorToRuntimeExceptionErrorListener syntaxErrorListener = new SyntaxErrorToRuntimeExceptionErrorListener();
		lexer.removeErrorListeners();
		lexer.addErrorListener(syntaxErrorListener);
		CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
		CQLParser parser = new CQLParser(commonTokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(syntaxErrorListener);
		ParseTree tree = parser.root();
	}

}
