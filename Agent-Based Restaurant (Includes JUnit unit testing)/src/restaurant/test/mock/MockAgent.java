package restaurant.test.mock;

/**
 * This is the base class for a mock agent. It only defines that an agent should
 * contain a name.
 * 
 */
public class MockAgent {
	private String name;

	public MockAgent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return this.getClass().getName() + ": " + name;
	}

}