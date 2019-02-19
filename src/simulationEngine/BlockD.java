package simulationEngine;

public class BlockD extends Block {

	private String id;
	private int base;
	private String text;

	public BlockD(String id, int width, int base) {
		this.base = base;
		this.id = id;
		terminals.put(id, new Terminal(width, false, this));
	}

	@Override
	public void calculate() throws Exception {
		try {
			Terminal t = terminals.get(id);
			if (base == 2) {
				text = t.getSignal().toString();
			}
			if (base == 16) {
				text = t.getSignal().getHexString();
			}
			if (base == 10) {
				text = t.getSignal().getDecString();
			}
		} catch (Exception e) {
			err = true;
			throw e;
		}
	}

	@Override
	public void initialize() throws Exception {
		calculate();
	}

	@Override
	public String getLabel() {
		return text;
	}

}
