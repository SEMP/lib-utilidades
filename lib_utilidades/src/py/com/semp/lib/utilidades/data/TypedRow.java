package py.com.semp.lib.utilidades.data;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;
import py.com.semp.lib.utilidades.utilities.Utilities;

//FIXME Eliminar clase
public final class TypedRow
{
	private final Map<String, TypedValue<?>> columns;
	
	public TypedRow(Map<String, TypedValue<?>> columns)
	{
		if(columns == null)
		{
			StringBuilder methodName = new StringBuilder();
			
			methodName.append("[columns] ");
			methodName.append(this.getClass().getSimpleName());
			methodName.append("::");
			methodName.append(this.getClass().getSimpleName());
			methodName.append("(Map<String, ");
			methodName.append(TypedValue.class.getSimpleName());
			methodName.append("<?>> columns)");
			
			String errorMessage = MessageUtil.getMessage(Messages.NULL_VALUES_NOT_ALLOWED_ERROR, methodName.toString());
			
			throw new NullPointerException(errorMessage);
		}
		
		this.columns = columns;
	}
	
	public Map<String, TypedValue<?>> getColumns()
	{
		return columns;
	}
	
	public TypedValue<?> getColumn(String column)
	{
		return columns.get(column);
	}
	
	public <T> T getValue(String column)
	{
		TypedValue<?> typedValue = columns.get(column);
		
		if(typedValue == null)
		{
			return null;
		}
		
		@SuppressWarnings("unchecked")
		T value = (T)typedValue.getValue();
		
		return value;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj) return true;
		
		if(!(obj instanceof TypedRow)) return false;
		
		TypedRow typedRow = (TypedRow)obj;
		
		return this.columns.equals(typedRow.columns);
	}
	
	@Override
	public int hashCode()
	{
		return this.columns.hashCode();
	}
	
	public String getRowDetails()
	{
		if(this.columns.isEmpty())
		{
			return "";
		}
		
		String lineSeparator = System.lineSeparator();
		
		StringBuilder sb = new StringBuilder();
		
		for(Map.Entry<String, TypedValue<?>> entry : this.columns.entrySet())
		{
			String columnName = entry.getKey();
			TypedValue<?> columnValue = entry.getValue();
			
			if(columnValue == null)
			{
				sb.append(columnName).append(": ").append(lineSeparator);
				
				continue;
			}
			
			Class<?> type = columnValue.getType();
			Object value = columnValue.getValue();
			
			sb.append(columnName).append(": ");
			
			if(type != null)
			{
				sb.append(Utilities.coalesce(type.getCanonicalName(), type.getName())).append(" -> ");
			}
			
			sb.append(value).append(lineSeparator);
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
		
		StringJoiner joiner = new StringJoiner(" | ");
		
		for(TypedValue<?> columnValue : this.columns.values())
		{
			if(columnValue == null)
			{
				joiner.add("null");
			}
			else
			{
				String stringValue = Objects.toString(columnValue.getValue());
				
				joiner.add(stringValue);
			}
		}
		
		return joiner.toString();
	}
}