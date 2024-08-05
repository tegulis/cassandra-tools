package net.tegulis.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CreateTableDefinition {

	public static class Column {

		public enum ColumnType {
			PARTITION, CLUSTERING, REGULAR, STATIC
		}

		public enum DataType {
			ASCII, BIGINT(8), BLOB, BOOLEAN(1), COUNTER(8), DATE(4), DECIMAL, DOUBLE(8), DURATION, FLOAT(4), INET, INT(4), SMALLINT(2), TEXT, TIME(8), TIMESTAMP(8), TIMEUUID(16), TINYINT(1), UUID(16), VARCHAR, VARINT;

			public final int size;

			DataType() {
				this(0);
			}

			DataType(int size) {
				this.size = size;
			}

		}

		public final String name;
		public final ColumnType columnType;
		public final DataType dataType;
		private long sizeOverride;
		private long cardinality;

		public Column(String name, DataType dataType, ColumnType columnType) {
			this.name = name;
			this.dataType = dataType;
			this.columnType = columnType;
		}

		public long getSize() throws IllegalArgumentException {
			if (dataType.size <= 0 && sizeOverride <= 0) {
				throw new IllegalArgumentException("Size cannot be zero or negative for column " + name);
			}
			return sizeOverride > 0 ? sizeOverride : dataType.size;
		}

		public String getFormattedSize() {
			String size = "INVALID";
			try {
				size = String.format("%,d", getSize());
			} catch (Exception ignored) {}
			return size;
		}

		public long getCardinality() throws IllegalArgumentException {
			if (columnType == ColumnType.REGULAR) {
				if (cardinality > 1) {
					throw new IllegalArgumentException("Cardinality cannot be greater than 1 for regular column " + name);
				} else {
					return 1;
				}
			}
			if (cardinality <= 0) {
				throw new IllegalArgumentException("Cardinality cannot be zero or negative for column " + name);
			}
			return cardinality;
		}

		public String getFormattedCardinality() {
			String cardinality = "INVALID";
			try {
				cardinality = String.format("%,d", getCardinality());
			} catch (Exception ignored) {}
			return cardinality;
		}

		private static final Pattern cardinalityPattern = Pattern.compile("(cardinality|count)\\s*=\\s*(?<cardinality>[0-9_]+)", Pattern.CASE_INSENSITIVE);
		private static final Pattern sizePattern = Pattern.compile("size\\s*=\\s*(?<size>[0-9_]+)", Pattern.CASE_INSENSITIVE);

		public void tryEnrichFromComment(String comment) {
			Matcher cardinalityMatcher = cardinalityPattern.matcher(comment);
			if (cardinalityMatcher.find()) {
				String cardinalityString = cardinalityMatcher.group("cardinality");
				this.cardinality = Integer.parseInt(cardinalityString.replace("_", ""));
			}
			Matcher sizeMatcher = sizePattern.matcher(comment);
			if (sizeMatcher.find()) {
				String sizeString = sizeMatcher.group("size");
				this.sizeOverride = Integer.parseInt(sizeString.replace("_", ""));
			}
		}

	}

	public final String keyspace;
	public final String tableName;
	public final int firstLineNumber;

	public final List<Column> columns = new ArrayList<Column>();

	public CreateTableDefinition(String keyspace, String tableName, int firstLineNumber) {
		this.keyspace = keyspace;
		this.tableName = tableName;
		this.firstLineNumber = firstLineNumber;
	}

	public String getFullTableName() {
		return keyspace.isEmpty() ? tableName : keyspace + "." + tableName;
	}

	public List<Column> getColumnsByFilter(Function<Column, Boolean> filter) {
		return columns.stream().filter(filter::apply).toList();
	}

	public List<Column> getColumnsByType(Column.ColumnType... columnTypes) {
		return getColumnsByFilter(column -> Arrays.asList(columnTypes).contains(column.columnType));
	}

	public long getPartitionCount() throws IllegalArgumentException {
		List<Column> partitionColumns = getColumnsByType(Column.ColumnType.PARTITION);
		if (partitionColumns.isEmpty()) {
			throw new IllegalArgumentException("No partition column(s) found in table " + getFullTableName());
		}
		long count = 1;
		for (Column column : partitionColumns) {
			count *= column.getCardinality();
		}
		return count;
	}

	public String getFormattedPartitionCount() {
		String partitionCount = "INVALID";
		try {
			partitionCount = String.format("%,d", getPartitionCount());
		} catch (IllegalArgumentException ignored) {}
		return partitionCount;
	}

	public long getPartitionRowCount() throws IllegalArgumentException {
		return getColumnsByType(Column.ColumnType.CLUSTERING).stream()
				.map(Column::getCardinality)
				.reduce((a, b) -> a * b)
				.orElse(1L);
	}

	public String getFormattedPartitionRowCount() {
		String partitionRowCount = "INVALID";
		try {
			partitionRowCount = String.format("%,d", getPartitionRowCount());
		} catch (IllegalArgumentException ignored) {}
		return partitionRowCount;
	}

	public long getPartitionCellCount() throws IllegalArgumentException {
		// The formula from https://cassandra.apache.org/doc/stable/cassandra/data_modeling/data_modeling_refining.html
		//   Nv = Nr * (Nc − Npk − Ns) + Ns
		// will give zero for partitions that do not have regular or static columns.
		long Nr = getPartitionRowCount();
		long Nc = columns.size();
		long Npk = getColumnsByType(Column.ColumnType.PARTITION, Column.ColumnType.CLUSTERING).size();
		long Ns = getColumnsByType(Column.ColumnType.STATIC).size();
		return Nr * (Nc - Npk - Ns) + Ns;
	}

	public String getFormattedPartitionCellCount() {
		String partitionCellCount = "INVALID";
		try {
			partitionCellCount = String.format("%,d", getPartitionCellCount());
		} catch (IllegalArgumentException ignored) {}
		return partitionCellCount;
	}

	public long getPartitionSize() {
		// Formula from https://cassandra.apache.org/doc/stable/cassandra/data_modeling/data_modeling_refining.html
		//   St = sizeOf(ck) + sizeOf(cs) + Nr * (ksizeOf(cr) + sizeOf(cc)) + Nv * sizeOf(tavg)
		long ck = getColumnsByType(Column.ColumnType.PARTITION).stream()
				.map(Column::getSize)
				.reduce(Long::sum)
				.orElse(0L);
		long cs = getColumnsByType(Column.ColumnType.STATIC).stream()
				.map(Column::getSize)
				.reduce(Long::sum)
				.orElse(0L);
		long Nr = getPartitionRowCount();
		long cr = getColumnsByType(Column.ColumnType.REGULAR).stream()
				.map(Column::getSize)
				.reduce(Long::sum)
				.orElse(0L);
		long cc = getColumnsByType(Column.ColumnType.CLUSTERING).stream()
				.map(Column::getSize)
				.reduce(Long::sum)
				.orElse(0L);
		long Nv = getPartitionCellCount();
		long tavg = 8L;
		return ck + cs + Nr * (cr + cc) + Nv * tavg;
	}

	public String getFormattedPartitionSize() {
		String partitionSize = "INVALID";
		try {
			partitionSize = String.format("%,d", getPartitionSize());
		} catch (IllegalArgumentException ignored) {}
		return partitionSize;
	}

	public long getTotalSize() {
		return getPartitionCount() * getPartitionSize();
	}

	public String getFormattedTotalSize() {
		String totalSize = "INVALID";
		try {
			totalSize = String.format("%,d", getTotalSize());
		} catch (IllegalArgumentException ignored) {}
		return totalSize;
	}

	@Override
	public String toString() {
		String partitionCount = getFormattedPartitionCount();
		String partitionSize = getFormattedPartitionSize();
		String totalSize = getFormattedTotalSize();
		String partitionRowCount = getFormattedPartitionRowCount();
		String partitionCellCount = getFormattedPartitionCellCount();
		int padding = Stream.of(partitionCount, partitionSize, totalSize, partitionRowCount, partitionCellCount)
				.map(String::length)
				.reduce(Integer::max)
				.orElse(0);
		StringBuilder sb = new StringBuilder();
		sb.append(" CREATE TABLE ").append(getFullTableName()).append(" {");
		sb.append(String.format("\n  PARTITION COUNT      = %" + padding + "s", partitionCount));
		sb.append(String.format("\n  PARTITION SIZE       = %" + padding + "s bytes", partitionSize));
		sb.append(String.format("\n  TOTAL SIZE           = %" + padding + "s bytes", totalSize));
		sb.append(String.format("\n  ROWS PER PARTITION   = %" + padding + "s", partitionRowCount));
		sb.append(String.format("\n  CELLS PER PARTITION  = %" + padding + "s (limit 2 billion)", partitionCellCount));
		sb.append("\n  COLUMNS:");
		int namePadding = columns.stream()
				.map(column -> column.name.length())
				.reduce(Integer::max)
				.orElse(0);
		int dataTypePadding = columns.stream()
				.map(column -> column.dataType.toString().length())
				.reduce(Integer::max)
				.orElse(0);
		int typePadding = columns.stream()
				.map(column -> column.columnType.toString().length())
				.reduce(Integer::max)
				.orElse(0);
		int cardinalityPadding = columns.stream()
				.map(column -> column.getFormattedCardinality().length())
				.reduce(Integer::max)
				.orElse(0);
		int sizePadding = columns.stream()
				.map(column -> column.getFormattedSize().length())
				.reduce(Integer::max)
				.orElse(0);
		for (Column column : columns) {
			String cardinality = column.getFormattedCardinality();
			String size = column.getFormattedSize();
			sb.append(("\n    %-" + namePadding + "s"
							+ " %-" + dataTypePadding + "s"
							+ " %-" + typePadding + "s"
							+ " cardinality = %" + cardinalityPadding + "s"
							+ ", size = %" + sizePadding + "s"
					).formatted(column.name, column.dataType, column.columnType, cardinality, size));
		}
		sb.append("\n}");
		return sb.toString();
	}

}
