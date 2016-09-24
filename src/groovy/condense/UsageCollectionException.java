package condense;

public class UsageCollectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5774742572112063208L;
	
	public UsageCollectionException() {}

    //Constructor that accepts a message
    public UsageCollectionException(String message)
    {
       super(message);
    }

}
