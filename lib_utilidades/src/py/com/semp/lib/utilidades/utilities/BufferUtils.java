package py.com.semp.lib.utilidades.utilities;

import java.util.List;

import py.com.semp.lib.utilidades.data.CircularByteBuffer;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

public final class BufferUtils
{
	private BufferUtils()
	{
		super();
		
		String errorMessage = MessageUtil.getMessage(Messages.DONT_INSTANTIATE, this.getClass().getName());
		
		throw new AssertionError(errorMessage);
	}

	/**
	 * Extrae los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos que se encuentran entre las cabeceras.
	 */
	public static List<byte[]> extractAll(byte[] data, String startHeader, String endHeader)
	{
		return extractAll(data, startHeader.getBytes(), endHeader.getBytes());
	}

	/**
	 * Extrae los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * Datos que se encuentran entre las cabeceras.
	 */
	public static byte[] extractOne(byte[] data, String startHeader, String endHeader)
	{
		return extractOne(data, startHeader.getBytes(), endHeader.getBytes());
	}

	/**
	 * Extrae los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos que se encuentran entre las cabeceras.
	 */
	public static List<byte[]> extractAll(byte[] data, byte[] startHeader, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(data);
		
		return circularByteBuffer.extractAll(startHeader, endHeader);
	}

	/**
	 * Extrae los datos que se encuentran entre las cabeceras
	 * iniciales y finales. La extracci&oacute;n incluye las cabeceras.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * @param startHeader
	 * cabecera inicial.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * Datos que se encuentran entre las cabeceras.
	 */
	public static byte[] extractOne(byte[] data, byte[] startHeader, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(data);
		
		return circularByteBuffer.extractOne(startHeader, endHeader);
	}

	/**
	 * Extrae los datos que se encuentran finalizados por
	 * una cabecera final.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos extraidos.
	 */
	public static List<byte[]> extractAll(byte[] data, String endHeader)
	{
		return extractAll(data, endHeader.getBytes());
	}

	/**
	 * Extrae los datos que se encuentran finalizados por
	 * una cabecera final.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * lista de datos extraidos.
	 */
	public static List<byte[]> extractAll(byte[] data, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(data);
		
		return circularByteBuffer.extractAll(endHeader);
	}

	/**
	 * Extrae los primeros datos que se encuentran finalizados por
	 * una cabecera final.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * datos extraidos.
	 */
	public static byte[] extractOne(byte[] data, String endHeader)
	{
		return extractOne(data, endHeader.getBytes());
	}

	/**
	 * Extrae los primeros datos que se encuentran finalizados por
	 * una cabecera final.
	 * 
	 * @param data
	 * array de bytes de donde se desea extraer los datos.
	 * La extracci&oacute;n incluye la cabecera final.
	 * @param endHeader
	 * cabecera final.
	 * @return
	 * datos extraidos.
	 */
	public static byte[] extractOne(byte[] data, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(data);
		
		return circularByteBuffer.extractOne(endHeader);
	}
}