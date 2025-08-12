package py.com.semp.lib.utilidades.data;

import java.util.Map;

//FIXME revisar
public final class TypedRow
{
	private final Map<String, TypedValue<?>> columns;
	
	public TypedRow(Map<String, TypedValue<?>> columns)
	{
		this.columns = columns;
	}
	
	public Map<String, TypedValue<?>> getColumns()
	{
		return columns;
	}
	
	public TypedValue<?> get(String column)
	{
		return columns.get(column);
	}
}