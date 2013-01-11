package factoryInterfaces;



/** For the FCS: Each agent should be able to be initialized.
 * I think further versions require agents to pause and resume as a 
 * result of the FCS as well, so just implement this, and we'll update
 * it as additional capabilities are intreduced.
 * 
 * Basically, IMPLEMENT THIS IF YOU'RE AN AGENT
 * @author Fintan O'Grady
 *
 */
public interface FactoryAgent {
	public void msgInitialize();
}
