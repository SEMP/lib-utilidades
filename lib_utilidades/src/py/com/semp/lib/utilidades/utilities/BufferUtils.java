package py.com.semp.lib.utilidades.utilities;

import java.nio.charset.StandardCharsets;
import java.util.List;

import py.com.semp.lib.utilidades.data.CircularByteBuffer;
import py.com.semp.lib.utilidades.internal.MessageUtil;
import py.com.semp.lib.utilidades.internal.Messages;

/**
 * Class with static methods with utilities for managing byte buffers.
 * 
 * @author Sergio Morel
 */
public final class BufferUtils
{
	private BufferUtils()
	{
		super();
		
		String errorMessage = MessageUtil.getMessage(Messages.DONT_INSTANTIATE, this.getClass().getName());
		
		throw new AssertionError(errorMessage);
	}
	
	/**
	 * Extracts from the buffer the first segment finalized by the end header.
	 * The segment of data extracted includes the end header.
	 * 
	 * @param buffer
	 * - buffer from which the data would be extracted.
	 * @param endHeader
	 * - The ending header in String format.
	 * @return
	 * - The first segment of data found, including the header.
	 * @author Sergio Morel
	 */
	public static byte[] extractOne(byte[] buffer, String endHeader)
	{
		return extractOne(buffer, endHeader.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Extracts from the buffer the first segment finalized by the end header.
	 * The segment of data extracted includes the end header.
	 * 
	 * @param buffer
	 * - buffer from which the data would be extracted.
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - The first segment of data found, including the header.
	 * @author Sergio Morel
	 */
	public static byte[] extractOne(byte[] buffer, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(buffer);
		
		return circularByteBuffer.extractOne(endHeader);
	}
	
	/**
	 * Extracts from the buffer all the data segments terminated by an ending header.
	 * Each segment of data extracted includes the end header.
	 * 
	 * @param buffer
	 * - buffer from which the data would be extracted.
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - A list containing segments of data terminated by the end header, including the header.
	 * @author Sergio Morel
	 */
	public static List<byte[]> extractAll(byte[] buffer, String endHeader)
	{
		return extractAll(buffer, endHeader.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Extracts from the buffer all the data segments terminated by an ending header.
	 * Each segment of data extracted includes the end header.
	 * 
	 * @param buffer
	 * - buffer from which the data would be extracted.
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - A list containing segments of data terminated by the end header, including the header.
	 * @author Sergio Morel
	 */
	public static List<byte[]> extractAll(byte[] buffer, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(buffer);
		
		return circularByteBuffer.extractAll(endHeader);
	}
	
	/**
	 * Extracts from the buffer the first segment found between occurrences of the start header and end header.
	 * The segment of data extracted includes both the start and end headers.
	 * 
	 * @param buffer
	 * - buffer from which the data would be extracted.
	 * @param startHeader
	 * - The starting header in String format.
	 * @param endHeader
	 * - The ending header in String format.
	 * @return
	 * - The first segment of data found between the headers, including the headers.
	 * @author Sergio Morel
	 */
	public static byte[] extractOne(byte[] buffer, String startHeader, String endHeader)
	{
		return extractOne(buffer, startHeader.getBytes(StandardCharsets.UTF_8), endHeader.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Extracts from the buffer the first data segment found between occurrences of the start header and end header.
	 * The segment of data extracted includes both the start and end headers.
	 * 
	 * @param buffer
	 * - buffer from which the data would be extracted.
	 * @param startHeader
	 * - The starting header.
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - The first segment of data found between the headers, including the headers.
	 * @author Sergio Morel
	 */
	public static byte[] extractOne(byte[] buffer, byte[] startHeader, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(buffer);
		
		return circularByteBuffer.extractOne(startHeader, endHeader);
	}
	
	/**
	 * Extracts from the buffer all the data segments found between occurrences of the start header and end header.
	 * Each segment of data extracted includes both the start and end headers.
	 * 
	 * @param buffer
	 * - buffer from which the data would be extracted.
	 * @param startHeader
	 * - The starting header in String format.
	 * @param endHeader
	 * - The ending header in String format.
	 * @return
	 * - A list containing segments of data found between the headers, including the headers.
	 * @author Sergio Morel
	 */
	public static List<byte[]> extractAll(byte[] buffer, String startHeader, String endHeader)
	{
		return extractAll(buffer, startHeader.getBytes(StandardCharsets.UTF_8), endHeader.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Extracts from the buffer all the data segments found between occurrences of the start header and end header.
	 * Each segment of data extracted includes both the start and end headers.
	 * 
	 * @param buffer
	 * - buffer from which the data would be extracted.
	 * @param startHeader
	 * - The starting header.
	 * @param endHeader
	 * - The ending header.
	 * @return
	 * - A list containing segments of data found between the headers, including the headers.
	 * @author Sergio Morel
	 */
	public static List<byte[]> extractAll(byte[] buffer, byte[] startHeader, byte[] endHeader)
	{
		CircularByteBuffer circularByteBuffer = new CircularByteBuffer(buffer);
		
		return circularByteBuffer.extractAll(startHeader, endHeader);
	}
}