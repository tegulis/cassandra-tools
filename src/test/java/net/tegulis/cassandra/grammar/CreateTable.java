package net.tegulis.cassandra.grammar;

import net.tegulis.cassandra.GrammarTests;
import org.junit.jupiter.api.Test;

public class CreateTable {

	@Test
	public void createTableExamples() {
		GrammarTests.parseGrammar(
			"""
			CREATE TABLE monkey_species (
				species text PRIMARY KEY,
				common_name text,
				population varint,
				average_size int
			) WITH comment='Important biological records';
			"""
		);
		GrammarTests.parseGrammar(
			"""
				CREATE TABLE loads (
					machine inet,
					cpu int,
					mtime timeuuid,
					load float,
					PRIMARY KEY ((machine, cpu), mtime)
				) WITH CLUSTERING ORDER BY (mtime DESC);
				"""
		);
		GrammarTests.parseGrammar(
			"""
				CREATE TABLE timeline (
					userid uuid,
					posted_month int,
					posted_time uuid,
					body text,
					posted_by text,
					PRIMARY KEY (userid, posted_month, posted_time)
				) WITH compaction = { 'class' : 'LeveledCompactionStrategy' };
				"""
		);
	}

}
