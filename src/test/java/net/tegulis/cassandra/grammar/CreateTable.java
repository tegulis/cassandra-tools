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
		GrammarTests.parseGrammar(
			"""
				CREATE TABLE t (
					pk int,
					t int,
					v text,
					s text static,
					PRIMARY KEY (pk, t)
				);
				"""
		);
		GrammarTests.parseGrammar(
			"""
				CREATE TABLE t (k text PRIMARY KEY);
				"""
		);
		GrammarTests.parseGrammar(
			"""
				CREATE TABLE t (
					a int,
					b int,
					c int,
					d int,
					PRIMARY KEY ((a, b), c, d)
				);
				"""
		);
		GrammarTests.parseGrammar(
			"""
				CREATE TABLE t2 (
					a int,
					b int,
					c int,
					d int,
					PRIMARY KEY (a, b, c)
				);
				"""
		);
		GrammarTests.parseGrammar(
			"""
				CREATE TABLE simple (
					id int,
					key text,
					value text,
					PRIMARY KEY (key, value)
				) WITH compression = {'class': 'LZ4Compressor', 'chunk_length_in_kb': 4};
				"""
		);
		GrammarTests.parseGrammar(
			"""
				CREATE TABLE simple (
				id int,
				key text,
				value text,
				PRIMARY KEY (key, value)
				) WITH caching = {'keys': 'ALL', 'rows_per_partition': 10};
				"""
		);
	}

}
