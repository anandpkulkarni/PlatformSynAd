package models.exception;

public class SynAdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SynAdException(String message)
	{
		super(message);
	}
	
	public SynAdException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
