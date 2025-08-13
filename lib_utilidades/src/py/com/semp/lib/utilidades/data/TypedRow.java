package py.com.semp.lib.utilidades.data;

import java.util.Map;

import py.com.semp.lib.utilidades.utilities.Utilities;

//FIXME continuar modificando
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
	
	public String getRowDetails()
	{
		if(this.columns == null || this.columns.isEmpty())
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		for(Map.Entry<String, TypedValue<?>> entry : this.columns.entrySet())
		{
			String columnName = entry.getKey();
			TypedValue<?> columnValue = entry.getValue();
			Class<?> type = columnValue.getType();
			Object value = columnValue.getValue();
			
			sb.append(columnName).append(": ");
			sb.append(Utilities.coalesce(type.getCanonicalName(), type.getName())).append(" -> ");
			sb.append(value).append("\n");
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		if(this.columns == null || this.columns.isEmpty())
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		
		for(TypedValue<?> columnValue : this.columns.values())
		{
			Object value = columnValue.getValue();
			
			sb.append(" | ");
			sb.append(value);
		}
		
		sb.append(" |");
		
		return sb.toString();
	}
}