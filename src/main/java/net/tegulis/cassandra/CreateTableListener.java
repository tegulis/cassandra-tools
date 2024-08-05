package net.tegulis.cassandra;

import cql.CQLBaseListener;
import cql.CQLParser;
import cql.CQLParser.ColumnDefinitionContext;
import net.tegulis.cassandra.CreateTableDefinition.Column.ColumnType;
import net.tegulis.cassandra.CreateTableDefinition.Column.DataType;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateTableListener extends CQLBaseListener {

	private final BufferedTokenStream tokenStream;

	private final List<CreateTableDefinition> createTableDefinitions = new ArrayList<>();

	public CreateTableListener(BufferedTokenStream tokenStream) {
		this.tokenStream = tokenStream;
	}

	public List<CreateTableDefinition> getCreateTableDefinitions() {
		return createTableDefinitions;
	}

	private static Map<String, ColumnType> getColumnTypesFromPrimaryKeyDefinition(CQLParser.PrimaryKeyContext primaryKeyContext) {
		Map<String, ColumnType> columnTypes = new HashMap<>();
		if (primaryKeyContext.clusteringColumns() != null) {
			primaryKeyContext.clusteringColumns().columnName().stream()
				.map(RuleContext::getText)
				.forEach(clusteringColumn -> columnTypes.put(clusteringColumn, ColumnType.CLUSTERING));
		}
		if (primaryKeyContext.partitionKey() != null) {
			primaryKeyContext.partitionKey().columnName().stream()
				.map(RuleContext::getText)
				.forEach(partitionColumn -> columnTypes.put(partitionColumn, ColumnType.PARTITION));
		}
		return columnTypes;
	}

	@Override
	public void enterCreateTableStatement(CQLParser.CreateTableStatementContext createTableStatementContext) {
		Objects.requireNonNull(createTableStatementContext.tableName());
		String keyspace = createTableStatementContext.tableName().keyspaceName() == null ? "" : createTableStatementContext.tableName().keyspaceName().getText();
		String tableName = createTableStatementContext.tableName().name().getText();
		int firstLineNumber = createTableStatementContext.start.getLine();
		CreateTableDefinition createTableDefinition = new CreateTableDefinition(keyspace, tableName, firstLineNumber);
		Map<String, ColumnType> columnTypes = getColumnTypesFromPrimaryKeyDefinition(createTableStatementContext.primaryKey());
		List<ColumnDefinitionContext> columnDefinitions = createTableStatementContext.columnDefinition();
		for (ColumnDefinitionContext columnDefinition : columnDefinitions) {
			String columnName = columnDefinition.columnName().getText();
			DataType dataType = DataType.valueOf(columnDefinition.cqlType().getText().toUpperCase());
			ColumnType columnType = ColumnType.REGULAR;
			if (columnDefinition.STATIC() != null) {
				columnType = ColumnType.STATIC;
			}
			if (columnTypes.containsKey(columnName)) {
				columnType = columnTypes.get(columnName);
			}
			CreateTableDefinition.Column column = new CreateTableDefinition.Column(columnName, dataType, columnType);
			int columnDefinitionLine = columnDefinition.getStart().getLine();
			List<Token> hiddenTokens = tokenStream.getHiddenTokensToRight(columnDefinition.getStop().getTokenIndex() + 1, Token.HIDDEN_CHANNEL);
			hiddenTokens.stream()
				.filter(token -> token.getLine() == columnDefinitionLine)
				.map(Token::getText)
				.filter(text -> text.startsWith("//"))
				.map(String::trim)
				.forEach(column::tryEnrichFromComment);
			createTableDefinition.columns.add(column);
		}
		createTableDefinitions.add(createTableDefinition);
	}

}
