package co.il.nmh.image.lock.gui.enums;

/**
 * @author Maor Hamami
 */

public enum ToolEnum
{
	LOCK("Lock"), UNLOCK("Unlock");

	private String value;

	private ToolEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
}
