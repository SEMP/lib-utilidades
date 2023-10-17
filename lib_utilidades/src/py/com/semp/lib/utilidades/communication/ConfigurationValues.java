package py.com.semp.lib.utilidades.communication;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import py.com.lib.util.templates.GenericEnum;
import py.com.semp.lib.utilidades.data.Pair;

/**
 * Clase de configuraci&oacute;n para la comunicaci&oacute;n.
 * 
 * @author Sergio Morel
 */
public abstract class ConfigurationValues
{
	/**
	 * Par&aacute;metros.
	 * - <b>String name:</b> nombre del par&aacute;metro.<br>
	 * - <b>Class&lt;?&gt type:</b> tipo de dato del par&aacute;metro.<br>
	 * - <b>Object value:</b> valor del par&aacute;metro.<br>
	 */
	private final Map<String, Pair<Class<?>, Object>> parameters;
	
	/**
	 * Par&aacute;metros requeridos.
	 * - <b>Class&lt;?&gt type:</b> tipo de dato del par&aacute;metro.<br>
	 * - <b>String name:</b> nombre del par&aacute;metro.<br>
	 */
	private final List<Pair<Class<?>, String>> requiredParameters;
	
	/**
	 * Par&aacute;metros opcionales.
	 * - <b>Class&lt;?&gt type:</b> tipo de dato del par&aacute;metro.<br>
	 * - <b>String name:</b> nombre del par&aacute;metro.<br>
	 */
	private final List<Pair<Class<?>, String>> optionalParameters;
	
	public ConfigurationValues()
	{
		this.parameters = Collections.synchronizedMap(new HashMap<>());
		this.requiredParameters = Collections.synchronizedList(new LinkedList<>());
		this.optionalParameters = Collections.synchronizedList(new LinkedList<>());
		
		this.setRequiredParameters();
		this.setOptionalParameters();
		this.setDefaultValues();
	}
	
	/**
	 * Guardar un par&aacute;metro.
	 * 
	 * @param type
	 * typo de dato del par&aacute;metro.
	 * @param name
	 * nombre del par&aacute;metro.
	 * @param value
	 * valor del par&aacute;metro.
	 * @return
	 * - Objeto de configuraci&oacute;n utilizado para llamar al m&eacute;todo.
	 */
	public ConfigurationValues setParameter(String name, Object value)
	{
		if(value == null)
		{
			StringBuilder errorMessage = new StringBuilder();
			
			errorMessage.append("No se puede determinar el tipo de dato de: ");
			errorMessage.append(value);
			
			throw new IllegalArgumentException(errorMessage.toString());
		}
		
		Class<?> type = value.getClass();
		Pair<Class<?>, Object> pair = new Pair<Class<?>, Object>(type, value);
		
		this.setParameter(name, pair);
		
		return this;
	}
	
	/**
	 * Guardar un par&aacute;metro.
	 * 
	 * @param type
	 * typo de dato del par&aacute;metro.
	 * @param name
	 * nombre del par&aacute;metro.
	 * @param value
	 * valor del par&aacute;metro.
	 * @return
	 * - Objeto de configuraci&oacute;n utilizado para llamar al m&eacute;todo.
	 */
	public ConfigurationValues setParameter(Class<?> type, String name, Object value)
	{
		Pair<Class<?>, Object> pair = new Pair<Class<?>, Object>(type, value);
		
		this.setParameter(name, pair);
		
		return this;
	}
	
	/**
	 * Obtiene el tipo de dato y el valor del par&aacute;metro que conicide con el nombre.
	 * @param name
	 * nombre del par&aacute;metro que se quiere obtener.
	 * @return
	 * - Objeto del tipo {@link Pair}. El primer valor corresponde al tipo de dato, y el segundo al valor del par&aacute;metro.<br>
	 * - <b>null</b> en caso de que no se encuentre ningun par&aacute;metro con el nombre buscado.
	 */
	public Pair<Class<?>, Object> getParameter(String name)
	{
		Pair<Class<?>, Object> pair;
		
		synchronized(this.parameters)
		{
			pair = this.parameters.get(name);
			
			this.parameters.notifyAll();
		}
		
		return pair;
	}
	
	public void setParameter(String name, Pair<Class<?>, Object> pair)
	{
		synchronized(this.parameters)
		{
			this.parameters.put(name, pair);
			
			this.parameters.notifyAll();
		}
	}
	
	/**
	 * Obtiene el valor del par&aacute;metro que conicide con el nombre.
	 * @param
	 * tipo de dato del par&aacute;metro.
	 * @param name
	 * nombre del par&aacute;metro que se quiere obtener.
	 * @return
	 * - Valor del par&aacute;metro.<br>
	 * - <b>null</b> en caso de que no se encuentre ningun par&aacute;metro con el nombre buscado.
	 */
	public <T> T getParameter(Class<T> type, String name)
	{
		Pair<Class<?>, Object> parameter = this.getParameter(name);
		
		if(parameter == null)
		{
			return null;
		}
		
		return type.cast(parameter.getSecond());
	}
	
	/**
	 * Obtiene el mapa de par&aacute;metros.<br>
	 * Campos:<br>
	 * - <b>String name:</b> nombre del par&aacute;metro.<br>
	 * - <b>Class&lt;?&gt type:</b> tipo de dato del par&aacute;metro.<br>
	 * - <b>Object value:</b> valor del par&aacute;metro.<br> 
	 * 
	 * @return
	 * mapa de par&aacute;metros.
	 */
	public Map<String, Pair<Class<?>, Object>> getParameters()
	{
		return this.parameters;
	}
	
//	public <T extends GenericEnum> T getEnum(Class<T> type)
//	{
//		String name = getNameFromClass(type);
//		
//		Pair<Class<?>,Object> parameter = this.getParameter(name);
//		
//		@SuppressWarnings("unchecked")
//		T value = (T) parameter.getSecond();
//		
//		return value;
//	}
//	
//	public <T extends GenericEnum> ConfigurationValues setEnum(T enumeration)
//	{
//		Class<?> type = enumeration.getClass();
//		String name = getNameFromClass(type);
//		
//		this.setParameter(type, name, enumeration);
//		
//		return this;
//	}
	
	/**
	 * Verifica se se cuenta con los par&aacute;metros requeridos con los tipos de dato correctos..
	 * 
	 * @return
	 * - <b>true</b> si los par&aacute;metro est&aacute;n correctos.<br>
	 * - <b>false</b> si los par&aacute;metro no est&aacute;n correctos.
	 */
	public boolean checkRequiredParameters()
	{
		List<Pair<Class<?>,String>> requiredParameters = this.getRequiredParameters();
		
		synchronized(requiredParameters)
		{
			for(Pair<Class<?>, String> requiredParameter : requiredParameters)
			{
				String requiredName = requiredParameter.getSecond();
				Class<?> requiredType = requiredParameter.getFirst();
				
				Pair<Class<?>,Object> parameter = this.getParameter(requiredName);
				
				if(parameter == null)
				{
					return false;
				}
				
				Class<?> type = parameter.getFirst();
				
				if(!requiredType.isAssignableFrom(type))
				{
					return false;
				}
			}
			
			requiredParameters.notifyAll();
		}
		
		return true;
	}
	
	/**
	 * Establece los valores de configuraci&oacute;n requeridos.
	 */
	protected abstract void setRequiredParameters();
	
	/**
	 * Establece los valores de configuraci&oacute;n opcionales.
	 */
	protected abstract void setOptionalParameters();
	
	/**
	 * Establece los valores de configuraci&oacute;n predeterminados.
	 */
	protected abstract void setDefaultValues();
	
	/**
	 * Obtiene una lista de par&aacute;metros requeridos para la comunicaci&oacute;n.
	 * 
	 * @return
	 * Lista de pares del tipo {@link Pair} con los siguientes campos:<br>
	 * - <b>tipo</b> de dato del par&aacute;metro.<br>
	 * - <b>nombre</b> del par&aacute;metro.
	 */
	public List<Pair<Class<?>, String>> getRequiredParameters()
	{
		return this.requiredParameters;
	}
	
	/**
	 * Agrega un nuevo par&aacute;metro requerido.
	 * 
	 * @param parameter
	 * par&aacute;metro con los siguientes campos:
	 * - <b>tipo</b> de dato del par&aacute;metro.<br>
	 * - <b>nombre</b> del par&aacute;metro.
	 * @return
	 * - Objeto de configuraci&oacute;n utilizado para llamar al m&eacute;todo.
	 */
	public ConfigurationValues addRequiredParameter(Pair<Class<?>, String> parameter)
	{
		synchronized(this.requiredParameters)
		{
			this.requiredParameters.add(parameter);
			
			this.requiredParameters.notifyAll();
		}
		
		return this;
	}
	
	/**
	 * Agrega un nuevo par&aacute;metro requerido.
	 * 
	 * @param parameterType
	 * tipo de dato del par&aacute;metro
	 * @return
	 * - Objeto de configuraci&oacute;n utilizado para llamar al m&eacute;todo.
	 */
	public ConfigurationValues addRequiredParameter(Class<?> parameterType)
	{
		Pair<Class<?>, String> parameter = getClassTypeNamePair(parameterType);
		
		return this.addRequiredParameter(parameter);
	}
	
	/**
	 * Agrega un nuevo par&aacute;metro requerido.
	 * 
	 * @param parameterType
	 * tipo de dato del par&aacute;metro
	 * @param parameterName
	 * nombre del par&aacute;metro.
	 * @return
	 * - Objeto de configuraci&oacute;n utilizado para llamar al m&eacute;todo.
	 */
	public ConfigurationValues addRequiredParameter(Class<?> parameterType, String parameterName)
	{
		Pair<Class<?>, String> parameter = new Pair<Class<?>, String>(parameterType, parameterName);
		
		return this.addRequiredParameter(parameter);
	}
	
	/**
	 * Obtiene una lista de par&aacute;metros opcionales para la configuraci&oacute;n.
	 * 
	 * @return
	 * Lista de pares del tipo {@link Pair} con los siguientes campos:<br>
	 * - <b>tipo</b> de dato del par&aacute;metro.<br>
	 * - <b>nombre</b> del par&aacute;metro.
	 */
	public List<Pair<Class<?>, String>> getOptionalParameters()
	{
		return optionalParameters;
	}
	
	/**
	 * Agrega un nuevo par&aacute;metro opcional.
	 * 
	 * @param parameterType
	 * tipo de dato del par&aacute;metro
	 * @param parameterName
	 * nombre del par&aacute;metro.
	 * @return
	 * - Objeto de configuraci&oacute;n utilizado para llamar al m&eacute;todo.
	 */
	public ConfigurationValues addOptionalParameter(Pair<Class<?>, String> parameter)
	{
		synchronized(this.optionalParameters)
		{
			this.optionalParameters.add(parameter);
			
			this.optionalParameters.notifyAll();
		}
		
		return this;
	}
	
	/**
	 * Agrega un nuevo par&aacute;metro opcional.
	 * 
	 * @param parameterType
	 * tipo de dato del par&aacute;metro
	 * @return
	 * - Objeto de configuraci&oacute;n utilizado para llamar al m&eacute;todo.
	 */
	public ConfigurationValues addOptionalParameter(Class<?> parameterType)
	{
		Pair<Class<?>, String> parameter = getClassTypeNamePair(parameterType);
		
		return this.addOptionalParameter(parameter);
	}
	
	/**
	 * Agrega un nuevo par&aacute;metro opcional.
	 * 
	 * @param parameterType
	 * tipo de dato del par&aacute;metro
	 * @param parameterName
	 * nombre del par&aacute;metro.
	 * @return
	 * - Objeto de configuraci&oacute;n utilizado para llamar al m&eacute;todo.
	 */
	public ConfigurationValues addOptionalParameter(Class<?> parameterType, String parameterName)
	{
		Pair<Class<?>, String> parameter = new Pair<Class<?>, String>(parameterType, parameterName);
		
		return this.addOptionalParameter(parameter);
	}
	
	public static Pair<Class<?>, String> getClassTypeNamePair(Class<?> type)
	{
		String name = getNameFromClass(type);
		
		return new Pair<Class<?>, String>(type, name);
	}
	
	private static String getNameFromClass(Class<?> type)
	{
		String className = type.getSimpleName();
		
		if(className == null || className.isEmpty())
		{
			StringBuilder errorMessage = new StringBuilder();
			
			errorMessage.append("No se puede determinar el nombre de la clase: ");
			errorMessage.append(type);
			
			throw new IllegalArgumentException(errorMessage.toString());
		}
		
		StringBuilder name = new StringBuilder();
		
		name.append(className.substring(0, 1).toLowerCase());
		
		if(className.length() > 1)
		{
			name.append(className.substring(1));
		}
		
		return name.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		Map<String, Pair<Class<?>, Object>> parameters = this.getParameters();
		
		synchronized(parameters)
		{
			for(Map.Entry<String, Pair<Class<?>, Object>> entry : parameters.entrySet())
			{
				Pair<Class<?>, Object> parameter = entry.getValue();
				
				String name = entry.getKey();
				Class<?> type = parameter.getFirst();
				Object value = parameter.getSecond();
				
				sb.append(" - ");
				sb.append(type.getCanonicalName());
				sb.append(" ");
				sb.append(name);
				sb.append(" = ");
				sb.append(value  == null ? "null" : value);
				sb.append("\n");
			}
			
			parameters.notifyAll();
		}
		
		return sb.toString();
	}
}