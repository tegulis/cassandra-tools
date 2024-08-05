// $antlr-format columnLimit 150, minEmptyLines 0, maxEmptyLinesToKeep 1
// $antlr-format useTab true
// $antlr-format alignTrailingComments true, reflowComments false
// $antlr-format allowShortRulesOnASingleLine true, allowShortBlocksOnASingleLine true
// $antlr-format alignSemicolons none, alignColons trailing

grammar CQL;

@header {
	package cql;
}

options {
	caseInsensitive = true;
	language = Java;
}

WHITESPACE			: [ \t\r\n]+ -> channel (HIDDEN);
MULTI_LINE_COMMENT	: '/*' .*? '*/' -> channel (HIDDEN);
LINE_COMMENT		: ('--' | '#' | '//') ~[\r\n]* ('\r'? '\n' | EOF) -> channel (HIDDEN);

// Operators, punctuatons
LEFT_ROUND_BRACKET		: '(';
RIGHT_ROUND_BRACKET		: ')';
LEFT_CURLY_BRACKET		: '{';
RIGHT_CURLY_BRACKET		: '}';
LEFT_SQUARE_BRACKET		: '[';
RIGHT_SQUARE_BRACKET	: ']';
COMMA					: ',';
SEMICOLON				: ';';
COLON					: ':';
DOT						: '.';
STAR					: '*';
DIVIDE					: '/';
MODULO					: '%';
PLUS					: '+';
MINUSMINUS				: '--';
MINUS					: '-';
DOUBLE_QUOTE			: '"';
SINGLE_QUOTE			: '\'';
BACKSLASH				: '\\';
QUESTIONMARK			: '?';
DOLLAR					: '$';
DOLLARDOLLAR			: '$$';
OPERATOR_EQ				: '=';
OPERATOR_LT				: '<';
OPERATOR_GT				: '>';
OPERATOR_LTE			: '<=';
OPERATOR_GTE			: '>=';

fragment DIGIT			: [0-9];
fragment HEX_DIGIT		: [0-9A-F];
fragment HEX_DIGIT_4	: HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT;
fragment HEX_DIGIT_8	: HEX_DIGIT_4 HEX_DIGIT_4;
fragment HEX_DIGIT_12	: HEX_DIGIT_4 HEX_DIGIT_4 HEX_DIGIT_4;

// Keywords
// https://cassandra.apache.org/doc/stable/cassandra/cql/appendices.html#appendix-A
// CQL distinguishes between reserved and non-reserved keywords. Reserved keywords cannot be used as identifier, they
// are truly reserved for the language (but one can enclose a reserved keyword by double-quotes to use it as an
// identifier). Non-reserved keywords however only have a specific meaning in certain context but can used as identifier
// otherwise. The only raison d’être of these non-reserved keywords is convenience: some keyword are non-reserved when
// it was always easy for the parser to decide whether they were used as keywords or not.
// Reserved keywords:
ADD				: 'ADD';
ALLOW			: 'ALLOW';
ALTER			: 'ALTER';
AND				: 'AND';
APPLY			: 'APPLY';
ASC				: 'ASC';
AUTHORIZE		: 'AUTHORIZE';
BATCH			: 'BATCH';
BEGIN			: 'BEGIN';
BY				: 'BY';
COLUMNFAMILY	: 'COLUMNFAMILY';
CREATE			: 'CREATE';
DELETE			: 'DELETE';
DESC			: 'DESC';
DESCRIBE		: 'DESCRIBE';
DROP			: 'DROP';
ENTRIES			: 'ENTRIES';
EXECUTE			: 'EXECUTE';
FROM			: 'FROM';
FULL			: 'FULL';
GRANT			: 'GRANT';
IF				: 'IF';
IN				: 'IN';
INDEX			: 'INDEX';
INFINITY		: 'INFINITY';
INSERT			: 'INSERT';
INTO			: 'INTO';
KEYSPACE		: 'KEYSPACE';
LIMIT			: 'LIMIT';
MODIFY			: 'MODIFY';
NAN				: 'NAN';
NORECURSIVE		: 'NORECURSIVE';
NOT				: 'NOT';
NULL			: 'NULL';
OF				: 'OF';
ON				: 'ON';
OR				: 'OR';
ORDER			: 'ORDER';
PRIMARY			: 'PRIMARY';
RENAME			: 'RENAME';
REPLACE			: 'REPLACE';
REVOKE			: 'REVOKE';
SCHEMA			: 'SCHEMA';
SELECT			: 'SELECT';
SET				: 'SET';
TABLE			: 'TABLE';
TO				: 'TO';
TOKEN			: 'TOKEN';
TRUNCATE		: 'TRUNCATE';
UNLOGGED		: 'UNLOGGED';
UPDATE			: 'UPDATE';
USE				: 'USE';
USING			: 'USING';
WHERE			: 'WHERE';
WITH			: 'WITH';
// Non-reserved keywords:
AGGREGATE	: 'AGGREGATE';
ALL			: 'ALL';
AS			: 'AS';
ASCII		: 'ASCII';
BIGINT		: 'BIGINT';
BLOB		: 'BLOB';
BOOLEAN		: 'BOOLEAN';
CALLED		: 'CALLED';
CLUSTERING	: 'CLUSTERING';
COMPACT		: 'COMPACT';
CONTAINS	: 'CONTAINS';
COUNT		: 'COUNT';
COUNTER		: 'COUNTER';
CUSTOM		: 'CUSTOM';
DATE		: 'DATE';
DECIMAL		: 'DECIMAL';
DISTINCT	: 'DISTINCT';
DOUBLE		: 'DOUBLE';
DURATION	: 'DURATION';
EXISTS		: 'EXISTS';
FILTERING	: 'FILTERING';
FINALFUNC	: 'FINALFUNC';
FLOAT		: 'FLOAT';
FUNCTION	: 'FUNCTION';
FUNCTIONS	: 'FUNCTIONS';
INET		: 'INET';
INITCOND	: 'INITCOND';
INPUT		: 'INPUT';
INT			: 'INT';
JSON		: 'JSON';
KEY			: 'KEY';
KEYS		: 'KEYS';
KEYSPACES	: 'KEYSPACES';
LANGUAGE	: 'LANGUAGE';
LIST		: 'LIST';
LOGIN		: 'LOGIN';
MAP			: 'MAP';
NOLOGIN		: 'NOLOGIN';
NOSUPERUSER	: 'NOSUPERUSER';
OPTIONS		: 'OPTIONS';
PASSWORD	: 'PASSWORD';
PERMISSION	: 'PERMISSION';
PERMISSIONS	: 'PERMISSIONS';
RETURNS		: 'RETURNS';
ROLE		: 'ROLE';
ROLES		: 'ROLES';
SFUNC		: 'SFUNC';
SMALLINT	: 'SMALLINT';
STATIC		: 'STATIC';
STORAGE		: 'STORAGE';
STYPE		: 'STYPE';
SUPERUSER	: 'SUPERUSER';
TEXT		: 'TEXT';
TIME		: 'TIME';
TIMESTAMP	: 'TIMESTAMP';
TIMEUUID	: 'TIMEUUID';
TINYINT		: 'TINYINT';
TRIGGER		: 'TRIGGER';
TTL			: 'TTL';
TUPLE		: 'TUPLE';
TYPE		: 'TYPE';
USER		: 'USER';
USERS		: 'USERS';
UUID		: 'UUID';
VALUES		: 'VALUES';
VARCHAR		: 'VARCHAR';
VARINT		: 'VARINT';
WRITETIME	: 'WRITETIME';
nonReservedKeyword:
	AGGREGATE
	| ALL
	| AS
	| ASCII
	| BIGINT
	| BLOB
	| BOOLEAN
	| CALLED
	| CLUSTERING
	| COMPACT
	| CONTAINS
	| COUNT
	| COUNTER
	| CUSTOM
	| DATE
	| DECIMAL
	| DISTINCT
	| DOUBLE
	| DURATION
	| EXISTS
	| FILTERING
	| FINALFUNC
	| FLOAT
	| FUNCTION
	| FUNCTIONS
	| INET
	| INITCOND
	| INPUT
	| INT
	| JSON
	| KEY
	| KEYS
	| KEYSPACES
	| LANGUAGE
	| LIST
	| LOGIN
	| MAP
	| NOLOGIN
	| NOSUPERUSER
	| OPTIONS
	| PASSWORD
	| PERMISSION
	| PERMISSIONS
	| RETURNS
	| ROLE
	| ROLES
	| SFUNC
	| SMALLINT
	| STATIC
	| STORAGE
	| STYPE
	| SUPERUSER
	| TEXT
	| TIME
	| TIMESTAMP
	| TIMEUUID
	| TINYINT
	| TRIGGER
	| TTL
	| TUPLE
	| TYPE
	| USER
	| USERS
	| UUID
	| VALUES
	| VARCHAR
	| VARINT
	| WRITETIME;

// Identifier
// https://cassandra.apache.org/doc/stable/cassandra/cql/definitions.html#identifiers
identifier			: UNQUOTED_IDENTIFIER | QUOTED_IDENTIFIER | nonReservedKeyword;
UNQUOTED_IDENTIFIER	: [A-Z] [A-Z0-9_]*;
QUOTED_IDENTIFIER	: '"' (~'"' | '""')+ '"';

// Constants
// https://cassandra.apache.org/doc/stable/cassandra/cql/definitions.html#constants
constant				: string | integer | float | boolean | uuid | blob | NULL;
string					: QUOTED_STRING_CONSTANT | DOLLAR_STRING_CONSTANT;
QUOTED_STRING_CONSTANT	: '\'' ('\\' . | '\'' '\'' | ~( '\'' | '\\'))* '\'';
DOLLAR_STRING_CONSTANT	: '$$' (~'$' | '$' ~'$')* '$$';
integer					: INTEGER_CONSTANT;
INTEGER_CONSTANT		: '-'? DIGIT+;
float					: FLOAT_CONSTANT;
FLOAT_CONSTANT			: '-'? DIGIT+ ('.' DIGIT*)? ('E' [+-]? DIGIT+)? | NAN | INFINITY;
boolean					: BOOLEAN_CONSTANT;
BOOLEAN_CONSTANT		: 'TRUE' | 'FALSE';
uuid					: UUID_CONSTANT;
UUID_CONSTANT			: HEX_DIGIT_8 '-' HEX_DIGIT_4 '-' HEX_DIGIT_4 '-' HEX_DIGIT_4 '-' HEX_DIGIT_12;
blob					: BLOB_CONSTANT;
BLOB_CONSTANT			: '0' 'X' HEX_DIGIT+;

// Terms
// https://cassandra.apache.org/doc/stable/cassandra/cql/definitions.html#terms
// Arithmetic operation is merged into term to avoid mutually left-recursive grammar
term:
	constant									# constantTerm
	| literal									# literalTerm
	| funtionCall								# functionCallTerm
	| '-' term									# minusArithmeticOperation
	| term ('+' | '-' | '*' | '/' | '%') term	# twoOperandArithmeticOperation
	| typeHint									# typeHintTerm
	| bindMarker								# bindMarkerTerm;
literal		: collectionLiteral | udtLiteral | tupleLiteral;
funtionCall	: identifier '(' (term (',' term)*)? ')';
typeHint	: '(' cqlType ')' term;
bindMarker	: '?' | ':' identifier;

// Data types
// https://cassandra.apache.org/doc/stable/cassandra/cql/types.html
cqlType: nativeType | collectionType | userDefinedType | tupleType | customType;
nativeType:
	ASCII
	| BIGINT
	| BLOB
	| BOOLEAN
	| COUNTER
	| DATE
	| DECIMAL
	| DOUBLE
	| DURATION
	| FLOAT
	| INET
	| INT
	| SMALLINT
	| TEXT
	| TIME
	| TIMESTAMP
	| TIMEUUID
	| TINYINT
	| UUID
	| VARCHAR
	| VARINT;

// Data types: Collections
// https://cassandra.apache.org/doc/stable/cassandra/cql/types.html#collections
collectionType:
	MAP '<' cqlType ',' cqlType '>'	# mapCollectionType
	| SET '<' cqlType '>'			# setCollectionType
	| LIST '<' cqlType '>'			# listCollectionType;
collectionLiteral	: mapLiteral | setLiteral | listLiteral;
mapLiteral			: '{' (keyValuePair (',' keyValuePair)*)? '}';
keyValuePair		: term ':' term;
setLiteral			: '{' (term (',' term)*)? '}';
listLiteral			: '[' (term (',' term)*)? ']';

// Data types: User-defined types
// https://cassandra.apache.org/doc/stable/cassandra/cql/types.html#udts
userDefinedType	: udtName;
udtName			: (keyspaceName '.')? identifier;
createTypeStatement:
	CREATE TYPE (IF NOT EXISTS)? udtName '(' fieldDefinition (',' fieldDefinition)* ')';
fieldDefinition			: identifier cqlType;
udtLiteral				: '{' fieldValuePair (',' fieldValuePair)* '}';
fieldValuePair			: identifier ':' term;
alter_type_statement	: ALTER TYPE (IF EXISTS)? udtName alterTypeModification;
alterTypeModification:
	ADD (IF NOT EXISTS)? fieldDefinition
	| RENAME (IF EXISTS)? renamePair (AND renamePair)*;
renamePair			: identifier TO identifier;
drop_type_statement	: DROP TYPE (IF EXISTS)? udtName;

// Data types: Tuples
// https://cassandra.apache.org/doc/stable/cassandra/cql/types.html#tuples
tupleType		: TUPLE '<' cqlType (',' cqlType)* '>';
tupleLiteral	: '(' term (',' term)* ')';

// Data types: Custom
// https://cassandra.apache.org/doc/stable/cassandra/cql/types.html#custom-types
customType: string;

// Data definition
// https://cassandra.apache.org/doc/stable/cassandra/cql/ddl.html#common-definitions
keyspaceName	: name;
tableName		: (keyspaceName '.')? name;
name			: UNQUOTED_NAME | QUOTED_NAME | identifier;
UNQUOTED_NAME	: [A-Z_0-9]+;
QUOTED_NAME		: '"' UNQUOTED_NAME '"';
columnName		: identifier;
// Options are renamed, as 'options' is a reserved keyword in ANTRL
optionAssignments	: optionAssignment (AND optionAssignment)*;
optionAssignment	: optionName '=' (identifier | constant | mapLiteral);
optionName			: identifier;

// Statements
// https://cassandra.apache.org/doc/stable/cassandra/cql/definitions.html#statements
root: statement (';' statement)* ';'? EOF;
statement:
	ddlStatement
	//        | dml_statement
	//        | secondary_index_statement
	//        | materialized_view_statement
	//        | role_or_permission_statement
	//        | udf_statement
	| udtStatement;
//        | trigger_statement;
ddlStatement:
	//use_statement
	//        | create_keyspace_statement
	//        | alter_keyspace_statement
	//        | drop_keyspace_statement
	createTableStatement; //        | truncate_statement
//	dml_statement : select_statement
//    		| insert_statement
//    		| update_statement
//    		| delete_statement
//    		| batch_statement;
//    secondary_index_statement : create_index_statement
//    		| drop_index_statement;
//    materialized_view_statement : create_materialized_view_statement
//    		| drop_materialized_view_statement;
//    role_or_permission_statement : create_role_statement
//    		| alter_role_statement
//    		| drop_role_statement
//    		| grant_role_statement
//    		| revoke_role_statement
//    		| list_roles_statement
//    		| grant_permission_statement
//    		| revoke_permission_statement
//    		| list_permissions_statement
//    		| create_user_statement
//    		| alter_user_statement
//    		| drop_user_statement
//    		| list_users_statement;
//    udf_statement : create_function_statement
//    		| drop_function_statement
//    		| create_aggregate_statement
//    		| drop_aggregate_statement;
udtStatement: createTypeStatement | alter_type_statement | drop_type_statement;
//trigger_statement : create_trigger_statement
//		| drop_trigger_statement;

// CREATE TABLE
// https://cassandra.apache.org/doc/stable/cassandra/cql/ddl.html#create-table-statement
createTableStatement:
	CREATE TABLE (IF NOT EXISTS)? tableName '(' columnDefinition (',' columnDefinition)* (
		',' PRIMARY KEY '(' primaryKey ')'
	)? ')' (WITH tableOptions)?;
columnDefinition	: columnName cqlType STATIC? (PRIMARY KEY)?;
primaryKey			: partitionKey (',' clusteringColumns)?;
partitionKey		: columnName | ('(' columnName (',' columnName)* ')');
clusteringColumns	: columnName (',' columnName)*;
tableOptions: (COMPACT STORAGE (AND tableOptions)?)
	| (CLUSTERING ORDER BY '(' clusteringOrders ')' (AND tableOptions)?)
	| optionAssignments;
clusteringOrders	: clusteringOrder (',' clusteringOrder)*;
clusteringOrder		: columnName (ASC | DESC);