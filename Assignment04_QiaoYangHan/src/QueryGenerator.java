import java.util.Map;

public class QueryGenerator {
	public static String getInsertQuery(String tableName, Map<String, String> columnValueMap) {
		StringBuilder insert = new StringBuilder("insert into " + tableName);
		StringBuilder columns = new StringBuilder("(");
		StringBuilder values = new StringBuilder("(");
		for(Map.Entry<String, String> entry : columnValueMap.entrySet()) {
			columns.append(entry.getKey() + ", ");
			values.append("\"" + entry.getValue() + "\", ");
		}
		if(columnValueMap.size() > 0) {
			columns.delete(columns.length()-2, columns.length()); // remove ", " from tail
			columns.append(')');
			values.delete(values.length()-2, values.length()); // as above
			values.append(')');
		}
		insert.append(" " + columns.toString() + " values " + values.toString());
		return insert.toString();
	}
	
	public static void main(String[] args) {
		java.util.List<Object> ol = new java.util.ArrayList<>();
		ol.add("Hello");
		ol.add(new Integer(999));
		for(Object o : ol) {
			System.out.println(o.getClass() == String.class ? "1" : 0);
		}
	}
}