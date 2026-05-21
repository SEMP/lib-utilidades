package py.com.semp.lib.utilidades.data;

import java.util.List;

//FIXME revisar, mover a database
public final class TypedResult
{
	private final List<TypedRow> rows;
	
	public TypedResult(List<TypedRow> rows)
	{
		this.rows = rows;
	}
	
	public List<TypedRow> getRows()
	{
		return rows;
	}
	
	public TypedRow getFirstRow()
	{
		return rows.isEmpty() ? null : rows.get(0);
	}
}