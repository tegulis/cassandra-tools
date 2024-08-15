# Cassandra Tools

Sketch of a little tool, that reads Cassandra CQL to find `CREATE TABLE` statements and enriches them with comments
about size and cardinality, if information is available (in comments).

## Example input

```cassandraql
// before comment
CREATE TABLE k.t (
  pk1 text, // pk1 cardinality = 5, size = 512
  pk2 int, // pk2 count = 10
  // inside comment
  c1 text, // c1 size = 1024, count = 10
  c2 int, // c2 cardinality = 10
  n1 int,
  n2 int,
  PRIMARY KEY ((pk1, pk2), c1, c2)
  // inside comment
);
// after comment
```

## Example output

```cassandraql
// before comment
// CREATE TABLE k.t {
//   PARTITION COUNT      =        50
//   PARTITION SIZE       =   105,716 bytes
//   TOTAL SIZE           = 5,285,800 bytes
//   ROWS PER PARTITION   =       100
//   CELLS PER PARTITION  =       200 (limit 2 billion)
//   COLUMNS:
//     pk1 TEXT PARTITION  cardinality =  5, size =   512
//     pk2 INT  PARTITION  cardinality = 10, size =     4
//     c1  TEXT CLUSTERING cardinality = 10, size = 1,024
//     c2  INT  CLUSTERING cardinality = 10, size =     4
//     n1  INT  REGULAR    cardinality =  1, size =     4
//     n2  INT  REGULAR    cardinality =  1, size =     4
// }
CREATE TABLE k.t (
  pk1 text, // pk1 cardinality = 5, size = 512
  pk2 int, // pk2 count = 10
  // inside comment
  c1 text, // c1 size = 1024, count = 10
  c2 int, // c2 cardinality = 10
  n1 int,
  n2 int,
  PRIMARY KEY ((pk1, pk2), c1, c2)
  // inside comment
);
// after comment
```
