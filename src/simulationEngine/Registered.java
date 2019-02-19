package simulationEngine;

public interface Registered {
	/**
	 * The method that updates the register contents.
	 * It should change the output of the block, invoking calculate() if required.
	 * @throws Exception
	 */
	public void updateRegister() throws Exception;
}
